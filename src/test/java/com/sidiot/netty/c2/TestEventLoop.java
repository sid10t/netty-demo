package com.sidiot.netty.c2;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(2);

        group.next().submit(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug(Thread.currentThread().getName());
        });

        group.next().scheduleAtFixedRate(() -> {
            log.debug("sidiot.");
        }, 0, 1, TimeUnit.SECONDS);

        log.debug(Thread.currentThread().getName());

        group.shutdownGracefully();
    }
}
