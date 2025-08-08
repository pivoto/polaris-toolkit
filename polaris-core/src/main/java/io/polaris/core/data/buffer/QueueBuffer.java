package io.polaris.core.data.buffer;

import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public interface QueueBuffer<T> {

	boolean push(T data);

	void drainTo(List<T> consumeList);

	/**
	 * @return 缓冲队列大小
	 */
	int size();

}
