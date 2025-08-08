package io.polaris.core.data.partition;

/**
 * @author Qt
 * @since 1.8
 */
public interface DataPartitioner<T> {

	int partition(int total, T data);

}
