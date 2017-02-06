package com.sgzhang.test;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Poller implements Runnable{
	private Selector selector;
	private final static Logger log = LogManager.getLogger(testServer.class);;
	
	public Poller(Selector selector) throws IOException {
		this.selector = selector;
	}
	
	@Override
	public void run() {
		log.info("poller starts...");
		while (true) {
			try {
				this.selector.select();
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
						key.interestOps(SelectionKey.OP_WRITE);
					}
					if(key.isWritable()) {
						log.info("writable");
						
						key.interestOps(SelectionKey.OP_READ);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
