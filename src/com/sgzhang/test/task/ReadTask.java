package com.sgzhang.test.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.sgzhang.test.Server;
import com.sgzhang.util.Count;

public class ReadTask extends AbstractTask {
	private final SelectionKey selectionKey;
	private final Server server;
	private ByteBuffer input = ByteBuffer.allocate(64);

	public ReadTask(SelectionKey selectionKey, Server server) {
		super(selectionKey, server);
		this.selectionKey = selectionKey;
		this.server = server;
		this.selectionKey.interestOps(this.selectionKey.interestOps() & (~this.selectionKey.readyOps()));
	}

	@Override
	public void run() { complete(); }
	@Override
	public void complete() {
		// TODO Auto-generated method stub
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
	//	System.out.println("read performs by "+Thread.currentThread().getName());
		try {
			int readCount = socketChannel.read(input);
			if (readCount == -1) {
				socketChannel.close();
				selectionKey.cancel();
				return;
			}
		//	input.flip();
		//	byte[] array = new byte[readCount];
		//	System.arraycopy(input.array(),0,array,0,readCount);
		//	StringBuilder sBuilder = new StringBuilder();
			input.flip();
			byte[] subStringBytes = new byte[readCount];
			byte[] array = input.array();
			System.arraycopy(array, 0, subStringBytes, 0, readCount);
		//	sBuilder.append(new String(subStringBytes));
			String str = new String(subStringBytes);
			input.clear();
			
			/** write task */
			// System.out.println("write performs by "+Thread.currentThread().getName());
			int size = 100;
			int[] arrayWrite = Count.insertionSort(Count.randomizeArray(size));

			//			String out = RANDOM.nextInt(1000)+"y"+"\n";
			//	String outStr = server.test;
			String outStr = Count.getString(Count.LENGTH);
		//	outStr += outStr;
			// outStr += outStr;
			String out = arrayWrite[0]+outStr+"\n";
			ByteBuffer output = ByteBuffer.allocate(out.length());
			// output.clear();
			output.put(out.getBytes());
			output.flip();

			while (output.hasRemaining()) {
				socketChannel.write(output);
			//	System.out.println("write bytes ["+socketChannel.write(output)+"]");
			}
			selectionKey.interestOps(SelectionKey.OP_READ);
			selectionKey.selector().wakeup();


		//	selectionKey.interestOps(SelectionKey.OP_WRITE);
		//	selectionKey.selector().wakeup();
		} catch (IOException e) {
			try {
				socketChannel.close();
				selectionKey.cancel();
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

}
