/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.*;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {
	
	static String type = null;
    static int PORT = 0;
    static int poolSize = 0;
    
    public static void main(String[] args) throws Exception {
    	/** 
    	 * @author sgzhang
    	 * initialize all parameters
    	 */
    	type = args[0];
    	PORT = Integer.parseInt(args[1]);
    	poolSize = Integer.parseInt(args[2]);
    	if (type.equals(null) || PORT==0 || poolSize==0){
    		System.out.println("wrong parameters");
    		return;
    	}
  
        // Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(poolSize);
		
	//	EventLoopGroup bossGroup = new EpollEventLoopGroup(1);
	//	EventLoopGroup workerGroup = new EpollEventLoopGroup(poolSize);

        try {
            ServerBootstrap b = new ServerBootstrap();
			//b.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(100, 200));
			b.childOption(ChannelOption.SO_SNDBUF, 1024*Integer.parseInt(args[3]));
			b.option(ChannelOption.SO_SNDBUF, 1024*Integer.parseInt(args[3]));
			b.childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(100));
			//b.childOption(ChannelOption.TCP_NODELAY, true); 
			b.group(bossGroup, workerGroup)
		//	b.group(bossGroup, bossGroup)
			.channel(NioServerSocketChannel.class)
       //    .channel(EpollServerSocketChannel.class)
//             .option(ChannelOption.SO_BACKLOG, 100)
            // .option(ChannelOption.SO_SNDBUF, 1024*Integer.parseInt(args[3]))
            // .option(ChannelOption.SO_RCVBUF, 1024)
			//.option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(100*1024, 101*1024))
			// .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 100*1024)
		    // .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 16*1024)
			 
			 //.option(ChannelOption.TCP_NODELAY, true)
//             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     p.addLast(new LineBasedFrameDecoder(1024));
                     p.addLast(new StringDecoder());
                     p.addLast(new EchoServerHandler());
                 }
             });
			
			// Print option settings
			//System.out.println(b.config().childOptions().get(ChannelOption.WRITE_BUFFER_WATER_MARK));
            System.out.println(b.config().childOptions().get(ChannelOption.SO_SNDBUF));


            // Start the server.
            ChannelFuture f = b.bind("192.168.10.3", PORT).sync();
           // ChannelFuture f = b.bind("localhost", PORT).sync();
            
            /**
             * @author sgzhang
             * log server starts
             */
            System.out.println("simple netty server starts listening ["+PORT
            		+"] with worker threads ["+poolSize+"]");
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
