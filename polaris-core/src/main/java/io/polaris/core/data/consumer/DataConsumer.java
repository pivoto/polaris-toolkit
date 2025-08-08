package io.polaris.core.data.consumer;

import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public interface DataConsumer<T> {

	void consume(List<T> data);

	default void onError(List<T> data, Throwable t) {
	}

	default void onExit() {
	}

}
