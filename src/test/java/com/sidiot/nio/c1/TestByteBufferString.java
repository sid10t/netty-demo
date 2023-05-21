package com.sidiot.nio.c1;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.sidiot.nio.c1.ByteBufferUtil.debugAll;
public class TestByteBufferString {

    @Test
    public void testGetBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put("sidiot".getBytes());
        debugAll(buffer);
    }

    @Test
    public void testCharset() {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode("sidiot");
        debugAll(buffer);

        System.out.println(StandardCharsets.UTF_8.decode(buffer));
    }

    @Test
    public void testWrap() {
        ByteBuffer buffer = ByteBuffer.wrap("sidiot".getBytes());
        debugAll(buffer);
    }
}
