package com.shun.liu.shunzhitianxia.client.object;

import com.shun.liu.shunzhitianxia.bean.ClientRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

public final class ObjectEchoClient {
    static final boolean SSL = System.getProperty("ssl") != null;

    public static void main(String[] args) throws Exception {
        ObjectEchoClient objectEchoClient = new ObjectEchoClient();
        objectEchoClient.connect("127.0.0.1",18800,new ClientRequest());

    }

    public void connect(final String host, final int port,final ClientRequest clientRequest) throws SSLException, InterruptedException {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    if (sslCtx != null) {
                        p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                    }
                    p.addLast(
                            new ObjectEncoder(),
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            new ObjectEchoClientHandler(clientRequest));
                }
             });

            // Start the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
