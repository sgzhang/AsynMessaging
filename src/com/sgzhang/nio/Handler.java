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

// java log
//import java.util.*;  
//import java.io.*;  
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

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
	Long st = 0L, et = 0L, selectTimestamp = 0L;

	/** random distribution of response size */
	final java.util.Random random = new java.util.Random();

	/** read and write count */
//	int readCounter = 0;
	int writeCounter = 0;
	String test = Count.getString(Count.LENGTH);
//	String smallStr = Count.getString(Count.SMALL_LENGTH);
//	String largeStr = Count.getString(Count.LARGE_LENGTH);

//	Logger log = LogManager.getLogger(Handler.class);

    Handler(Selector selector, SocketChannel c, int sndbuf) throws IOException {
		// java log
//		Properties props = new Properties();
//		props.load(new FileInputStream("/home/szhang/simple_server/src/log4j.properties"));
//		PropertyConfigurator.configure(props);

		socketChannel = c;
		socketChannel.setOption(StandardSocketOptions.SO_SNDBUF, sndbuf*1024);
//		System.out.println(socketChannel.getOption(StandardSocketOptions.SO_SNDBUF));
        c.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }


	public void setSelectTimestamp(final Long selectTimestamp) {
		this.selectTimestamp = selectTimestamp;
	}

	public Long getSelectTimeStamp() {
		return selectTimestamp;
	}

    public void run() {
        try {
            if (state == READING) {
				st = this.getSelectTimeStamp();
                read();
            } else if (state == SENDING) {
                send();
				et = System.currentTimeMillis();
			//	if (st > 0 && (et-st) > -1)
			//		System.out.println(st+","+et+",StoriesOfTheDay,"+(et-st));
            }
        } catch (IOException ex) {
			System.out.println("******1000001 error here");
            ex.printStackTrace();
        }
    }

    void read() throws IOException {
        try {
            int readCount = socketChannel.read(input);
            if (readCount > 0) {
                readProcess(readCount);
            }
//            System.out.println(clientName);
			
			/** read and write operations in one event */
		    state = SENDING;
            // Interested in writing
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
			System.out.println("******1000001 error here");
            socketChannel.close();
        }
    }

    /**
     * Processing of the read message. This only prints the message to stdOut.
     *
     * @param readCount
     */
    synchronized void readProcess(int readCount) {
        input.flip();
        // byte[] subStringBytes = new byte[readCount];
        // pooled bytebuffer get array
		byte[] array = input.array();
        // System.arraycopy(array, 0, subStringBytes, 0, readCount);
        // Assuming ASCII (bad assumption but simplifies the example)
//        sb.append(new String(subStringBytes));
        input.clear();
		//clientName = new String(subStringBytes);
		clientName = new String(array);
		//System.out.println(clientName);
		/** write operations */
	//	int size = 300;
	//	int[] array1 = Count.insertionSort(Count.randomizeArray(size));
	//	String outStr = Count.getString(Count.LENGTH/2);
	//	outStr += outStr;
	//	outStr += outStr;
	//	String str = array1[0]+outStr+"\n";
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
	//	ByteBuffer output = ByteBuffer.allocate(str.length());
	//	output.clear();
	//	output.put((str).getBytes());
	//	output.flip();
	//	int c = 0;
	//	try {
	//		while(output.hasRemaining()) {
		//	if (c > 0) {
		//		System.out.println("bingo ["+c+"]");
		//	}
		//	c++;
		//	System.out.print(System.currentTimeMillis());
		//	System.out.print("#");
		//	socketChannel.write(output);
		//	System.out.println(socketChannel.write(output));
		//	System.out.print(System.currentTimeMillis());
		//	System.out.println("");
			
			//	System.out.println(count++);
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
       
//    }

    void send() throws IOException {
	//	System.out.println("write count ["+(writeCounter++)+"]");
        try {
        	/** Original Reactor Pattern code */
//			ByteBuffer output = ByteBuffer.wrap(("Saying Hello to " + clientName + "\n").getBytes());
			
			/** test */
//			System.out.println("clientName");
			/** buffer size */
	//		System.out.println("rcvbuf: "+socketChannel.getOption(StandardSocketOptions.SO_RCVBUF));
	//		System.out.println("sndbuf: "+socketChannel.getOption(StandardSocketOptions.SO_SNDBUF));

			int size = 100;
			int[] array = Count.insertionSort(Count.randomizeArray(size));
			//String str = array[0]+Count.getString(Count.LENGTH)+"\n";

			String outStr = Count.getString(Count.LENGTH/2);
			//String outStr = Count.strL;
			outStr += outStr;
			String out = array[0]+outStr+"\n";

			/** mixed response size */
	//		String out = null;
	//		ByteBuffer output = null;
	//		if (random.nextInt(100) < Count.RATIO) {
	//			int size = 100;
	//			int[] array = Count.insertionSort(Count.randomizeArray(size));
	//		//	String outStr = Count.strL;
	//			String outStr = Count.getString(Count.LARGE_LENGTH);
	//			out = array[0]+outStr+"\n";
	//			output = ByteBuffer.allocate(out.length());
	//			output.clear();
	//			output.put(out.getBytes());
	//			output.flip();
	//		} else {
	//			int size = 100;
	//			int[] array = Count.insertionSort(Count.randomizeArray(size));
	//			String outStr = Count.strS;
	//		//	String outStr = Count.getString(Count.SMALL_LENGTH);
	//			out = array[0]+outStr+"\n";
	//			output = ByteBuffer.allocate(out.length());
	//		//	output = ByteBuffer.allocate(Count.smallStr.length);
	//			output.clear();
	//			output.put(out.getBytes());
	//		//	output.put(Count.smallStr);
	//			output.flip();
	//		}

			// http header
			StringBuilder header = new StringBuilder("HTTP/1.1 200 OK\r\nConnection: Keep-Alive\r\nContent-Length: ");
			header.append(out.length())
				.append("\r\n\r\n")
				.append(out);

			//System.out.println("*******00001 header->\n"+header.toString());
			/** native write */
			//ByteBuffer output = ByteBuffer.allocate(out.length());
			ByteBuffer output = ByteBuffer.allocate(header.length());
			//	ByteBuffer output = ByteBuffer.allocate(Count.largeStr.length);
			output.clear();
			//	output.put(Count.largeStr);
			//output.put(out.getBytes());
			output.put(header.toString().getBytes());
			output.flip();
			//ByteBuffer output = ByteBuffer.wrap(header.toString().getBytes());
	//		int count = 0;
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
