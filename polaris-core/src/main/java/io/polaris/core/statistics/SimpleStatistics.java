package io.polaris.core.statistics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qt
 * @since Aug 02, 2024
 */
public class SimpleStatistics implements EventStatistics<SimpleStatistics> {
	private final AtomicLong total = new AtomicLong(0);


	public long total() {
		return total.get();
	}

	@Override
	public void receive(Object event) {
		total.incrementAndGet();
	}

	@Override
	public void minus(SimpleStatistics statistics) {
		total.addAndGet(-statistics.total.get());
	}

	@Override
	public void plus(SimpleStatistics statistics) {
		total.addAndGet(statistics.total.get());
	}

	@Override
	public SimpleStatistics clone() {
		try {
			return (SimpleStatistics) super.clone();
		} catch (CloneNotSupportedException e) {
			SimpleStatistics statistics = new SimpleStatistics();
			statistics.total.set(total.get());
			return statistics;
		}
	}
}
