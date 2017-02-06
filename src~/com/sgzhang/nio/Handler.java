package com.sgzhang.nio;

import com.sgzhang.util.HTTPResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class Handler implements Runnable {

    final SocketChannel socketChannel;
    final SelectionKey selectionKey;
    ByteBuffer input = ByteBuffer.allocate(1024);
    static final int READING = 0, SENDING = 1;
    int state = READING;
    //    public HTTPRequest request = null;
    String clientName = "";

    Handler(Selector selector, SocketChannel c) throws IOException {
        socketChannel = c;
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
                readProcess(readCount);
            }
//            System.out.println(clientName);
//            request = new HTTPRequest(clientName);
            state = SENDING;
            // Interested in writing
            selectionKey.interestOps(SelectionKey.OP_WRITE);
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
        StringBuilder sb = new StringBuilder();
        input.flip();
        byte[] subStringBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array, 0, subStringBytes, 0, readCount);
        // Assuming ASCII (bad assumption but simplifies the example)
        sb.append(new String(subStringBytes));
        input.clear();
        clientName = sb.toString().trim();
    }

    private void writeLine(String line) throws IOException {
        socketChannel.write(ByteBuffer.wrap((line + "\r\n").getBytes()));
    }

    void send() throws IOException {
        try {
            HTTPResponse response = new HTTPResponse();
            response.setContent("I like cats".getBytes());
            response.addDefaultHeaders();
            writeLine(response.getVersion() + " " + response.getResponseCode() + " " + response.getResponseReason());
            for (Map.Entry<String, String> header : response.getHeader().entrySet()) {
                writeLine(header.getKey() + ": " + header.getValue());
            }
            writeLine("");
            socketChannel.write(ByteBuffer.wrap(response.getContent()));
//            String[] line = clientName.split("\r\n");
//            System.out.println(line[0]);
//            ByteBuffer output = ByteBuffer.wrap(("Hello " + clientName + "\n").getBytes());
//            socketChannel.write(output);
            selectionKey.interestOps(SelectionKey.OP_READ);
            state = READING;
        } catch (Exception e) {
            socketChannel.close();
        }
    }
}