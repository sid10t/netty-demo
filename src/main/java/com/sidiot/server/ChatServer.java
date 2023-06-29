package com.sidiot.server;

import com.sidiot.protocol.MessageCodecSharable;
import com.sidiot.protocol.ProtocolFrameDecoder;
import com.sidiot.server.handler.*;
import com.sidiot.server.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天--服务端
 * @author sidiot
 */
@Slf4j
public class ChatServer {
    public static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    public static final MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
    public static final LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
    public static final ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
    public static final GroupCreateRequestMessageHandler GROUP_CREATE_HANDLER = new GroupCreateRequestMessageHandler();
    public static final GroupChatRequestMessageHandler GROUP_CHAT_HANDLER = new GroupChatRequestMessageHandler();
    public static final GroupMembersRequestMessageHandler GROUP_MEMBERS_HANDLER = new GroupMembersRequestMessageHandler();
    public static final GroupJoinRequestMessageHandler GROUP_JOIN_HANDLER = new GroupJoinRequestMessageHandler();
    public static final GroupQuitRequestMessageHandler GROUP_QUIT_HANDLER = new GroupQuitRequestMessageHandler();
    public static final QuitHandler QUIT_HANDLER = new QuitHandler();

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bs = new ServerBootstrap();
            bs.channel(NioServerSocketChannel.class);
            bs.group(boss, worker);
            bs.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(60, 0, 0));
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.READER_IDLE) {
                                String name = SessionFactory.getSession().getName(ctx.channel());
                                log.debug("已有五秒未读入用户 {} 的数据！", name);
                            }
                        }
                    });
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(LOGIN_HANDLER);
                    ch.pipeline().addLast(CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_CREATE_HANDLER);
                    ch.pipeline().addLast(GROUP_CHAT_HANDLER);
                    ch.pipeline().addLast(GROUP_MEMBERS_HANDLER);
                    ch.pipeline().addLast(GROUP_JOIN_HANDLER);
                    ch.pipeline().addLast(GROUP_QUIT_HANDLER);
                    ch.pipeline().addLast(QUIT_HANDLER);
                }
            });
            ChannelFuture channelFuture = bs.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
