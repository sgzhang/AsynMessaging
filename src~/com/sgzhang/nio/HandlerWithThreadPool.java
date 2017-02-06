package com.sgzhang.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandlerWithThreadPool extends Handler {
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static final int PROCESSING = 2;

    public HandlerWithThreadPool(Selector selector, SocketChannel socketChannel) throws IOException {
        super(selector, socketChannel);
    }

    void read() throws IOException {
        try {
            int readCount = socketChannel.read(input);
            if (readCount > 0) {
                state = PROCESSING;
                executorService.submit(new Processor(readCount));
            }
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        } catch (Exception e) {
            socketChannel.close();
        }
    }

    private synchronized void processAndHandoff(int readCount) {
        readProcess(readCount);
        state = SENDING;
    }

    private class Processor implements Runnable {
        int readCount;

        Processor(int readCount) {
            this.readCount = readCount;
        }

        @Override
        public void run() {
            processAndHandoff(readCount);
        }
    }
}
