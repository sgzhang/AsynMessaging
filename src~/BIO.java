
import com.sgzhang.bio.TinyServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class BIO {
	public static void main(String[] args) throws IOException, InterruptedException {
//		Reactor reactor = new Reactor(9000, true);
//		new Thread(reactor).start();
		TinyServer sb = new TinyServer(new InetSocketAddress(9000));
		sb.setTHREAD_POOL_SIZE(Integer.parseInt(args[0]));
		new Thread(sb).start();
		System.out.println("Thread based server start.");
//		WebServer server = new WebServer(new InetSocketAddress(9000));
//		while (!Thread.interrupted()) {
//			server.run();
//		}
	}
}
