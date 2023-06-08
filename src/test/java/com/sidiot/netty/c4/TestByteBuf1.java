package com.sidiot.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static com.sidiot.netty.c4.TestByteBuf.log;

public class TestByteBuf1 {
    public static void main(String[] args) {
        testZeroCopy();
    }

    public static void testCreateByteBuf() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(16);
        System.out.println(buf.getClass());

        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.heapBuffer(16);
        System.out.println(heapBuf.getClass());

        ByteBuf directBuf = ByteBufAllocator.DEFAULT.directBuffer(16);
        System.out.println(directBuf.getClass());
    }

    public static void testDynamicExpansion() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(6);
        log(buf);
        buf.writeLong(7);
        log(buf);
        buf.writeBytes("Hello, World! --sidiot.".getBytes());
        log(buf);
        buf.writeBytes("Hello, World! --sidiot.".getBytes());
        log(buf);
        buf.writeBytes("Hello, World! --".getBytes());
        log(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            sb.append('a');
        }
        buf.writeBytes(sb.toString().getBytes());
        log(buf);
    }

    public static void testRelease() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(16);
        System.out.println("ByteBuf 的引用计数为 " + buf.refCnt());
        buf.writeBytes("sidiot.".getBytes());
        log(buf);
        buf.release();
        log(buf);
    }

    public static void testRetain() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(16);
        System.out.println("ByteBuf 的引用计数为 " + buf.refCnt());
        buf.writeBytes("sidiot.".getBytes());
        log(buf);
        buf.retain();
        buf.retain();
        System.out.println("ByteBuf 的引用计数为 " + buf.refCnt());
        log(buf);
    }

    public static void testZeroCopy() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer(16);
        buf.writeBytes("sidiot.".getBytes());
        log(buf);

        System.out.println("\n<===== 测试 slice 方法 ======>\n");
        ByteBuf s1 = buf.slice(0, 1);
        ByteBuf s2 = buf.slice(1, 5);
        s1.retain();
        System.out.println("=====> s1 <======");
        log(s1);
        s2.retain();
        System.out.println("\n=====> s2 <======");
        log(s2);

        System.out.println("\n<===== 修改原始 ByteBuf 中的值 ======>\n");
        buf.setBytes(3, new byte[]{'1' , '0'});
        System.out.println("=====> buf <======");
        log(buf);
        System.out.println("\n=====> s2 <======");
        log(s2);

        System.out.println("\n<===== 测试 CompositeByteBuf 类 ======>\n");
        CompositeByteBuf cBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        cBuf.addComponents(true, s1, s2, s1);
        System.out.println("=====> cBuf <======");
        log(cBuf);

        System.out.println("buf 的引用计数为 " + buf.refCnt());
        System.out.println("cBuf 的引用计数为 " + cBuf.refCnt());
        cBuf.release();
        System.out.println("cBuf 的引用计数为 " + cBuf.refCnt());
        System.out.println("buf 的引用计数为 " + buf.refCnt());
    }
}
