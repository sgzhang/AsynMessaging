package com.sgzhang.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sgzhang.test.task.Task;

public class Worker implements Runnable {
	private final ThreadPool threadPool;
	private boolean initilized;
	private ThreadSafeQ<Task> jobQ = new ThreadSafeQ<>();
	private static final Logger LOGGER = LogManager.getLogger("worker");
	
	public Worker(ThreadPool threadPool) {
		this.threadPool = threadPool;
	}
	
	public synchronized boolean addJob(Task task) {
		boolean notify = jobQ.add(task);
		this.notifyAll();
		return notify;
	}
	
	@Override
	public void run() {
		if(!initilized) {
			threadPool.acknowledgeInit(this);
			initilized = true;
//			LOGGER.info("worker from WORKER-["+Thread.currentThread().getName()+"]");
		}
		while (true) {
			if(jobQ.isEmpty()) {
				synchronized (this) {
					if (jobQ.isEmpty()) {
						try {
							wait();
						} catch (InterruptedException e) {
							// ingore
						}
					}
				}
			}
//			LOGGER.info("worker from WORKER-["+Thread.currentThread().getName()+"] task size -["+jobQ.length()+"]");
			Task task = jobQ.remove();
			task.complete();
			threadPool.acknowledgeCompletion(this);
		}
	}
}
