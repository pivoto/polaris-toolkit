package io.polaris.core.concurrent;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadsTest {

	@Test
	void test01() {
		Consoles.println("{}", Threads.getCallerClass());
		Consoles.println("{}", Threads.getCallerClass(ThreadsTest.class));
	}
}
