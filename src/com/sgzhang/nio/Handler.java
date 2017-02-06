package com.sgzhang.nio;

import com.sgzhang.util.Count;
import com.sgzhang.util.HTTPResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.GatheringByteChannel;
import java.net.StandardSocketOptions;
import java.util.Map;

public class Handler implements Runnable {

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
	ByteBuffer input = ByteBuffer.allocate(64);
//	ByteBuffer output = ByteBuffer.allocate(102402);
//	ByteBuffer input = ByteBuffer.allocateDirect(64);
    static final int READING = 0, SENDING = 1;
    int state = READING;
    //    public HTTPRequest request = null;
    String clientName = "";

	/** random distribution of response size */
	final java.util.Random random = new java.util.Random();

	/** read and write count */
//	int readCounter = 0;
	int writeCounter = 0;
	String test = Count.getString(Count.LENGTH);
//	String smallStr = Count.getString(Count.SMALL_LENGTH);
//	String largeStr = Count.getString(Count.LARGE_LENGTH);

    Handler(Selector selector, SocketChannel c, int sndbuf) throws IOException {
        socketChannel = c;
		socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, sndbuf*1024);
		System.out.println(socketChannel.getOption(StandardSocketOptions.SO_SNDBUF));
        c.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }


    public void run() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        try {
            int readCount = socketChannel.read(input);
            if (readCount > 0) {
			//	System.out.println("sndbuf: "+socketChannel.getOption(StandardSocketOptions.SO_SNDBUF));
                readProcess(readCount);
            }
//            System.out.println(clientName);
//            request = new HTTPRequest(clientName);
			
			/** read and write operations in one event */
//		    state = SENDING;
//            // Interested in writing
//            selectionKey.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            socketChannel.close();
        }
    }

    /**
     * Processing of the read message. This only prints the message to stdOut.
     *
     * @param readCount
     */
    synchronized void readProcess(int readCount) {
//    void readProcess(int readCount) {
//		System.out.println(input);
//        StringBuilder sb = new StringBuilder();
//		long a = System.currentTimeMillis();
        input.flip();
        byte[] subStringBytes = new byte[readCount];
        // pooled bytebuffer get array
		byte[] array = input.array();
		
//		System.out.println("read count ["+(readCounter++)+"]");

		// direct bytebuffer get array
		// byte[] array = new byte[input.remaining()];
		// input.get(array);
        System.arraycopy(array, 0, subStringBytes, 0, readCount);
        // Assuming ASCII (bad assumption but simplifies the example)
//        sb.append(new String(subStringBytes));
        input.clear();
		clientName = new String(subStringBytes);
		// System.out.println("clientName ["+clientName.substring(0,4)+"]");
//        clientName = sb.toString();
 
		// System.out.println(socketChannel.socket().toString());

//		/** write a file to disk */
//		String f = "io/t"+socketChannel.socket().getPort()+".txt";
//		try {
//			java.io.PrintWriter writer = new java.io.PrintWriter(f, "UTF-8");
//			java.text.DateFormat df = new java.text.SimpleDateFormat("dd/MM/yy HH:mm:ss");
//			java.util.Date dateobj = new java.util.Date();
//			writer.println(dateobj);
//			for(int i=0;i<10000;i++) {
//				writer.println(random.nextInt(100));
//			}
//			writer.close();
//		} catch (IOException e) {
//			System.out.println("io wrong");
//		}
	
//		/** read a file from disk */
//		try {
//			java.io.File f = new java.io.File("/home/szhang/simple_server/data/"+random.nextInt(20480)+".txt");
//			java.io.RandomAccessFile raf = new java.io.RandomAccessFile(f,"rw");
//			raf.setLength(1024*1024);
//			raf.seek(1024);
//		//	java.io.InputStream in = new java.io.FileInputStream(f);
//			int tmp, nc=0;
//			char c = (char)raf.read();
//			while ((tmp = raf.read()) != -1 && nc++ < 10240) {
//				c = (char)tmp;
//			}
//			// System.out.println(c);
//			raf.close();
//		} catch (Exception e) {
//			System.out.println("io read error");
//			e.printStackTrace();
//		}

		/** write operations */
		int size = 600;
		int[] array1 = Count.insertionSort(Count.randomizeArray(size));
		String outStr = Count.getString(Count.LENGTH/2);
		outStr += outStr;
	//	outStr += outStr;
		String str = array1[0]+outStr+"\n";
	//	String str = array1[0]+test+"\n";

		/** mixed response size */
	//	String str = null;
	//	if (random.nextInt(100) < Count.RATIO) {
	//		str = array[0]+Count.getString(Count.LARGE_LENGTH)+"\n";
	//	//	str = array[0]+largeStr+"\n";
	//	} else {
	//		str = array[0]+Count.getString(Count.SMALL_LENGTH)+"\n";
	//	//	str = array[0]+smallStr+"\n";
	//	}				

		/** native write */
		ByteBuffer output = ByteBuffer.allocate(str.length());
		output.clear();
		output.put((str).getBytes());
		output.flip();
		int c = 0;
		try {
			while(output.hasRemaining()) {
		//	if (c > 0) {
		//		System.out.println("bingo ["+c+"]");
		//	}
		//	c++;
		//	System.out.print(System.currentTimeMillis());
		//	System.out.print("#");
			socketChannel.write(output);
		//	System.out.println(socketChannel.write(output));
		//	System.out.print(System.currentTimeMillis());
		//	System.out.println("");
			
			//	System.out.println(count++);
		}

			/*************** simulate http 2.0 server push */
			/** write operations */		
/**			String str1 = Count.getString(Count.LENGTH)+"\n";
			ByteBuffer output1 = ByteBuffer.allocate(str1.length());
			output1.clear();
			output1.put((str1).getBytes());
			output1.flip();
			while(output1.hasRemaining()) {
		//	if (c > 0) {
		//		System.out.println("bingo ["+c+"]");
		//	}
		//	c++;
				System.out.println(socketChannel.write(output1));
			//	System.out.println(count++);
		}
		String str2 = Count.getString(Count.LENGTH)+"\n";
			ByteBuffer output2 = ByteBuffer.allocate(str2.length());
			output2.clear();
			output2.put((str1).getBytes());
			output2.flip();
			while(output2.hasRemaining()) {
		//	if (c > 0) {
		//		System.out.println("bingo ["+c+"]");
		//	}
		//	c++;
				System.out.println(socketChannel.write(output2));
			//	System.out.println(count++);
		}
		String str3 = Count.getString(Count.LENGTH)+"\n";
			ByteBuffer output3 = ByteBuffer.allocate(str3.length());
			output3.clear();
			output3.put((str1).getBytes());
			output3.flip();
			while(output3.hasRemaining()) {
		//	if (c > 0) {
		//		System.out.println("bingo ["+c+"]");
		//	}
		//	c++;
				System.out.println(socketChannel.write(output3));
			//	System.out.println(count++);
		}
*/



//		long b = System.currentTimeMillis();
//		System.out.println(b-a);

		} catch (Exception e) {
			try {
				socketChannel.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

		/** netty write */
//		try {
//			io.netty.buffer.ByteBuf out = io.netty.buffer.Unpooled.wrappedBuffer(str.getBytes());
//			int writeCount = 0;
//			while (writeCount < out.readableBytes()) {
//				writeCount += out.getBytes(out.readerIndex(), (GatheringByteChannel)socketChannel, out.readableBytes());
//			}
//		} catch (Exception e) {
//			try {
//				socketChannel.close();
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
		// System.out.println("write counter ["+writeCount+"]");

//		for (int i = 0; i < 100; i++) {
//			ByteBuffer output = ByteBuffer.wrap((Count.getString() + "\n").getBytes());
//			socketChannel.write(output);
//			System.out.println("limit-"+output.limit());
//			System.out.println("position-"+output.position());
//			output.clear();
//		}
//		selectionKey.interestOps(SelectionKey.OP_READ);
       
    }

    private void writeLine(String line) throws IOException {
        socketChannel.write(ByteBuffer.wrap((line + "\r\n").getBytes()));
    }

    void send() throws IOException {
		System.out.println("write count ["+(writeCounter++)+"]");
        try {
            /** HTTP Header */
//            HTTPResponse response = new HTTPResponse();
//            response.setContent("I like cats".getBytes());
//            response.addDefaultHeaders();
//            writeLine(response.getVersion() + " " + response.getResponseCode() + " " + response.getResponseReason());
//            for (Map.Entry<String, String> header : response.getHeader().entrySet()) {
//                writeLine(header.getKey() + ": " + header.getValue());
//            }
//            writeLine("");
//            socketChannel.write(ByteBuffer.wrap(response.getContent()));
        	/** Original Reactor Pattern code */
//			ByteBuffer output = ByteBuffer.wrap(("Saying Hello to " + clientName + "\n").getBytes());
			
			/** test */
//			System.out.println("clientName");
			/** buffer size */
	//		System.out.println("rcvbuf: "+socketChannel.getOption(StandardSocketOptions.SO_RCVBUF));
	//		System.out.println("sndbuf: "+socketChannel.getOption(StandardSocketOptions.SO_SNDBUF));

			int size = 100;
			int[] array = Count.insertionSort(Count.randomizeArray(size));
//			String str = array[0]+Count.getString(Count.LENGTH)+"\n";

			/** mixed response size */
			String str = null;
			if (random.nextInt(10) < Count.RATIO) {
				str = array[0]+Count.getString(Count.LARGE_LENGTH)+"\n";
			} else {
				str = array[0]+Count.getString(Count.SMALL_LENGTH)+"\n";
			}

			/** native write */
			ByteBuffer output = ByteBuffer.allocate(str.length());
			output.clear();
			output.put((str).getBytes());
			output.flip();
			int count = 0;
			while(output.hasRemaining()) {
				int i = socketChannel.write(output);
			//	System.out.println(count++);
			}

			/** netty write */
//			io.netty.buffer.ByteBuf out = io.netty.buffer.Unpooled.wrappedBuffer(str.getBytes());
//			int writeCount = 0;
//			while (writeCount < out.readableBytes()) {
//				writeCount += out.getBytes(out.readerIndex(), (GatheringByteChannel)socketChannel, out.readableBytes());
//			}
			// System.out.println("write counter ["+writeCount+"]");

//			for (int i = 0; i < 100; i++) {
//				ByteBuffer output = ByteBuffer.wrap((Count.getString() + "\n").getBytes());
//				socketChannel.write(output);
//				System.out.println("limit-"+output.limit());
//				System.out.println("position-"+output.position());
//				output.clear();
//			}
			selectionKey.interestOps(SelectionKey.OP_READ);
            state = READING;
        } catch (Exception e) {
            socketChannel.close();
        }
    }
}
