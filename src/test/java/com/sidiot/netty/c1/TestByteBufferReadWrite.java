package com.sidiot.netty.c1;

import java.nio.ByteBuffer;

import static com.sidiot.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 写入一个字节的数据
        buffer.put((byte) 0x73);
        debugAll(buffer);

        // 写入一组五个字节的数据
        buffer.put(new byte[]{0x69, 0x64, 0x69, 0x6f, 0x74});
        debugAll(buffer);

        // 获取数据
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        ByteBufferUtil.debugAll(buffer);

        // 使用 compact 切换写模式
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);

        // 再次写入
        buffer.put((byte) 102);
        buffer.put((byte) 103);
        ByteBufferUtil.debugAll(buffer);
    }
}
