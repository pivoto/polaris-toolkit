package io.polaris.core.concurrent.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import io.polaris.core.concurrent.Executors;
import io.polaris.core.concurrent.Threads;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPriorityQueueTest {


	@Test
	void test01() {
		ThreadPoolExecutor pool = Executors.create(1, 1, ThreadPriorityQueue.create(new ArrayBlockingQueue<>(100)), "test", false);
		for (int i = 0; i < 10; i++) {
			pool.execute(() -> {
				Threads.sleep(100);
				Consoles.println("", Thread.currentThread().getId(), Thread.currentThread().getName());
			});
		}
		Threads.sleep(10000);
	}
}
