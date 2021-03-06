package com.sgzhang.bio;

import com.sgzhang.util.HTTPResponse;
import com.sgzhang.util.Count;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TinyServer implements Runnable {
	private InetSocketAddress address;
	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private int THREAD_POOL_SIZE;
	int sndbuf;
	public TinyServer(InetSocketAddress address, int THREAD_POOL_SIZE, int sndbuf) throws IOException {
		// TODO Auto-generated constructor stub
		this.address = address;
		this.THREAD_POOL_SIZE = THREAD_POOL_SIZE;
		this.sndbuf = sndbuf;
		serverSocket = new ServerSocket(address.getPort());
	//	executorService = Executors.newFixedThreadPool(20);
	
//		executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//		System.out.println("Server listening to port: "+address.getPort());
	}

	public void setTHREAD_POOL_SIZE(int num) {
		this.THREAD_POOL_SIZE = num;
	}

	public int getTHREAD_POOL_SIZE() {
		return THREAD_POOL_SIZE;
	}
	
	public void run() {
		while(!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
			//	executorService.submit(new Handler(socket));
				new Thread(new Handler(socket)).start();				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class Handler implements Runnable {
		Count c = new Count();
		String test = Count.getString(Count.LENGTH);
		private Socket socket;
		public Handler(Socket socket) {
			this.socket = socket;
			try {
				this.socket.setSendBufferSize(1024*sndbuf);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
			// Open connections to the socket
		      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			  /** PrintStream method */
			  PrintStream out = new PrintStream(socket.getOutputStream());

//			  PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		      long start = System.currentTimeMillis();
		      String clientName;
	    	  while((clientName=in.readLine())!=null) {			 
	    		//  System.out.println(Thread.currentThread().getName());
				  
				  int size = 100;
				  int[] array = c.insertionSort(c.randomizeArray(size));

				  String outStr = c.getString(c.LENGTH/2);
				  outStr += outStr;
			//	  outStr += outStr;
				  String str = array[0]+outStr+"\n";
			//	  String str = array[0]+test+"\n";
	    	//	  InputStream f = new ByteArrayInputStream((str).getBytes());
			//	  byte[] a = new byte[str.length()];
			//	  int n;
			//	  while ((n=f.read(a))>0) {
			//		  out.write(a,0,n);
			//	  }
				  out.write(str.getBytes(),0,str.length());
				  out.flush();
			  }

//				  out.write(response.getContent());
			  in.close();
			  out.close();		    
			} catch (Exception x) {
//		      x.printStackTrace();
			  try {
				  socket.close();
			  } catch (IOException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
			}
		}
	}
}
