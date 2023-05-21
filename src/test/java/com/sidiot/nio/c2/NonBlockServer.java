package com.sidiot.nio.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import static com.sidiot.nio.c1.ByteBufferUtil.debugRead;

@Slf4j
public class NonBlockServer {
    public static void main(String[] args) throws IOException {
        // 使用 nio 来理解阻塞模式，单线程

        // 1. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 2. 创建服务器
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            // 设置为非阻塞模式，没有连接时返回 null，不会阻塞线程
            ssc.configureBlocking(false);

            // 3. 绑定监听端口
            ssc.bind(new InetSocketAddress(7999));

            // 4. 创建连接集合
            ArrayList<SocketChannel> channels = new ArrayList<>();
            while (true) {
                // 5. accept 建立与客户端连接，SocketChannel 用来与客户端之间通信
                SocketChannel sc = ssc.accept();
                if (sc != null) {
                    log.debug("connected... {}", sc);
                    sc.configureBlocking(false);
                    channels.add(sc);
                }

                for (SocketChannel channel : channels) {
                    // 6. 接收客户端发送的数据
                    int read = channel.read(buffer);
                    if (read > 0){
                        buffer.flip();
                        debugRead(buffer);
                        buffer.clear();
                        log.debug("after read... {}", channel);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
