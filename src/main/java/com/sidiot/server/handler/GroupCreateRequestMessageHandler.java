package com.sidiot.server.handler;

import com.sidiot.message.GroupCreateRequestMessage;
import com.sidiot.message.GroupCreateResponseMessage;
import com.sidiot.server.session.Group;
import com.sidiot.server.session.GroupSession;
import com.sidiot.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * 创建群聊---管理器
 * @author sidiot
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);

        if(group == null){
            // 发送创建成功消息
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, "成功创建群聊 " + groupName));

            // 发送成员受邀消息
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            for (Channel channel : channels){
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入群聊 " + groupName));
            }
        } else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, "创建失败，已存在群聊 " + groupName));
        }
    }
}
