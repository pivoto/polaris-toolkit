package io.polaris.core.concurrent.pool;

import java.util.function.Consumer;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public interface ResourceableConsumer<E> extends Consumer<E> {

	void open();

	void close();


}
