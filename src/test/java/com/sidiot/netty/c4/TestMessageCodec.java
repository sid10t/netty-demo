package com.sidiot.netty.c4;

import com.sidiot.message.LoginRequestMessage;
import com.sidiot.protocol.MessageCodec;
import com.sidiot.protocol.MessageCodecSharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LoggingHandler(),
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new MessageCodecSharable()
        );

        // encode
        LoginRequestMessage msg = new LoginRequestMessage("sidiot", "123456");
        channel.writeOutbound(msg);

        // decode
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, msg, buf);

        // 入站
        channel.writeInbound(buf);

        // 半包现象
        ByteBuf s1 = buf.slice(0, 121);
        channel.writeInbound(s1);
    }
}
