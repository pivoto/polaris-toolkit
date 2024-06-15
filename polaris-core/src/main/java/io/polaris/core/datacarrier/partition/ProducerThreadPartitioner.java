package io.polaris.core.datacarrier.partition;

/**
 * @author Qt
 * @since 1.8
 */
public class ProducerThreadPartitioner<T> implements IDataPartitioner<T> {
	public ProducerThreadPartitioner() {
	}

	@Override
	public int partition(int total, T data) {
		return (int) Thread.currentThread().getId() % total;
	}

}
