package com.sgzhang.test.task;

import java.nio.channels.SelectionKey;
import java.util.concurrent.atomic.AtomicInteger;
import java.nio.channels.Selector;

import com.sgzhang.test.Server;

public abstract class AbstractTask implements Task{
	public static AtomicInteger jobCounter = new AtomicInteger(0);
	private SelectionKey selectionKey;
	private Selector selector;
	private int jobId;
	
	public AbstractTask(SelectionKey selectionKey, Selector selector) {
		this.selectionKey = selectionKey;
		this.selector = selector;
		this.jobId = jobCounter.incrementAndGet();
	}
}
