package io.netty.example.utils;

import java.util.*;

public class Count {
	public final static int LENGTH = 100;
	public final static int RATIO = 0;

	/** large response size and small response size length */
	public final static int LARGE_LENGTH = 102400;
	public final static int SMALL_LENGTH = 100;
	
	public final static String strL = getString(LARGE_LENGTH);
	public final static String strS = getString(SMALL_LENGTH);

	public static final byte[] largeStr = (getString(LARGE_LENGTH)+"\n").getBytes();
	public static final byte[] smallStr = (getString(SMALL_LENGTH)+"\n").getBytes();

	private static long count = 0;
	public static synchronized void increment() {
	    count++;
	}
	
	public static synchronized long getCount() {
	    return count;
	}   

	public static synchronized void sort(int n) {
		int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
				  11,12,13,14,15,16,17,18,19,10};
		for (int i = 0; i < n; i++) {
			Arrays.sort(a);
			Collections.reverse(Arrays.asList(a));
		}
	}

	public static int[] insertionSort(int[] array) {
		int tmp;
		for (int i = 1; i < array.length; i++) {
			for (int j = i; j > 0; j--) {
				if (array[j] < array[j-1]) {
					tmp = array[j];
					array[j] = array[j-1];
					array[j-1] = tmp;
				}
			}
		}
		return array;
	}
	
	public static int[] randomizeArray(int size) {
		Random r = new Random();
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = i;
		}
		for (int i = 0; i < array.length; i++) {
			int randomPosition = r.nextInt(array.length);
			int tmp = array[i];
			array[i] = array[randomPosition];
			array[randomPosition] = tmp;
		}
		return array;
	}
	public static String getString(final int length) {
	    StringBuilder stringBuilder = new StringBuilder();
	    for (int i = 0; i < length-1; i++) {
	        stringBuilder.append("x");
	    }
	    stringBuilder.append("y");
	    return stringBuilder.toString();
	}
}
