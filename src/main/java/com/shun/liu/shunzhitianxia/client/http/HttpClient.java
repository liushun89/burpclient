package com.shun.liu.shunzhitianxia.client.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

public final class HttpClient {

    public static HttpRequest getRequestMethod(Map<String, String> parameter, String url, String method) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        String path = uri.getRawPath();
        String host = uri.getHost();

        HttpRequest request = null;
        if ("post".equalsIgnoreCase(method)) {
            request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, path);

            HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
            // This encoder will help to encode Request for a FORM as POST.
            HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, false);
            // add Form attribute
            if (parameter != null) {
                Set<Map.Entry<String, String>> entrySet = parameter.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String key = e.getKey();
                    String value = e.getValue();
                    bodyRequestEncoder.addBodyAttribute(key, value);
                }
                try {
                    request = bodyRequestEncoder.finalizeRequest();
                } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
                    // if an encoding error occurs
                    e.printStackTrace();
                }
            }

            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            request.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, ((DefaultFullHttpRequest)request).content().readableBytes());
        } else if ("get".equalsIgnoreCase(method)) {
            //uri.toString()没有查询参数的uri
            QueryStringEncoder encoder = new QueryStringEncoder(uri.toString());
            if (parameter != null) {
                Set<Map.Entry<String, String>> entrySet = parameter.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String key = e.getKey();
                    String value = e.getValue();
                    encoder.addParam(key, value);
                }
            }
            //encoder.toString()有查询参数的uri
            request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaderNames.HOST, host);
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP.toString() + ','
                    + HttpHeaderValues.DEFLATE.toString());

            headers.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "fr");
            headers.set(HttpHeaderNames.USER_AGENT, "Netty Simple Http Client side");
            headers.set(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            headers.set(HttpHeaderNames.COOKIE,  ClientCookieEncoder.STRICT.encode(
                    new DefaultCookie("my-cookie", "foo"),
                    new DefaultCookie("another-cookie", "bar"))
            );
        } else {
//            System.err.println("this method is not support!");
        }
        return request;
    }

    public void run(String url, HttpRequest request) throws Exception {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
//            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpClientInitializer(sslCtx));


            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();
            // send request
            ch.writeAndFlush(request).sync();
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

}
