package com.sidiot.server.handler;

import com.sidiot.message.ChatRequestMessage;
import com.sidiot.message.ChatResponseMessage;
import com.sidiot.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author sidiot
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());
        // 在线
        if (channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        // 不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false, String.format("用户 %s 不存在或离线，消息发送失败", msg.getTo())));
        }
    }
}