package io.polaris.core.datacarrier;

import java.util.concurrent.atomic.AtomicBoolean;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * @author Qt
 * @since 1.8
 */
public class ThreadTest {


	@Test
	void test01() throws InterruptedException {
		AtomicBoolean running = new AtomicBoolean(true);
		Thread t = new Thread(() -> {
			while (running.get()) {
				TestConsole.printx("run...");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			TestConsole.printx("end.");
		});

		Executable executable = () -> {
			t.start();
			Thread.sleep(200);
			running.set(false);
			Thread.sleep(100);
			running.set(true);
		};
		Assertions.assertDoesNotThrow(executable);
		Assertions.assertThrows(IllegalThreadStateException.class, executable);
	}
}
