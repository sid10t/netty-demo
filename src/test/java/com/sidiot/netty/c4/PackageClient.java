package com.sidiot.netty.c4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class PackageClient {
    static final Logger log = LoggerFactory.getLogger(PackageClient.class);
    public static void main(String[] args) {
        send();
    }

    public static StringBuilder makeString(char ch, int len) {
        StringBuilder sb = new StringBuilder(len + 2);
        for (int i = 0; i < len; i++) {
            sb.append(ch);
        }
        sb.append("\\s");
        return  sb;
    }

    private static void send() {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    log.debug("connected...");
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
/*
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("sending...");
                            // 每次发送16个字节的数据，共发送10次
                            for (int i = 0; i < 5; i++) {
                                ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes("sidiot.".getBytes());
                                ctx.writeAndFlush(buffer);
                            }

                            Thread.sleep(1000);
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("Next......".getBytes());
                            ctx.writeAndFlush(buffer);
                        }
*/

/*
                        // 短链接：每次发送完毕就断开连接
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            log.debug("sending...");
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("sidiot.".getBytes());
                            ctx.writeAndFlush(buffer);
                            ctx.channel().close();
                        }

*/

/*
                        // 定长解码器
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 每次发送16个字节的数据，共发送10次
                            for (int i = 0; i < 5; i++) {
                                ByteBuf buffer = ctx.alloc().buffer();
                                buffer.writeBytes("sidiot.".getBytes());
                                ctx.writeAndFlush(buffer);
                            }

                            Thread.sleep(1000);
                            ByteBuf buffer = ctx.alloc().buffer();
                            buffer.writeBytes("Next......".getBytes());
                            ctx.writeAndFlush(buffer);
                        }
*/

                        // 行解码器
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) {
                            ByteBuf buffer = ctx.alloc().buffer();
                            char ch = 's';
                            Random r = new Random();
                            for (int i = 0; i < 5; i++) {
                                StringBuilder sb = makeString(ch, r.nextInt(52)+1);
                                ch++;
                                buffer.writeBytes(sb.toString().getBytes());
                            }
                            ctx.writeAndFlush(buffer);
                        }

                    });
                }
            });
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8080).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("client error", e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}
