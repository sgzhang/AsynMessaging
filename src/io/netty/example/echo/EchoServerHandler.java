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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import io.netty.example.utils.*;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	private int count = 0;
//	private String smallStr = Count.getString(Count.SMALL_LENGTH);
//	private String largeStr = Count.getString(Count.LARGE_LENGTH);
	
	/** random distribution of response size */
	java.util.Random random = new java.util.Random();
	
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	String inString = (String) msg;
		
		/** no decoder */
		try {
    		/**
    		 * @author sgzhang
    		 * print requests
    		 */
//    		System.out.println(in+"::["+(count++)+"]");
    		// System.out.println(ctx.channel().id());
    	
			int size = 100;
			int[] array = Count.insertionSort(Count.randomizeArray(size));
			
//			/** write a file to disk */
//			String f = "io/"+ctx.channel().id()+".txt";
//			try {
//				java.io.PrintWriter writer = new java.io.PrintWriter(f, "UTF-8");
//				java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss");
//				java.util.Date dateobj = new java.util.Date();
//				writer.println(dateobj);
//				for(int i=0;i<1000;i++) {
//					writer.println(random.nextInt(100));
//				}
//				writer.close();
//			} catch (java.io.IOException e) {
//				System.out.println("io wrong");
//			}
			
//			/** read a file from disk */
//			try {
//				java.io.File f = new java.io.File("/home/szhang/simple_server/data/"+random.nextInt(20480)+".txt");
//				java.io.RandomAccessFile raf = new java.io.RandomAccessFile(f,"rw");
//				raf.setLength(1024*1024);
//				raf.seek(1024);
//				//java.io.InputStream in = new java.io.FileInputStream(f);
//				int tmp, nc=0;
//				char c = (char)raf.read();
//				while ((tmp = raf.read()) != -1 && nc++ < 10240) {
//					c = (char)tmp;
//				}
//			//	System.out.println(c);
//				raf.close();
//			} catch (Exception e) {
//				System.out.println("io read error");
//				e.printStackTrace();
//			}

			/** baseline */
	   		if (!inString.equals("")) {
				//System.out.println(in);
				String outStr = Count.getString(Count.LENGTH);
			//	outStr += outStr;
				String str = array[0]+outStr+"\n";
	    		ByteBuf out = io.netty.buffer.Unpooled.wrappedBuffer((str).getBytes());

				/** mixed response size */
			//	ByteBuf out = null;
			//	if (random.nextInt(100) < Count.RATIO) {
			//		out = io.netty.buffer.Unpooled.wrappedBuffer((array[0]+Count.getString(Count.LARGE_LENGTH)+"\n").getBytes());
				//	out = io.netty.buffer.Unpooled.wrappedBuffer((array[0]+largeStr+"\n").getBytes());
			//	} else {
			//		out = io.netty.buffer.Unpooled.wrappedBuffer((array[0]+Count.getString(Count.SMALL_LENGTH)+"\n").getBytes());
				//	out = io.netty.buffer.Unpooled.wrappedBuffer((array[0]+smallStr+"\n").getBytes());
			//	}

				//Count.increment();
				//System.out.println(Count.getCount());
				ctx.write(out);
    		}
		
			/** encoding disable */
//    		if (((ByteBuf) msg).writerIndex() > 0) {
//				((ByteBuf) msg).clear();
//	    		ByteBuf out = io.netty.buffer.Unpooled.wrappedBuffer((array[0]+Count.getString()+"\n").getBytes());
//				//Count.increment();
//				//System.out.println(Count.getCount());
//				ctx.write(out);
//    		}
    	} finally {
//    		ReferenceCountUtil.release(msg);
    	}
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
       /**
		* ignore
		*/
		//cause.printStackTrace();
        ctx.close();
    }
}
