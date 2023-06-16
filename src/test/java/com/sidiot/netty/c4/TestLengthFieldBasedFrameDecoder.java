package com.sidiot.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFieldBasedFrameDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024, 0, 2, 0, 0),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{0x00, 0x0c});
        buffer.writeBytes("HELLO, WORLD".getBytes());
        channel.writeInbound(buffer);

        System.out.println("\n\n\n\n\n\n\n\n");
    }

    private static void send(ByteBuf buf, String content) {
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        buf.writeInt(length);
        buf.writeBytes(bytes);
    }
}
