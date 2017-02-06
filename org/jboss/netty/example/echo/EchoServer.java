package org.jboss.netty.example.echo;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LineBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringDecoder;

/**
 * Echoes back any received data from a client.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 *
 */
public class EchoServer {
	static String type = null;
	static int PORT = 0;
	static int poolSize = 0;

	public static void main(String[] args) throws Exception {
        // args
		type = args[0];
		PORT = Integer.parseInt(args[1]);
		poolSize = Integer.parseInt(args[2]);
		if (type.equals(null) || PORT == 0 || poolSize == 0) {
			System.out.println("wrong parameters");
			return;
		}
		// Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
					Executors.newSingleThreadExecutor(),
					Executors.newFixedThreadPool(poolSize)));
					// Set up the pipeline factory.
					bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
						public ChannelPipeline getPipeline() throws Exception {
							ChannelPipeline p = Channels.pipeline();
							p.addLast("getLine", new LineBasedFrameDecoder(1024));
							p.addLast("string", new StringDecoder());
							p.addLast("handler", new EchoServerHandler());
							return p;
						}
					});
					
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress("192.168.10.3",PORT));
		System.out.println("simple netty3 server starts listening [" + PORT
				+ "] with worker threads [" + poolSize + "]");
	}
}
