package com.sgzhang.test;

import java.io.IOException;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Handlers implements Runnable {
	private static Logger logger = LogManager.getLogger(Handlers.class);
	private List<PollerEvent> q;
	private ExecutorService executorService;
	private ByteBuffer input = ByteBuffer.allocate(1024);
	
	public Handlers(List<PollerEvent> q) {
		this.q = q;
		this.executorService = Executors.newFixedThreadPool(10);
	}
	
	@Override
	public void run() {
		
	}
	
	private synchronized void read(PollerEvent event) {
		SocketChannel socketChannel = (SocketChannel) event.selectionKey.channel();
		try {
			socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024*8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int read = socketChannel.read(input);
			if (read == -1) {
				socketChannel.close();
				event.selectionKey.cancel();
				return;
			}
			if (read > 0) {
//				StringBuilder sb = new StringBuilder();
		        input.flip();
		        byte[] subStringBytes = new byte[read];
		        byte[] array = input.array();
		        System.arraycopy(array, 0, subStringBytes, 0, read);
		        // Assuming ASCII (bad assumption but simplifies the example)
//		        sb.append(new String(subStringBytes));
		        String str = new String(subStringBytes);
		        input.clear();		    
			}
		} catch(IOException e) {
			event.selectionKey.cancel();
			logger.info(e.getMessage());
		} 
	}
	
	private synchronized void write(PollerEvent event) {
		SocketChannel socketChannel = (SocketChannel) event.selectionKey.channel();
	}
	
}
