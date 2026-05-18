package io.polaris.core.concurrent.thread;

/**
 * @author Qt
 */
public interface InternalThreadSpec {

	InternalThreadLocalMap threadLocalMap();

	void setThreadLocalMap(InternalThreadLocalMap threadLocalMap);

}
