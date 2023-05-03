package com.sidiot.netty.c1;

import org.junit.Test;

import java.nio.ByteBuffer;

import static com.sidiot.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferRead {

    @Test
    public void testRewind() {
        // rewind 从头开始读
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        buffer.flip();
        buffer.get(new byte[6]);
        debugAll(buffer);
        buffer.rewind();
        System.out.println((char) buffer.get());
    }

    @Test
    public void testMarkAndReset() {
        // mark 做一个标记，用于记录 position 的位置；reset 是将 position 重置到 mark 的位置；
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        buffer.flip();
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        buffer.mark();      // 添加标记为索引2的位置;
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
        debugAll(buffer);
        buffer.reset();     // 将 position 重置到索引2;
        debugAll(buffer);
        System.out.println((char) buffer.get());
        System.out.println((char) buffer.get());
    }

    @Test
    public void testGet_i() {
        // get(i) 不会改变读索引的位置；
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(new byte[]{'s', 'i', 'd', 'i', 'o', 't'});
        buffer.flip();
        System.out.println((char) buffer.get(2));
        debugAll(buffer);
    }

}
