package com.sidiot.server.handler;

import com.sidiot.message.GroupCreateResponseMessage;
import com.sidiot.message.GroupJoinRequestMessage;
import com.sidiot.message.GroupJoinResponseMessage;
import com.sidiot.server.session.Group;
import com.sidiot.server.session.GroupSession;
import com.sidiot.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * 加入群聊---管理器
 * @author sidiot
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.joinMember(groupName, username);
        if(group != null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, String.format("成功加入群聊 [%s]", groupName)));
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels){
                channel.writeAndFlush(new GroupCreateResponseMessage(true, String.format("用户 %s 加入群聊 [%s]", username, groupName)));
            }
        } else{
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, String.format("加入失败，不存在群聊 [%s]", groupName)));
        }
    }
}
