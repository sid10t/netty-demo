package com.sidiot.netty.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.sidiot.netty.c1.ByteBufferUtil.debugRead;

@Slf4j
public class BlockServer {
    public static void main(String[] args) throws IOException {
        // 使用 nio 来理解阻塞模式，单线程

        // 1. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 2. 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 3. 绑定监听端口
        ssc.bind(new InetSocketAddress(7999));

        // 4. 创建连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // 5. accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
            log.debug("connecting...");
            SocketChannel sc = ssc.accept();
            log.debug("connected... {}", sc);
            channels.add(sc);
            for (SocketChannel channel : channels) {
                // 6. 接收客户端发送的数据
                log.debug("before read... {}", channel);
                channel.read(buffer);
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read... {}", channel);
            }
        }
    }
}
