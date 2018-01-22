package com.sgzhang.test;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;
import java.util.Queue;

import com.sgzhang.test.task.Task;
import com.sgzhang.test.task.WriteTask;

public class ThreadSafeQ<T> {
	private Object[] queue;
	int size = 0;
	int insert = 0;
	int remove = 0;
				
	public ThreadSafeQ() {
//		queue = new LinkedList<T>();
		queue = new Object[128];
		size = 128;
	}
								
	public synchronized boolean add(T obj) {
		queue[insert++] = obj;
		if (insert == size){
			insert = 0;
		}
		if (insert == remove) {
			expand();
		}
		return true;
	}
												
	private void expand() {
		int newSize = size * 2;
		Object[] newQueue = new Object[newSize];
		System.arraycopy(queue, insert, newQueue, 0, size-insert);
		System.arraycopy(queue, 0, newQueue, size-insert, insert);
																		
		insert = size;
		remove = 0;
		queue = newQueue;
		size = newSize;
	}
							
	public synchronized boolean isEmpty(){
        if (length() == 0)
			return true;
		else
            return false;
    }
	
	@SuppressWarnings("unchecked")
	public synchronized T peek(){
		if (insert == remove) {
			return null;
        }
        return (T) queue[remove];
    }

	public synchronized T remove(){
		if (insert == remove) {
			return null;
	    }
	    @SuppressWarnings("unchecked")
	    T result = (T) queue[remove];
	    queue[remove] = null;
		remove++;
		if (remove == size) {
			remove = 0;
		}
		return result;
	}	
	
	public synchronized void remove(T obj){
		if (insert == remove) {
			return;
		}
		queue[remove] = null;
		remove++;
		if (remove == size) {
			remove = 0;
		}
	} 
	
	/**
	* Test @ Oct 24, 2016
	*/
	public synchronized int length() {
		int result = insert - remove;
		if (result < 0) {
			result += size;
		}
		return result;
	}

	public boolean contain(SelectionKey selectionKey) {
		for (Object t: queue) {
			WriteTask task = (WriteTask) t;
			if ((task.selectionKey).equals(selectionKey))
				return true;
		}
		return false;
	}
}

