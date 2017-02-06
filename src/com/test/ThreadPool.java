package com.sgzhang.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sgzhang.test.task.Task;


public class ThreadPool {
	private final int SIZE;
	private final List<Thread> workers;
	private final ThreadPoolManager threadPoolManager;
	private CountDownLatch countDownLatch;
	private volatile boolean initilized;
	private ThreadSafeQ<Worker> idleWorkers; 
	private final static Logger LOGGER = LogManager.getLogger("threadPool");
	
	private class ThreadPoolManager extends Thread {
		@Override
		public void run() {
			JobQ jobQ = JobQ.getInstance();
			while (true) {
				Task task = null;
				synchronized (jobQ) {
					if(jobQ.hasJob()) {
						task = jobQ.getNextJob();
					} else {
						try {
							jobQ.wait();
							if (jobQ.hasJob()){
								task = jobQ.getNextJob();
							}
						} catch (InterruptedException e) {
							continue;
						}
					}
					if (task != null && !idleWorkers.isEmpty()) {
//						System.out.println("available workers: ["+idleWorkers.length()+"]");
						Worker worker = idleWorkers.remove();
						worker.addJob(task);
						jobQ.remove(task);
					}
				}
			}
		}
	}
	
	public ThreadPool(int size) {
		this.SIZE = size;
		countDownLatch = new CountDownLatch(this.SIZE);
		workers = new ArrayList<Thread>(this.SIZE);
		idleWorkers = new ThreadSafeQ<Worker>();
		threadPoolManager = new ThreadPoolManager();
	}
	
	public boolean initilize() {
		threadPoolManager.start();
		for (int i = 0; i < this.SIZE; i++) {
			workers.add(new Thread(new Worker(this)));
		}
		if (!initilized) {
			for(Thread t: workers) {
				t.start();
				LOGGER.debug("worker started-["+t.getName()+"]");
			}
			try {
				countDownLatch.await();
				initilized = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		return false;
	}
	
	public void acknowledgeInit(Worker worker) {
		countDownLatch.countDown();
		idleWorkers.add(worker);
	}
	
	public void acknowledgeCompletion(Worker worker) {
		idleWorkers.add(worker);
	}
}
