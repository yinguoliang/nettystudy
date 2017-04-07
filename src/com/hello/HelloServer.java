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
        /*Netty��boss��һ���߳��顣ʵ����Netty��ServerBootstrap���Լ�������˿ںţ�
         * ���ֻ����һ���˿ںţ���ôֻ��Ҫһ��boss�̼߳��ɣ�
         * �Ƽ���bossGroup���߳��������ó�1*/
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        /*worker�߳��鴦��IO����*/
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChannelInitializer<SocketChannel>(){
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new DelimiterBasedFrameDecoder(8192,Delimiters.lineDelimiter()));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new SimpleChannelInboundHandler<String>(){
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx,String msg) throws Exception {
                        System.out.println("Server�յ�:"+msg);
                        ctx.writeAndFlush("Server�Ѿ��յ���Ϣ��"+"\r\n");
                    }
                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("�ͻ������ӣ�"+ctx.channel().remoteAddress());
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
