package com.sidiot.nio.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import static com.sidiot.nio.c1.ByteBufferUtil.debugRead;

@Slf4j
public class SelectorTest {
    public static void main(String[] args) {
        try {
            // 1. 创建选择器来管理多个 channel
            Selector selector = Selector.open();

            ServerSocketChannel ssc = ServerSocketChannel.open();
            // 通道必须设置为非阻塞模式
            ssc.configureBlocking(false);

            // 2. 注册 selector 和 channel 的联系
            SelectionKey sscKey = ssc.register(selector, 0, null);
            sscKey.interestOps(SelectionKey.OP_ACCEPT);
            log.debug("Register Key: {}", sscKey);

            ssc.bind(new InetSocketAddress(7999));

            while (true) {
                // 3. 在没有事件发生时，线程阻塞；反之，则线程恢复运行
                selector.select();

                // 4. 处理事件，SelectionKey 内部包含了所有发生的事件
                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iter = keySet.iterator();
                log.debug("count: {}", keySet.size());

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    log.debug("Selection Key: {}", key);

                    // 5. 区分事件类型
                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        sc.register(selector, SelectionKey.OP_READ);
                        log.debug("sc Key: {}", sc);
                        iter.remove();
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4);

                        try {
                            int read = channel.read(buffer);
                            if (read == -1) {
                                key.cancel();
                                channel.close();
                            } else {
                                buffer.flip();
                                debugRead(buffer);
                                buffer.clear();
                            }
                            iter.remove();
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                            channel.close();
                            iter.remove();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
