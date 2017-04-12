package com.hello;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloServer {
    public static void main(String args[]) throws Exception{
        /*Netty的boss是一个线程组。实际上Netty的ServerBootstrap可以监听多个端口号，
         * 如果只监听一个端口号，那么只需要一个boss线程即可，
         * 推荐将bossGroup的线程数量设置成1*/
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        /*worker线程组处理IO请求*/
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        /*
         * NIO的编程模型是Socket绑定到端口上，得到一个channel，然后将channel注册到selector上，最后不断的遍历selector
         * 
         * netty也一样要按照上面的思路来处理
         * 其中channel有自己的扩展，并增强了功能，提供诸如pipeline的模式来处理消息
         * 而selector则放在了EventLoop中
         * 
         * netty增强的channel都会注册到某个EventLoopGroup中，Group会选择一个EventLoop注册进去
         */
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>(){
            /*
             * 注册channel的时候会调用initChannel方法
             */
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>(){
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
                        System.out.println("Server收到:"+msg);
                        ctx.writeAndFlush("Server已经收到消息了"+"\r\n");
                    }
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("客户端连接："+ctx.channel().remoteAddress());
                        ctx.writeAndFlush("Welcome to Service!"+"\r\n");
                        super.channelActive(ctx);
                    }
                });
            }
        });
        
        ChannelFuture f = b.bind(8080).sync();
        
        f.channel().closeFuture().sync();
        
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    
    }
}
