package io.polaris.core.net;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import io.polaris.core.io.Consoles;
import io.polaris.core.io.IO;
import org.junit.jupiter.api.Test;

class NetsTest {

	@Test
	void test00() throws IOException {
		ServerSocket serverSocket = null;
		DatagramSocket datagramSocket = null;
		try {
			serverSocket = new ServerSocket(1024);
			datagramSocket = new DatagramSocket(1024);
			Consoles.println(serverSocket.getLocalPort(), datagramSocket.getLocalPort());
		} finally {
			IO.close(serverSocket);
			IO.close(datagramSocket);
		}
	}

	@Test
	void test01() {
		Object[] args = new Object[]{Nets.getRandomTcpPort()};
		Consoles.println(args);
	}

	@Test
	void test02() {
		Object[] args = new Object[]{Nets.getRandomTcpPort(1024, 2048)};
		Consoles.println(args);
	}

	@Test
	void test03() {
		Object[] args = new Object[]{Nets.getAvailableTcpPort(1024, 2048)};
		Consoles.println(args);
	}

	@Test
	void test04() {
		Object[] args = new Object[]{Nets.getRandomUdpPort()};
		Consoles.println(args);
	}

	@Test
	void test05() {
		Object[] args = new Object[]{Nets.getRandomUdpPort(1024, 2048)};
		Consoles.println(args);
	}

	@Test
	void test06() {
		Object[] args = new Object[]{Nets.getAvailableUdpPort(1024, 2048)};
		Consoles.println(args);
	}
}
