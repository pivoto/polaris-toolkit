package io.polaris.core.data.partition;

/**
 * @author Qt
 */
public class ProducerThreadPartitioner<T> implements DataPartitioner<T> {
	public ProducerThreadPartitioner() {
	}

	@Override
	public int partition(int total, T data) {
		return (int) Thread.currentThread().getId() % total;
	}

}
