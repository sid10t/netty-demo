package com.sidiot.netty.c2;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import static com.sidiot.netty.c1.ByteBufferUtil.debugAll;
import static com.sidiot.netty.c1.ByteBufferUtil.debugRead;

@Slf4j
public class MSGBoundary {

    public static void main(String[] args) {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);

            SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT);
            log.debug("Register Key: {}", sscKey);

            ssc.bind(new InetSocketAddress(7999));

            while (true) {
                selector.select();

                Set<SelectionKey> keySet = selector.selectedKeys();
                Iterator<SelectionKey> iter = keySet.iterator();
//                log.debug("count: {}", keySet.size());

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    log.debug("Selection Key: {}", key);

                    if (key.isAcceptable()) {
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel sc = channel.accept();
                        sc.configureBlocking(false);
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        sc.register(selector, SelectionKey.OP_READ, buffer);
                        log.debug("sc Key: {}", sc);
                        iter.remove();
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buf = (ByteBuffer) key.attachment();

                        try {
                            int read = channel.read(buf);
                            log.debug("read: {}", read);
                            if (read <= 0) {
                                key.cancel();
                                channel.close();
                            } else {
                                split(buf);
                                if (buf.position() == buf.limit()) {
                                    ByteBuffer newBuf = ByteBuffer.allocate(buf.capacity() * 2);
                                    buf.flip();
                                    newBuf.put(buf);
                                    key.attach(newBuf);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            key.cancel();
                        } finally {
                            iter.remove();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for(int i=0; i<buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int length = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for(int j=0; j<length; j++) {
                    target.put(buffer.get());
                }
                debugAll(target);
            }
        }
        buffer.compact();
    }
}
