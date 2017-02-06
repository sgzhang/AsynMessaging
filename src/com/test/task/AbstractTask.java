package com.sgzhang.test.task;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;

import com.sgzhang.test.Server;

public abstract class AbstractTask implements Task{
	public static AtomicInteger jobCounter = new AtomicInteger(0);
	private SelectionKey selectionKey;
	private Server server;
	private int jobId;
	
	public AbstractTask(SelectionKey selectionKey, Server server) {
		this.selectionKey = selectionKey;
		this.server = server;
		this.jobId = jobCounter.incrementAndGet();
	}
}