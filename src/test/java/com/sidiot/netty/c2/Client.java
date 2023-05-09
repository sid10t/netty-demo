package com.sidiot.netty.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 7999));
        SocketAddress address = sc.getLocalAddress();
        System.out.println("waiting...");
    }
}
