package com.sgzhang.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.StandardSocketOptions;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sgzhang.test.task.ReadTask;
import com.sgzhang.test.task.WriteTask;

public class Server {
	private final int port;
	private final String hostName;
	//	private final ThreadPool threadPool;
	private Selector selector;
	private final int selectorSlavesCnt = 1;
	private List<SelectorProcessor> selectorSlaves = new ArrayList<>(selectorSlavesCnt);
	private ExecutorService eService = Executors.newFixedThreadPool(8);
	//private final static Logger LOGGER = LogManager.getLogger(Server.class);
	
	public Server(int port, int threadPoolSize) {
		this.port = port;
		//	this.threadPool = new ThreadPool(threadPoolSize);
		this.hostName = "192.168.10.3";
	}
	
	public boolean initilize() throws IOException {
		this.selector = Selector.open();
		for (int i = 0; i < selectorSlavesCnt; i++) {
			selectorSlaves.add(new SelectorProcessor(this, eService));
		}

		return true;
		//	return threadPool.initilize();
	}
	
	public static void main (String[] args) {
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
				server.startBlocking();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.exit(-1);
		}
	}

	private void startBlocking() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(true);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(this.hostName, this.port));

		System.out.println("server started with blocking mode...");
		
		int i = 25;
		boolean myswitch = true;
		while (true) {
			if (myswitch && i-- == 0) {
				for (int j = 0; j < selectorSlavesCnt; j++)
					new Thread(selectorSlaves.get(j).selectorProcessorRunnable, "selector-runnable-"+j).start();
				myswitch = false;
			}
			SocketChannel socketChannel = serverSocketChannel.accept();
			socketChannel.configureBlocking(false);
			selectorSlaves.get(Math.abs(socketChannel.getRemoteAddress().hashCode()) % selectorSlaves.size()).registerChannels(socketChannel, SelectionKey.OP_READ);

		}
	}

	private void start() throws IOException {
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
		ServerSocket serverSocket = serverSocketChannel.socket();
		serverSocket.bind(new InetSocketAddress(this.hostName, this.port));
		serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
		//LOGGER.info("server started...");
		System.out.println("server started...");
		//	JobQ jobQ = JobQ.getInstance();
		
		while (true) {
			int cnt = selector.select();
		//	System.out.println("select "+cnt);
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
						socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 1024*8);
						if (socketChannel != null) {
							socketChannel.configureBlocking(false);
						//	socketChannel.register(this.selector, SelectionKey.OP_READ);
							selectorSlaves.get(socketChannel.getRemoteAddress().hashCode() % selectorSlaves.size()).registerChannels(socketChannel, SelectionKey.OP_READ);
//							LOGGER.info("accepting...");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
 

	class SelectorProcessor {
		private Server server = null;
		private Selector selector = null;
		private ExecutorService executorService = null;
		private SelectorProcessorRunnable selectorProcessorRunnable = null;

		SelectorProcessor(final Server server, final ExecutorService executorService) {
			getSelector();
			this.server = server;
			this.executorService = executorService;
			this.selectorProcessorRunnable = new SelectorProcessorRunnable(server, selector);
		}

		void wakeup() {
			selector.wakeup();
		}

		void registerChannels(final SocketChannel sc, final int sk) {
			try {
				if (sc != null) {
					sc.register(this.selector, sk);
				}
			} catch (ClosedChannelException cce) {
			System.err.println("channel register error -> " + cce.getCause());
			}
		}
																
		private void getSelector() {
			if (selector == null) {
				try {
					selector = Selector.open();
				} catch (IOException ioe) {
					System.err.println("cannot open selector -> " + ioe.getCause());
				}
			}
		}
	}
	
	class SelectorProcessorRunnable implements Runnable {
		private final Server server;
		private final Selector selector;
		SelectorProcessorRunnable (final Server server, final Selector selector) {
			this.server = server;
			this.selector = selector;
		}

		@Override
		public void run() {
			try {
				while (true) {
					int cnt = selector.select();
				//	System.out.println("cnt " + cnt);
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while (it.hasNext()) {
						SelectionKey key = it.next();
						it.remove();
						if (!key.isValid()) {
							key.cancel();
							continue;
						}
						if (key.isReadable()) {
							ReadTask readTask = new ReadTask(key, selector);
							server.eService.execute(readTask);
						} else if (key.isWritable()) {
							WriteTask writeTask = new WriteTask(key, selector);
							server.eService.execute(writeTask);
						}
					}
				}
			} catch (IOException ioe) {
				System.err.println("selector selects error -> " + ioe.getCause());
			}
		}
	}
}
