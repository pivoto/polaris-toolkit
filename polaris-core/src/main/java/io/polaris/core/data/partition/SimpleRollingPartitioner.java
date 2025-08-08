package io.polaris.core.data.partition;

/**
 * @author Qt
 * @since 1.8
 */
public class SimpleRollingPartitioner <T> implements DataPartitioner<T> {
	private volatile int i = 0;

	@Override
	public int partition(int total, T data) {
		return Math.abs(i++ % total);
	}

}
