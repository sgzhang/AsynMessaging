package com.sgzhang.test;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.*;

public class testServer implements Runnable {
	private static Logger log = LogManager.getLogger(testServer.class);
	private boolean isRunning = false;
	private ServerSocketChannel serverSocketChannel = null;
	private BlockingQueue<SelectionKey> qChannels;
	private Selector selector = null;
	
	private List<PollerEvent> q = new LinkedList<>();
	
	private Poller poller = null;
	
	public Poller getPoller() {return this.poller;}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (isRunning) {
			try {
				int cnt = this.selector.select();
				if (cnt == 0) {
					continue;
				}
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if (!key.isValid()) continue;
					
					if (key.isAcceptable()) {
						synchronized (qChannels) {
							log.info("acceptable key");
							qChannels.offer(key);
							qChannels.notify();
						}
					}
					if (key.isReadable()) {
						log.info("readable key");
//						q.add(new PollerEvent(key, this, PollerEvent.READ));
						SocketChannel channel = (SocketChannel) key.channel();
						ByteBuffer buffer = ByteBuffer.allocate(1024);
						int cnt1 = -1;
						cnt1 = channel.read(buffer);
						if (cnt1 == -1) {
							Socket socket = channel.socket();
							SocketAddress socketAddress = socket.getRemoteSocketAddress();
							System.out.println("Connection closed by client: "+socketAddress);
							channel.close();
							key.cancel();
							return;
						}
						byte[] data = new byte[cnt1];
						System.arraycopy(buffer.array(),0, data, 0, cnt1);
						System.out.println("Got: "+new String(data));
						// wrap a read event
					} else if (key.isWritable()) {
						log.info("writable key");
						q.add(new PollerEvent(key, this, PollerEvent.WRITE));
						// wrap a write event
					}
				}
//				selectedKeys.clear();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
		}
	}
	
	public testServer() throws Exception {
		this.selector = Selector.open();
		serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress("localhost", 9090);
		serverSocketChannel.bind(address);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
		poller = new Poller(this);
		this.isRunning = true;
		new Thread(new Acceptor(this.serverSocketChannel, this.selector), "acpt").start();
//		new Thread(poller, "polr").start();
	}
	
	/*
	 * Acceptor thread
	 */
	class Acceptor implements Runnable {
		private ServerSocketChannel serverSocketChannel;
		private Selector selector;
		
		public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
			this.serverSocketChannel = serverSocketChannel;
			this.selector = selector;
		}
		
		@Override
		public void run(){
			log.info("acceptor starts...");
			while (isRunning) {
				SocketChannel socketChannel = null;
				synchronized (qChannels) {
					if (qChannels.isEmpty()) {
						try {
							qChannels.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					SelectionKey key = qChannels.poll();
					if (key != null && !key.channel().isRegistered()) {
						try {
							ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
							socketChannel = serverSocketChannel.accept();
							socketChannel.configureBlocking(false);
							Socket socket = socketChannel.socket();
							log.info("remote address: "+socket.getRemoteSocketAddress());
							socketChannel.register(selector, SelectionKey.OP_READ);
							log.info("channle isRegistered: "+socketChannel.isRegistered());
							selector.wakeup();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	/*
	 * Poller thread
	 */
	public class Poller implements Runnable{
		private testServer tServer;
		private Selector selector;
		
		public Poller(testServer tServer) throws IOException {
			this.tServer = tServer;
			if (this.tServer != null)
				this.selector = this.tServer.selector;
		}
		
		public void register(SocketChannel socketChannel) {
			try {
				log.info("registering");
				socketChannel.configureBlocking(false);
				SelectionKey selectionKey0 = socketChannel.register(this.selector, 0);
				selectionKey0.interestOps(SelectionKey.OP_READ);
				this.selector.wakeup();
				log.info("registered as OP_READ");
			} catch (ClosedChannelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void run() {
			log.info("poller starts...");
			while (true) {
				try {
					int selectCount = this.selector.select();
					log.info("select count: "+selectCount);
					Iterator<SelectionKey> selelctedKeys = this.selector.selectedKeys().iterator();
					while (selelctedKeys.hasNext()) {
						SelectionKey key = (SelectionKey) selelctedKeys.next();
						selelctedKeys.remove();
						System.out.println("select");
						if(!key.isValid()) {
							log.info("key is not valid");
							continue;
						}
			
						if (key.isReadable()) {
							log.info("readable");
							SocketChannel channel = (SocketChannel) key.channel();
							ByteBuffer buffer = ByteBuffer.allocate(1024);
							int cnt = -1;
							cnt = channel.read(buffer);
							if (cnt == -1) {
								Socket socket = channel.socket();
								SocketAddress socketAddress = socket.getRemoteSocketAddress();
								System.out.println("Connection closed by client: "+socketAddress);
								channel.close();
								key.cancel();
								return;
							}
							byte[] data = new byte[cnt];
							System.arraycopy(buffer.array(),0, data, 0, cnt);
							System.out.println("Got: "+new String(data));
							key.interestOps(SelectionKey.OP_WRITE);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new Thread(new testServer(), "main").start();
			log.info("testServer Start...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

