package io.polaris.core.clock;


/**
 * @author Qt
 * @since 1.8
 */
public class TimeIdClock implements IdClock {
	private static volatile TimeIdClock DEFAULT;

	public TimeIdClock() {
	}

	public static TimeIdClock getInstance() {
		if (DEFAULT == null) {
			synchronized (TimeIdClock.class) {
				if (DEFAULT == null) {
					DEFAULT = new TimeIdClock();
				}
			}
		}
		return DEFAULT;
	}

	@Override
	public long setCurrentTimestamp() {
		return System.currentTimeMillis();
	}

	@Override
	public long setCurrentTimestamp(long timestamp) {
		return System.currentTimeMillis();
	}

	@Override
	public long getCurrentTimestamp() {
		return 0;
	}

	@Override
	public long nextTimestamp(long lastTimestamp) {
		long currTimestamp = System.currentTimeMillis();
		if (currTimestamp < lastTimestamp) {
			// 避免回拨
			currTimestamp = lastTimestamp;
		}
		return currTimestamp;
	}
}
