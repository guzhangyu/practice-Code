package com.test.netty.securechat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;

public class SecureChatServerHandler extends SimpleChannelInboundHandler<String> {

    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // once session is secured, send a greeting and register the channel to the global channel list
        // so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " secure chat service!\n");
                ctx.writeAndFlush("Your session is protected by " + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite.\n");

                channels.add(ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // send the received message to all channels but the current one
        for(Channel c: channels){
            String name = "you";
            if(c != ctx.channel()){
                name = ctx.channel().remoteAddress().toString();
            }
            c.writeAndFlush("[" + name + "] " + msg + "\n");
        }

        if("bye".equals(msg.toLowerCase())){
            ctx.close();
        }
    }
}
