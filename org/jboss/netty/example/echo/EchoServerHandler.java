package org.jboss.netty.example.echo;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.example.utils.Count;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 * Handler implementation for the echo server.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 *
 * @version $Rev: 2121 $, $Date: 2010-02-02 09:38:07 +0900 (Tue, 02 Feb 2010) $
 */
public class EchoServerHandler extends SimpleChannelUpstreamHandler {
	private static final Logger logger = Logger.getLogger(
			EchoServerHandler.class.getName());
	private final AtomicLong transferredBytes = new AtomicLong();		
	public long getTransferredBytes() {return transferredBytes.get();}
	
	 @Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		/**
		 * original example
		 */
		// Send back the received message to the remote peer.
		//        transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
		//        e.getChannel().write(e.getMessage());
		/**
	     * @author sgzhang
	     */
	    String in = (String) e.getMessage();
	    //        System.out.println(in);
		
		int size = 100;
		int[] array = Count.insertionSort(Count.randomizeArray(size));

		if (in != null) {
			ChannelBuffer out = ChannelBuffers.wrappedBuffer((Count.getString(10) + "\n").getBytes());
			e.getChannel().write(out);
		}
	}
	@Override
	public void exceptionCaught(
		ChannelHandlerContext ctx, ExceptionEvent e) {
		// Close the connection when an exception is raised.
		logger.log(
				Level.WARNING,
				"Unexpected exception from downstream.",
				e.getCause());
				e.getChannel().close();
		}
}

