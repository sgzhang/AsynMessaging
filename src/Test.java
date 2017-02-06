
import com.sgzhang.bio.TinyServer;
import com.sgzhang.nio.Reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		if (args[0].equalsIgnoreCase("bio") && args[1].matches("^[0-9]+$")) {
			TinyServer sb = new TinyServer(new InetSocketAddress(Integer.parseInt(args[1])),Integer.parseInt(args[2]),Integer.parseInt(args[3]));
			new Thread(sb).start();
			System.out.println("-- bio server is listening port 9000 (thread-pool is "+Integer.parseInt(args[2])+")");
		} else if (args[0].equalsIgnoreCase("nio1")) {
			//if (args[1] == null || !args[1].matches("^[0-9]+$")) {
			if (args[1].matches("^[0-9]+$")) {
				Reactor reactor = new Reactor(Integer.parseInt(args[1]), false, 0, Integer.parseInt(args[3]));
				new Thread(reactor).start();
				System.out.println("-- nio server is listening port 9000 (single-thread)");
			} else {
				Reactor reactor = new Reactor(9000, true, Integer.parseInt(args[1]), Integer.parseInt(args[1]));
				new Thread(reactor).start();
				System.out.println("-- nio server is listening port 9000 (thread-pool is "+Integer.parseInt(args[1])+")");
				System.out.println("---- Notice: for each connection, there is a coresponding READ thread-pool.\n     Therefore, total size of threads is "+Integer.parseInt(args[1])
						+" * CONCURRENCY.");
			}
		} else {
			System.out.println("please make sure you type correct options!");
			return;
		}
	}
}
