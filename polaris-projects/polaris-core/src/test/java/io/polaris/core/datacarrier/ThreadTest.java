package io.polaris.core.datacarrier;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Qt
 * @since 1.8
 */
public class ThreadTest {


	@Test
	void test01() throws InterruptedException {
		AtomicBoolean running = new AtomicBoolean(true);
		Thread t = new Thread() {
			@Override
			public void run() {
				while (running.get()) {
					System.out.println("run...");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
				System.out.println("end.");
			}
		};
		t.start();
		Thread.sleep(200);
		running.set(false);
		Thread.sleep(100);
		running.set(true);
		t.start(); // illegal thread state
		Thread.sleep(200);
		running.set(false);
		Thread.sleep(100);
	}
}
