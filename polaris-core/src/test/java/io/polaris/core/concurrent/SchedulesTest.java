package io.polaris.core.concurrent;

import java.util.concurrent.ScheduledExecutorService;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class SchedulesTest {

	@Test
	void test01() throws InterruptedException {

		DefaultWrappingTaskFactory factory = new DefaultWrappingTaskFactory();
		factory.setInterceptor(new WrappingInterceptor() {

			@Override
			public void onBefore() {
				Consoles.log("before");
			}

			@Override
			public void onAfter() {
				Consoles.log("after");
			}

			@Override
			public void onThrowing(Throwable e) {
				Consoles.log("throwing");
			}

			@Override
			public void onFinally() {
				Consoles.log("finally");
			}
		});
		WrappingSchedules.setDefaultWrappingTaskFactory(factory);

		ScheduledExecutorService executor = WrappingSchedules.create(5, "Test");
		for (int i = 0; i < 5; i++) {
			executor.execute(() -> {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				Consoles.log("execute");
			});
			executor.submit(() -> {
				Consoles.log("submit");
			});
			executor.submit(() -> {
				Consoles.log("call");
				return null;
			});
		}
		Thread.sleep(1000);
		Executors.shutdown(executor);
	}
}
