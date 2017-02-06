package com.simple.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server implements Runnable {

	public final static String ADDRESS = "192.168.10.3";
	public final static int PORT = 9000;
	public final static long TIMEOUT = 10000;
	
	private ServerSocketChannel serverChannel;
	private Selector selector;
	/**
	 * This hashmap is important. It keeps track of the data
	 */
	private Map<SocketChannel,byte[]> dataTracking = new HashMap<SocketChannel, byte[]>();

	public Server(){
		init();
	}

	private void init(){
		System.out.println("initializing server");
		// We do not want to call init() twice and recreate the selector or the serverChannel.
		if (selector != null) return;
		if (serverChannel != null) return;

		try {
			// This is how you open a Selector
			selector = Selector.open();
			// This is how you open a ServerSocketChannel
			serverChannel = ServerSocketChannel.open();
			System.out.println(serverChannel.supportedOptions());
			// You MUST configure as non-blocking or else you cannot register the serverChannel to the Selector.
			serverChannel.configureBlocking(false);
			// bind to the address that you will use to Serve.
			serverChannel.socket().bind(new InetSocketAddress(ADDRESS, PORT));

			/**
			 * Here you are registering the serverSocketChannel to accept connection, thus the OP_ACCEPT.
			 * This means that you just told your selector that this channel will be used to accept connections.
			 * We can change this operation later to read/write, more on this later.
			 */
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Now accepting connections...");
		try{
			// A run the server as long as the thread is not interrupted.
			while (!Thread.currentThread().isInterrupted()){
				/**
				 * selector.select(TIMEOUT) is waiting for an OPERATION to be ready and is a blocking call.
				 * For example, if a client connects right this second, then it will break from the select()
				 * call and run the code below it. The TIMEOUT is not needed, but its just so it doesn't 
				 * block undefinitely.
				 */
				selector.select(TIMEOUT);

				/**
				 * If we are here, it is because an operation happened (or the TIMEOUT expired).
				 * We need to get the SelectionKeys from the selector to see what operations are available.
				 * We use an iterator for this. 
				 */
				Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

				while (keys.hasNext()){
					SelectionKey key = keys.next();
					// remove the key so that we don't process this OPERATION again.
					keys.remove();

					// key could be invalid if for example, the client closed the connection.
					if (!key.isValid()){
						continue;
					}
					/**
					 * In the server, we start by listening to the OP_ACCEPT when we register with the Selector.
					 * If the key from the keyset is Acceptable, then we must get ready to accept the client
					 * connection and do something with it. Go read the comments in the accept method.
					 */
					if (key.isAcceptable()){
						System.out.println("Accepting connection @ "+System.currentTimeMillis());
						accept(key);
					}
					/**
					 * If you already read the comments in the accept method then you understand that 
					 */
					if (key.isReadable()){
						System.out.println("Reading connection");
						read(key);
					}
					if (key.isWritable()){
						System.out.println("Writing...");
						write(key);
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			closeConnection();
		}

	}

	private void write(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		// Configure send buffer as a specific size
	//	channel.socket().setSendBufferSize(1024*8);
	//	channel.socket().setReceiveBufferSize(43690);
		System.out.println("rcvbuf: "+channel.getOption(StandardSocketOptions.SO_RCVBUF));
	//	System.out.println("sndbuf: "+51200);
		System.out.println("sndbuf: "+channel.getOption(StandardSocketOptions.SO_SNDBUF));
		byte[] data = dataTracking.get(channel);
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		dataTracking.remove(channel);
		while (buffer.hasRemaining()) {
			System.out.println("sndbuf: "+channel.getOption(StandardSocketOptions.SO_SNDBUF));
			//System.out.printf("write %d @ %d%n",channel.write(buffer),System.currentTimeMillis());
			long a = System.currentTimeMillis();
			System.out.println("write "+channel.write(buffer)+" time "+(System.currentTimeMillis()-a));
		}
//		channel.write(ByteBuffer.wrap(data));
		
		key.interestOps(SelectionKey.OP_READ);
		
	}
	// Nothing special, just closing our selector and socket.
	private void closeConnection(){
		System.out.println("Closing server down");
		if (selector != null){
			try {
				selector.close();
				serverChannel.socket().close();
				serverChannel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Since we are accepting, we must instantiate a serverSocketChannel by calling key.channel().
	 * We use this in order to get a socketChannel (which is like a socket in I/O) by calling
	 *  serverSocketChannel.accept() and we register that channel to the selector to listen 
	 *  to a WRITE OPERATION. I do this because my server sends a hello message to each
	 *  client that connects to it. I then 
	 */
	private void accept(SelectionKey key) throws IOException{
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		
		socketChannel.register(selector, SelectionKey.OP_WRITE);
	
		/* Shungeng Zhang */
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 1; i < 1024*1024;i++)
			stringBuilder.append("x");
		stringBuilder.append("y");
		byte[] hello = new String(stringBuilder.toString()).getBytes("UTF-8");
		System.out.println(stringBuilder.toString().getBytes("UTF-8").length);
		
//		byte[] hello = new String("Hello from server").getBytes();
		dataTracking.put(socketChannel, hello);
	}

	private void read(SelectionKey key) throws IOException{
		SocketChannel channel = (SocketChannel) key.channel();
		ByteBuffer readBuffer = ByteBuffer.allocate(1024);
		readBuffer.clear();
		int read;
		try {
			read = channel.read(readBuffer);
		} catch (IOException e) {
			System.out.println("Reading problem, closing connection");
			key.cancel();
			channel.close();
			return;
		}
		if (read == -1){
			System.out.println("Nothing was there to be read, closing connection");
			channel.close();
			key.cancel();
			return;
		}
		readBuffer.flip();
		byte[] data = new byte[read];
		readBuffer.get(data, 0, read);
		System.out.println("Received: "+new String(data));

//		echo(key,data);
	}

	private void echo(SelectionKey key, byte[] data){
		SocketChannel socketChannel = (SocketChannel) key.channel();
		dataTracking.put(socketChannel, data);
		key.interestOps(SelectionKey.OP_WRITE);
	}

}
