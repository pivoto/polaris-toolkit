package io.polaris.core.datacarrier.partition;

/**
 * @author Qt
 * @since 1.8
 */
public interface IDataPartitioner<T> {

	int partition(int total, T data);

}
