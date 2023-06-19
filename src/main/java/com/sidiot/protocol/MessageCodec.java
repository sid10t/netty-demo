package com.sidiot.protocol;

import com.sidiot.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author sidiot
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // 设置魔数
        out.writeBytes("IDIOT".getBytes());
        // 设置版本号
        out.writeByte(1);
        // 设置序列化方式
        out.writeByte(1);
        // 设置指令类型
        out.writeByte(msg.getMessageType());
        // 设置请求序号
        out.writeInt(msg.getSequenceId());

        // 获得序列化后的 msg
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();

        // 获得并设置正文长度
        out.writeInt(bytes.length);
        // 设置消息正文
        out.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 获取魔数
        byte[] magic = new byte[5];
        in.readBytes(magic, 0, 5);
        // 获取版本号
        byte version = in.readByte();
        // 获得序列化方式
        byte seqType = in.readByte();
        // 获得指令类型
        byte messageType = in.readByte();
        // 获得请求序号
        int sequenceId = in.readInt();
        // 获得正文长度
        int length = in.readInt();
        // 获得正文
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message) ois.readObject();

        // 打印获得的信息正文
        log.debug("魔数: {}, 版本号: {}, 序列化方法: {}, 指令类型: {}, 请求序号: {}, 正文长度: {}",
                new String(magic), version, seqType, messageType, sequenceId, length);
        log.debug("正文: {}", message);

        // 将信息放入 List 中，传递给下一个 handler
        out.add(message);
    }
}
