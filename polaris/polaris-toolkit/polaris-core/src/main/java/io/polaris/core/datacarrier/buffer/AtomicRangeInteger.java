package io.polaris.core.datacarrier.buffer;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Qt
 * @since 1.8
 */
public class AtomicRangeInteger extends Number implements Serializable {
	private static final int VALUE_LENGTH = 31;
	private static final int VALUE_OFFSET = 15;
	private final AtomicIntegerArray values = new AtomicIntegerArray(VALUE_LENGTH);
	private int startValue;
	private int endValue;

	public AtomicRangeInteger(int startValue, int maxValue) {
		this.values.set(VALUE_OFFSET, startValue);
		this.startValue = startValue;
		this.endValue = maxValue - 1;
	}

	public final int getAndIncrement() {
		int next;
		do {
			next = this.values.incrementAndGet(VALUE_OFFSET);
			if (next > endValue && this.values.compareAndSet(VALUE_OFFSET, next, startValue)) {
				return endValue;
			}
		}
		while (next > endValue);

		return next - 1;
	}

	public final int get() {
		return this.values.get(VALUE_OFFSET);
	}

	@Override
	public int intValue() {
		return this.values.get(VALUE_OFFSET);
	}

	@Override
	public long longValue() {
		return this.values.get(VALUE_OFFSET);
	}

	@Override
	public float floatValue() {
		return this.values.get(VALUE_OFFSET);
	}

	@Override
	public double doubleValue() {
		return this.values.get(VALUE_OFFSET);
	}
}
