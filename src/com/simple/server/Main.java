package com.simple.server;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread server = new Thread(new Server()	);
		server.start();

	}

}
