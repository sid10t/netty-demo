package com.sidiot.server.handler;

import com.sidiot.message.GroupJoinResponseMessage;
import com.sidiot.message.GroupMembersRequestMessage;
import com.sidiot.server.session.GroupSession;
import com.sidiot.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Arrays;
import java.util.Set;

/**
 * 查看成员---管理器
 * @author sidiot
 */
@ChannelHandler.Sharable
public class GroupMembersRequestMessageHandler extends SimpleChannelInboundHandler<GroupMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMembersRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Set<String> members = groupSession.getMembers(groupName);
        if(members != null){
            ctx.writeAndFlush(new GroupJoinResponseMessage(true, String.format("群聊成员：%s", Arrays.toString(members.toArray()))));
        } else{
            ctx.writeAndFlush(new GroupJoinResponseMessage(false, String.format("查看失败，不存在群聊 [%s]", groupName)));
        }
    }
}
