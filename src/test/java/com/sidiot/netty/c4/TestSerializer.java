package com.sidiot.netty.c4;

import com.sidiot.message.LoginRequestMessage;
import com.sidiot.protocol.MessageCodecSerializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class TestSerializer {
    public static void main(String[] args) {
        MessageCodecSerializer CODEC = new MessageCodecSerializer();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);

        LoginRequestMessage message = new LoginRequestMessage("sidiot", "123456");
        channel.writeOutbound(message);
    }
}
