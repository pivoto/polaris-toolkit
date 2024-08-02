package io.polaris.core.statistics;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

class SlidingWindowTest {

	@org.junit.jupiter.api.Test
	void test() throws InterruptedException {
		SlidingWindow<SimpleStatistics> window1 = new SlidingWindow<>(15, 100, SimpleStatistics::new);
		SlidingWindow<SimpleStatistics> window2 = new SlidingWindow<>(5, 100, SimpleStatistics::new);
		window1.startScheduler();
		window2.startScheduler();

		int COUNT = 3;
		CountDownLatch latch = new CountDownLatch(COUNT);
		Runnable task = () -> {
			for (int i = 0; i < 1000; i++) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ignored) {
				}
				window1.emit(System.currentTimeMillis(), "");
				window2.emit(System.currentTimeMillis(), "");
			}
			latch.countDown();
		};
		long begin = System.currentTimeMillis();
		for (int i = 0; i < COUNT; i++) {
			Thread thread = new Thread(task);
			thread.start();
		}
		latch.await();
		System.out.println("elapse :" + (System.currentTimeMillis() - begin));

		List<Long> list1 = window1.get().stream().map(SimpleStatistics::total).collect(Collectors.toList());
		List<Long> list2 = window2.get().stream().map(SimpleStatistics::total).collect(Collectors.toList());
		System.out.println(list1 + " => " + list1.stream().reduce(Long::sum));
		System.out.println("summary:" + window1.summary().total());
		System.out.println(list2 + " => " + list2.stream().reduce(Long::sum));
		System.out.println("summary:" + window2.summary().total());
	}

}
