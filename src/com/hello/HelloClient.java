package com.hello;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloClient {
    public static void main(String args[]) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap strap = new Bootstrap();
        strap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringEncoder());
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<String>(){
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
                            System.out.println("Client收到:"+msg);
                            ctx.writeAndFlush("Client已经收到消息了"+"\r\n");
                            Thread.sleep(1000);
                        }
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("Client Active::::"+ctx.channel().remoteAddress());
                            ctx.writeAndFlush("Welcome to Client!"+"\r\n");
                            Thread.sleep(1000);
                            super.channelActive(ctx);
                        }
                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("Client InActive::::"+ctx.channel().remoteAddress());
//                          ctx.fireChannelInactive();
                        }
                    });
                }});
        
        Channel ch = strap.connect("localhost",8080).sync().channel();
        ch.writeAndFlush("Hello Service"+"\r\n");
    }
}
