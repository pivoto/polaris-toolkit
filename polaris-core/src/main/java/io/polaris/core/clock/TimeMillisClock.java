package io.polaris.core.clock;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since 1.8
 */
public class TimeMillisClock {
	private final int period;
	private volatile long currentTimeMillis;

	private TimeMillisClock(final int period) {
		this.period = period;
		currentTimeMillis = System.currentTimeMillis();
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, r -> new Thread(r, "Clock-" + period));
		executor.scheduleAtFixedRate(() -> currentTimeMillis = (System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			executor.shutdown();
		}));
	}

	public static TimeMillisClock newClock(int period) {
		return new TimeMillisClock(period);
	}

	public static long currentTimeMillis() {
		return Holder.instance.currentTimeMillis;
	}

	static class Holder {
		static TimeMillisClock instance = new TimeMillisClock(1);
	}

}
