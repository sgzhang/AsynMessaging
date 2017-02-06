
import com.sgzhang.bio.*;
import com.sgzhang.nio.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NIO {
	public static void main(String[] args) throws IOException, InterruptedException {
		Reactor reactor = new Reactor(9000, false);
		new Thread(reactor).start();
		System.out.println("Event based server start.");
//		TinyServer sb = new TinyServer(new InetSocketAddress(9000));
//		sb.setTHREAD_POOL_SIZE(Integer.parseInt(args[0]));
//		new Thread(sb).start();
//		System.out.println("Thread based server start.");
//		WebServer server = new WebServer(new InetSocketAddress(9000));
//		while (!Thread.interrupted()) {
//			server.run();
//		}
	}
}
