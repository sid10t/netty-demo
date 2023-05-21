package com.sidiot.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {

    public static void main(String[] args) {

        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从 channel 读取数据写入到 buffer
                int len = channel.read(buffer);
                log.debug("读取到的字节数 {}", len);
                if (len == -1) break;

                // 打印 buffer 内容
                buffer.flip();  // 切换至读模式
                while(buffer.hasRemaining()) {  // 是否还有剩余未读数据
                    byte b = buffer.get();
                    log.debug("实际字节 {}", (char)b);
                }
                buffer.clear();
            }
        } catch (IOException e) {

        }

    }

}
