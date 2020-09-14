package io.polaris.core.guid;


import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qt
 * @since 1.8
 */
public class GuidSequenceClock implements GuidClock {
	private static final GuidSequenceClock DEFAULT = new GuidSequenceClock();
	private AtomicLong currentTimestamp = new AtomicLong(System.currentTimeMillis());

	public GuidSequenceClock() {
		/*init(1000L);*/
	}

	public static GuidSequenceClock getInstance() {
		return DEFAULT;
	}

	@Override
	public long currentTimestamp() {
		currentTimestamp.set(System.currentTimeMillis());
		return currentTimestamp.get();
	}

	@Override
	public long nextTimestamp(long lastTimestamp) {
		long currTimestamp = lastTimestamp;
		while (currTimestamp <= lastTimestamp) {
			currTimestamp = currentTimestamp.incrementAndGet();
		}
		return currTimestamp;
	}

}
