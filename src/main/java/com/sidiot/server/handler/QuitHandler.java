package com.sidiot.server.handler;

import com.sidiot.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 退出---处理器
 * 只关心异常事件和 ChannelInActive 事件
 * @author sidiot
 */
@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {

    /**
     * 正常断开
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 主动断开了连接", ctx.channel());
    }

    /**
     * 异常断开
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug("{} 发生异常断开连接，异常是 {}", ctx.channel(), cause.getMessage());
    }

    /**
     * 建立连接
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.debug("======================= handlerAdded =======================");
    }

    /**
     * 断开连接
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.debug("======================= handlerRemoved =======================");
    }
}
