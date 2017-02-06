package com.sgzhang.test;

import java.nio.channels.SelectionKey;

import java.nio.ByteBuffer;

public class Attachment {
	public SelectionKey selectionKey;
	public ByteBuffer input;
	public ByteBuffer output;
	
	public Attachment(SelectionKey selectionKey, int inputSize, int outputSize) {
		this.selectionKey = selectionKey;
		this.input = ByteBuffer.allocate(inputSize);
		this.output = ByteBuffer.allocate(outputSize);
	}
}
