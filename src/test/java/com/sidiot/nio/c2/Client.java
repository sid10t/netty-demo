package com.sidiot.nio.c2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 7999));
        SocketAddress address = sc.getLocalAddress();
        sc.write(Charset.defaultCharset().encode("Hello, World! --sid10t.\n"));
        System.out.println("waiting...");
    }
}
