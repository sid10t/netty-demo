package com.sidiot.server.handler;

import com.sidiot.message.GroupChatRequestMessage;
import com.sidiot.message.GroupChatResponseMessage;
import com.sidiot.server.session.GroupSession;
import com.sidiot.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 发送消息---管理器
 * @author sidiot
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        final GroupSession groupSession = GroupSessionFactory.getGroupSession();
        final List<Channel> channelList = groupSession.getMembersChannel(msg.getGroupName());

        for (Channel channel : channelList){
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getGroupName(), msg.getContent()));
        }
    }
}
