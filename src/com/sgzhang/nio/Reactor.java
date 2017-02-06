package com.sgzhang.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Reactor implements Runnable {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;
    private final boolean isWithThreadPool;
	private final int POOL_SIZE;
	private int sndbuf;

	/** select count */
//	int count = 0;

    public Reactor(int port, boolean isWithThreadPool, int POOL_SIZE, int sndbuf) throws IOException {
        this.isWithThreadPool = isWithThreadPool;
		this.POOL_SIZE = POOL_SIZE;
        this.sndbuf = sndbuf;
		selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
		SelectionKey selectionKey0 = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey0.attach(new Acceptor());
    }

    @Override
    public void run() {
//        System.out.println("Server listening to port: " + serverSocketChannel.socket().getLocalPort());
        try {
            while (!Thread.interrupted()) {
//				long before = System.currentTimeMillis();
                selector.select();
//				System.out.println("select count ["+(count++)+"]");
//				long after = System.currentTimeMillis();
//				System.out.println("select ["+(after-before)+"]");
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
					SelectionKey k = (SelectionKey) it.next();
                    dispatch(k);
//					if (k.isReadable())
//						System.out.println("readable ["+k.isReadable()+"]");
//					else if (k.isWritable()) {
//						System.out.println("writable ["+k.isWritable()+"]");
//					}
                }
                selected.clear();
            }
        } catch (IOException e) {
//			e.printStackTrace();
//			shutdown();
        }
    }

    private void shutdown() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            // ignore
        }
    }

    private void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment());
        if (r != null) {
            r.run();
        }
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    if (isWithThreadPool)
                        new HandlerWithThreadPool(selector, socketChannel, POOL_SIZE, sndbuf);
                    else
                        new Handler(selector, socketChannel, sndbuf);
                }
//				System.out.println("Connection Accepted by Reactor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
