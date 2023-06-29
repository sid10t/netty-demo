package com.sidiot.client;

import com.sidiot.message.*;
import com.sidiot.protocol.MessageCodecSharable;
import com.sidiot.protocol.ProtocolFrameDecoder;
import com.sidiot.server.session.SessionFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author sidiot
 */
@Slf4j
public class ChatClient {
    public static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    public static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(new IdleStateHandler(0, 30, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.WRITER_IDLE) {
                                ctx.writeAndFlush(new PingMessage());
                                log.debug("发送心跳包");
                            }
                        }
                    });
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 负责接收用户在控制台的输入，负责向服务器发送各种消息
                            new Thread(() -> {
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名：");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码：");
                                String password = scanner.nextLine();
                                // 构造消息对象
                                LoginRequestMessage msg = new LoginRequestMessage(username, password);
                                // 发送消息
                                ctx.writeAndFlush(msg);

                                System.out.println("正在登录中...");
                                // 阻塞直到登陆成功后 CountDownLatch 被设置为 0
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // 执行后续操作
                                if (!LOGIN.get()) {
                                    // 登陆失败，关闭 channel 并返回
                                    ctx.channel().close();
                                    return;
                                }

                                // 登录成功后，执行其他操作
//                                menu();
                                while (true) {
                                    String command = scanner.nextLine();
                                    // 获得指令及其参数，并发送对应类型消息
                                    String[] commands = command.split("\\$");
                                    switch (commands[0]){
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, commands[1], commands[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username,commands[1], commands[2]));
                                            break;
                                        case "gcreate":
                                            // 分割，获得群员名
                                            String[] members = commands[2].split(",");
                                            Set<String> set = new HashSet<>(Arrays.asList(members));
                                            // 把自己加入到群聊中
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(commands[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(commands[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, commands[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, commands[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                        default:
                                            System.out.println("指令有误，请重新输入");
                                            continue;
                                    }
                                }
                            }, "system in").start();
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                            log.debug("ReadMSG: {}", msg);
//                            System.out.println("验证信息中...");
                            if (msg instanceof LoginResponseMessage) {
                                // 如果是登录响应信息
                                LoginResponseMessage message = (LoginResponseMessage) msg;
                                boolean isSuccess = message.isSuccess();
                                // 登录成功，设置登陆标记
                                if (isSuccess) {
                                    LOGIN.set(true);
                                }
                                System.out.println(message.getReason());
                                // 登陆后，唤醒登陆线程
                                WAIT_FOR_LOGIN.countDown();
                            } else if (msg instanceof ChatResponseMessage) {
                                ChatResponseMessage message = (ChatResponseMessage) msg;
                                if (message.getContent() != null) {
                                    System.out.println(message.getFrom() + " : " + message.getContent());
                                } else {
                                    System.out.println("Warning : " + message.getReason());
                                }
                            } else if (msg instanceof GroupChatResponseMessage) {
                                GroupChatResponseMessage message = (GroupChatResponseMessage) msg;
                                if (message.getContent() != null) {
                                    System.out.printf("[%s] %s : %s\n", message.getGroupName(), message.getFrom(), message.getContent());
                                }
                            } else if (msg instanceof AbstractResponseMessage) {
                                AbstractResponseMessage message = (AbstractResponseMessage) msg;
                                System.out.println(message.getReason());
                            }
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error", e);
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    private static void menu() {
        System.out.println("==================================");
        System.out.println("send [username] [content]");
        System.out.println("gsend [group name] [content]");
        System.out.println("gcreate [group name] [m1,m2,m3...]");
        System.out.println("gmembers [group name]");
        System.out.println("gjoin [group name]");
        System.out.println("gquit [group name]");
        System.out.println("quit");
        System.out.println("==================================");
    }
}
