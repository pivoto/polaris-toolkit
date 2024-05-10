package io.polaris.core.concurrent.pool;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public interface RunnableStatisticsHolder {


	/** 统计信息 */
	default RunnableStatistics runnableStatistics() {
		return null;
	}

}
