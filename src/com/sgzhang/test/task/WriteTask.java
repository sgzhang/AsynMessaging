package com.sgzhang.test.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Random;

import com.sgzhang.test.Server;
import com.sgzhang.util.Count;

public class WriteTask extends AbstractTask {
	public final SelectionKey selectionKey;
	private final Server server;
	
	public WriteTask (SelectionKey selectionKey, Server server) {
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
		try {
		//	System.out.println("write performs by "+Thread.currentThread().getName());
			int size = 1;
			int[] array = Count.insertionSort(Count.randomizeArray(size));

			//			String out = RANDOM.nextInt(1000)+"y"+"\n";
		//	String outStr = server.test;
			String outStr = Count.getString(Count.LENGTH/2);
			outStr += outStr;
		//	outStr += outStr;
			String out = array[0]+outStr+"\n";
			ByteBuffer output = ByteBuffer.allocate(out.length());
			// output.clear();
			output.put(out.getBytes());
			output.flip();

			while (output.hasRemaining()) {
				socketChannel.write(output);
			}
			selectionKey.interestOps(SelectionKey.OP_READ);
			selectionKey.selector().wakeup();
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
