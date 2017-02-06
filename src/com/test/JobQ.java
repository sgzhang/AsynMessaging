package com.sgzhang.test;

import com.sgzhang.test.task.Task;

public class JobQ {
	private static final JobQ instance = new JobQ();
	public final ThreadSafeQ<Task> jobs = new ThreadSafeQ<>();
	private JobQ() {
		
	}
	
	public static JobQ getInstance() {
		return instance;
	}
	
	public synchronized void addJob(Task t) {
//		if ()
		jobs.add(t);
		this.notifyAll();
	}
	
	public synchronized boolean hasJob() {
		return !jobs.isEmpty();
	}
	
	public Task getNextJob() {
		if(hasJob()) {
			return jobs.peek();
		}
		return null;
	}
	
	public void remove(Task t) {
		jobs.remove(t);
	}
}
