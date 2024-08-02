package io.polaris.core.statistics;

/**
 * @author Qt
 * @since Aug 01, 2024
 */
public interface EventStatistics<S extends EventStatistics<S>> extends Cloneable {

	void receive(Object event);

	void minus(S statistics);

	void plus(S statistics);

	S clone();
}
