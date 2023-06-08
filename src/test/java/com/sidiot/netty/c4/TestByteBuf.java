package com.sidiot.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        log(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("sidiot");
        }
        buf.writeBytes(sb.toString().getBytes());
        log(buf);
    }

    public static void testWriteBoolean(ByteBuf buf) {
        buf.writeBoolean(true);
        buf.writeBoolean(true);
        buf.writeBoolean(false);
        log(buf);
    }

    public static void testWriteBytes(ByteBuf buf) {
        buf.writeBytes(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        log(buf);
    }

    public static void testWriteInt(ByteBuf buf) {
        buf.writeInt(1314);
        log(buf);
    }

    public static void testWriteIntLE(ByteBuf buf) {
        buf.writeIntLE(1314);
        log(buf);
    }

    public static void testReadableBytes(ByteBuf buf) {
        buf.writeBytes(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        log(buf);
        System.out.println("当前可读取的字节数为" + buf.readableBytes());
        buf.readByte();
        log(buf);
        System.out.println("当前可读取的字节数为" + buf.readableBytes());
    }

    public static void testReadBytes(ByteBuf buf) {
        buf.writeBytes(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        byte[] bytes = new byte[3];
        buf.readBytes(bytes);
        System.out.println(new String(bytes));
        log(buf);
    }

    public static void testReadByte(ByteBuf buf) {
        buf.writeBytes(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        System.out.println((char)buf.readByte());
        System.out.println((char)buf.readByte());
        log(buf);
    }

    public static void testReadInt(ByteBuf buf) {
        buf.writeInt(6);
        buf.writeInt(5);
        buf.writeInt(4);
        System.out.println(buf.readInt());
    }

    public static void testReadRepeat(ByteBuf buf) {
        buf.writeBytes(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        log(buf);
        buf.markReaderIndex();
        System.out.println((char)buf.readByte());
        System.out.println((char)buf.readByte());
        log(buf);
        System.out.println("resetReaderIndex");
        buf.resetReaderIndex();
        log(buf);
    }

    public static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
