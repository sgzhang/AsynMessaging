package com.sgzhang.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sgzhang.test.task.ReadTask;
import com.sgzhang.test.task.WriteTask;

public class Server {
	private final int port;
	private final String hostName;
	private final ThreadPool threadPool;
	public Selector selector;
	private final static Logger LOGGER = LogManager.getLogger(Server.class);
	
//	private int sendBufferSize;
	
	public Server(int port, int threadPoolSize) {
		this.port = port;
		this.threadPool = new ThreadPool(threadPoolSize);
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			this.hostName = "localhost";
		} else {
			this.hostName = "192.168.10.3";
		}
	}
	
	public boolean initilize() throws IOException {
		this.selector = Selector.open();
		return threadPool.initilize();
	}
	
	public static void main (String[] args) {
		System.out.println("this is a demo server");
		String type = args[0];
		int port = Integer.parseInt(args[1]);
		int threadPoolSize = Integer.parseInt(args[2]);
		int sendBufferSize = Integer.parseInt(args[3]);
		
		if(type.equals("") || port == 0 || threadPoolSize == 0 || sendBufferSize == 0) {
			System.out.println("Missing required arguments");
			System.exit(-1);
		}
		
		Server server = new Server(port, threadPoolSize);
		boolean initilized = false;
		try {
			initilized = server.initilize();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (initilized) {
			try {
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.exit(-1);
		}
	}
	
	private void start() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(this.hostName, this.port));
		serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
		LOGGER.info("server started...");
		JobQ jobQ = JobQ.getInstance();
		
		while (true) {
			int cnt = selector.select(3);
			if (cnt == 0) continue;
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> it = selectedKeys.iterator();
			while (it.hasNext()) {
				SelectionKey key = it.next();
				it.remove();
				if (!key.isValid()) continue;
				if (key.isAcceptable()) {
					try {
						SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
						socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024*100);
						if (socketChannel != null) {
							socketChannel.configureBlocking(false);
							socketChannel.register(this.selector, SelectionKey.OP_READ);
//							LOGGER.info("accepting...");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (key.isReadable()) {
					ReadTask readTask = new ReadTask(key, this);
					jobQ.addJob(readTask);
					System.out.println("read");
//					jobQ.hasJob();
				} else if (key.isWritable()) {
					WriteTask writeTask = new WriteTask(key, this);
					jobQ.addJob(writeTask);
					System.out.println("write");
				}
//				LOGGER.error("job queue size: ["+jobQ.jobs.length()+"]");
			}
		}
	}
 
}
