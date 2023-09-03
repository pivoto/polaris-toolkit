package io.polaris.core.datacarrier.consumer;

import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public interface IConsumer<T> {

	void consume(List<T> data);

	default void onError(List<T> data, Throwable t) {
	}

	default void onExit() {
	}

}
