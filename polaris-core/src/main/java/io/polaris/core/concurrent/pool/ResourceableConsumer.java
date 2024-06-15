package io.polaris.core.concurrent.pool;

import java.util.function.Consumer;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
public interface ResourceableConsumer<E> extends Consumer<E> {

	void open();

	void close();


}
