package io.polaris.core.clock;


import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qt
 * @since 1.8
 */
public class SeqIdClock implements IdClock {
	private static volatile SeqIdClock DEFAULT;
	private final AtomicLong currentTimestamp = new AtomicLong(System.currentTimeMillis());

	public SeqIdClock() {
	}

	public static SeqIdClock getInstance() {
		if (DEFAULT == null) {
			synchronized (SeqIdClock.class) {
				if (DEFAULT == null) {
					DEFAULT = new SeqIdClock();
				}
			}
		}
		return DEFAULT;
	}


	@Override
	public long setCurrentTimestamp() {
		return setCurrentTimestamp(System.currentTimeMillis());
	}

	@Override
	public long setCurrentTimestamp(long timestamp) {
		currentTimestamp.set(timestamp);
		return timestamp;
	}

	@Override
	public long getCurrentTimestamp() {
		return currentTimestamp.incrementAndGet();
	}

	@Override
	public long nextTimestamp(long lastTimestamp) {
		long currTimestamp = currentTimestamp.incrementAndGet();
		if (currTimestamp < lastTimestamp) {
			// 避免回拨
			currTimestamp = currentTimestamp.addAndGet(lastTimestamp - currTimestamp + 1);
		}
		return currTimestamp;
	}

}
