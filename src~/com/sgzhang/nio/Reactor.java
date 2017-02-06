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

    public Reactor(int port, boolean isWithThreadPool) throws IOException {
        this.isWithThreadPool = isWithThreadPool;
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
                selector.select();
                Set<SelectionKey> selected = selector.selectedKeys();
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) it.next());
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
                        new HandlerWithThreadPool(selector, socketChannel);
                    else
                        new Handler(selector, socketChannel);
                }
//				System.out.println("Connection Accepted by Reactor");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
