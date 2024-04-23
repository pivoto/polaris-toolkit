package io.polaris.core.concurrent.pool;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public interface RunnableState<E> extends RunnableStatisticsHolder {


	/** 消费完成通知 */
	default void notifyFinished() {
	}

	/** 活动消费者数增加 */
	default void incrementActiveCount() {
	}

	/** 活动消费者数减少 */
	default void decrementActiveCount() {
	}

	/** 是否可能存在消费数据(处于生产进行中或消费队列非空) */
	default boolean hasNext() {
		return false;
	}

	/** 提取下一条消费数据 */
	default E next() {
		return null;
	}


}
