package com.sidiot.protocol;

import com.sidiot.config.AppConfig;
import com.sidiot.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author sidiot
 * 使用自定义 Serializer
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSerializer extends MessageToMessageCodec<ByteBuf, Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        out.writeBytes("IDIOT".getBytes());
        out.writeByte(1);
        out.writeByte(AppConfig.getSerializerAlgorithm().ordinal());
        out.writeByte(msg.getMessageType());
        out.writeInt(msg.getSequenceId());
        byte[] bytes = AppConfig.getSerializerAlgorithm().serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte[] magic = new byte[5];
        in.readBytes(magic, 0, 5);
        byte version = in.readByte();
        byte serializeAlgorithm = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializeAlgorithm];
        Class<?> messageClass = Message.getMessageClass(messageType);
        Object deserialize = algorithm.deserialize(messageClass, bytes);

        log.debug("魔数: {}, 版本号: {}, 序列化方法: {}, 指令类型: {}, 请求序号: {}, 正文长度: {}",
                new String(magic), version, serializeAlgorithm, messageType, sequenceId, length);
        log.debug("正文: {}", deserialize);

        out.add(deserialize);
    }
}
