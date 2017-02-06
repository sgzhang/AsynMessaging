package com.sgzhang.test;

import java.nio.channels.SelectionKey;

public class PollerEvent {
	public SelectionKey selectionKey;
	public testServer server;
	public final int mode;
	public final static int READ = 0, WRITE = 1;
	
	public PollerEvent(SelectionKey key, testServer server, int mode) {
		this.selectionKey = key;
		this.server = server;
		this.mode = mode;
	}
}
