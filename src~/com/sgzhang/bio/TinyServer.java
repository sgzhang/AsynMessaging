package com.sgzhang.bio;

import com.sgzhang.util.HTTPResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TinyServer implements Runnable {
	private InetSocketAddress address;
	private ServerSocket serverSocket;
	private ExecutorService executorService;
	private int THREAD_POOL_SIZE;
	public TinyServer(InetSocketAddress address) throws IOException {
		// TODO Auto-generated constructor stub
		this.address = address;
		serverSocket = new ServerSocket(address.getPort());
	}

	public void setTHREAD_POOL_SIZE(int num) {
		this.THREAD_POOL_SIZE = num;
	}

	public int getTHREAD_POOL_SIZE() {
		return THREAD_POOL_SIZE;
	}
	
	public void run() {
		executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		while(!Thread.interrupted()) {
			try {
				Socket socket = serverSocket.accept();
				executorService.submit(new Handler(socket));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	class Handler implements Runnable {
		private Socket socket;
		public Handler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
			// Open connections to the socket
		      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		      PrintStream out = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
		      // Read filename from first input line "GET /filename.html ..."
		      // or if not in this format, treat as a file not found.
		      long start = System.currentTimeMillis();
//		      String s=in.readLine();
//		      // Attempt to serve the file.  Catch FileNotFoundException and
//		      // return an HTTP error "404 Not Found".  Treat invalid requests
//		      // the same way.
//		      String filename="";
//		      StringTokenizer st=new StringTokenizer(s);
		      try {
//
//		        // Parse the filename from the GET command
//		        if (st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET")
//		            && st.hasMoreElements())
//		          filename=st.nextToken();
//		        else
//		          throw new FileNotFoundException();  // Bad request
//
//		        // Append trailing "/" with "index.html"
//		        if (filename.endsWith("/"))
//		          filename+="index.html";
//
//		        // Remove leading / from filename
//		        while (filename.indexOf("/")==0)
//		          filename=filename.substring(1);
//
//		        // Replace "/" with "\" in path for PC-based servers
//		        filename=filename.replace('/', File.separator.charAt(0));
//
//		        // Check for illegal characters to prevent access to superdirectories
//		        if (filename.indexOf("..")>=0 || filename.indexOf(':')>=0
//		            || filename.indexOf('|')>=0)
//		          throw new FileNotFoundException();
//
//		        // If a directory is requested and the trailing / is missing,
//		        // send the client an HTTP request to append it.  (This is
//		        // necessary for relative links to work correctly in the client).
//		        if (new File(filename).isDirectory()) {
//		          filename=filename.replace('\\', '/');
//		          out.print("HTTP/1.0 301 Moved Permanently\r\n"+
//		            "Location: /"+filename+"/\r\n\r\n");
//		          out.close();
//		          return;
//		        }
//
//		        // Open the file (may throw FileNotFoundException)
//		        @SuppressWarnings("resource")
//				InputStream f=new FileInputStream(filename);
//
//		        // Determine the MIME type and print HTTP header
//		        String mimeType="text/plain";
//		        if (filename.endsWith(".html") || filename.endsWith(".htm"))
//		          mimeType="text/html";
//		        else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg"))
//		          mimeType="image/jpeg";
//		        else if (filename.endsWith(".gif"))
//		          mimeType="image/gif";
//		        else if (filename.endsWith(".class"))
//		          mimeType="application/octet-stream";
//		        out.print("HTTP/1.0 200 OK\r\n"+
//		          "Content-type: "+mimeType+"\r\n\r\n");

		        // Send file contents to client, then close the connection
				  StringBuilder stringBuilder = new StringBuilder();
				  HTTPResponse response = new HTTPResponse();
				  response.setContent("I like cats".getBytes());
				  response.addDefaultHeaders();
				  stringBuilder.append(response.getVersion() + " " + response.getResponseCode() + " " + response.getResponseReason() + "\r\n");
				  for (Map.Entry<String, String> header : response.getHeader().entrySet()) {
					  stringBuilder.append(header.getKey() + ": " + header.getValue() + "\r\n");
				  }
				  stringBuilder.append("\r\n");
				  InputStream f = new ByteArrayInputStream(stringBuilder.toString().getBytes());
				  byte[] a=new byte[4096];
				  int n;
				  while ((n=f.read(a))>0) {
					  out.write(a, 0, n);
				  }
				  out.write(response.getContent());
				  out.close();
		      }
		      catch (FileNotFoundException x) {
//		        out.println("HTTP/1.0 404 Not Found\r\n"+
//		          "Content-type: text/html\r\n\r\n"+
//		          "<html><head></head><body>"+filename+" not found</body></html>\n");
//		        out.close();
		      }
//		      System.out.println(s+" "+start+" "+System.currentTimeMillis());  // Log the request
		    }
		    catch (IOException x) {
		      System.out.println(x);
		    }
		}
	}
}
