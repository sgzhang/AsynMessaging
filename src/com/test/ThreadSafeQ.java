package com.sgzhang.test;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.Queue;

import com.sgzhang.test.task.Task;
import com.sgzhang.test.task.WriteTask;

public class ThreadSafeQ<T> {
	private final Queue<T> queue;
	
	public ThreadSafeQ() {
		queue = new LinkedList<T>();
	}
	
	public boolean add(T obj) {
		synchronized (queue) {
			return queue.add(obj);
		}
	}
	
	public boolean isEmpty(){
        synchronized (queue){
            return queue.isEmpty();
        }
    }

    public T peek(){
        synchronized (queue){
            return queue.peek();
        }
    }

    public T remove(){
        synchronized (queue){
            return queue.remove();
        }
    }

    public void remove(T obj){
        synchronized (queue){
            queue.remove(obj);
        }
    } 
    
    /**
     * Test @ Oct 24, 2016
     */
    public int length() {
    	synchronized (queue) {
    		return queue.size();
		}
    }
    
    public boolean contain(SelectionKey selectionKey) {
    	for (T t: queue) {
    		WriteTask task = (WriteTask) t;
    		if ((task.selectionKey).equals(selectionKey))
    			return true;
    	}
    	return false;
    }
}
