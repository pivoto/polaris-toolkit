package io.polaris.core.data.partition;

/**
 * @author Qt
 */
public interface DataPartitioner<T> {

	int partition(int total, T data);

}
