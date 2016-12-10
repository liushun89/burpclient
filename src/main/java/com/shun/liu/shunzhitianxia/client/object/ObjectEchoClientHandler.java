package com.shun.liu.shunzhitianxia.client.object;

import com.shun.liu.shunzhitianxia.bean.ClientRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ObjectEchoClientHandler extends ChannelInboundHandlerAdapter {
    private ClientRequest clientRequest;


    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler(ClientRequest clientRequest) {
        this.clientRequest = clientRequest;
    }

    /**
     * Creates a client-side handler.
     */
    public ObjectEchoClientHandler() {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(clientRequest);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
