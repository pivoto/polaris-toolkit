package io.polaris.core.log;

import org.apache.logging.log4j.ThreadContext;

/**
 * @author Qt
 * @since  Aug 04, 2023
 */
class Log4j2Stack implements IStack {

	@Override
	public void put(String key, String val) {
		ThreadContext.put(key, val);
	}

	@Override
	public String get(String key) {
		return ThreadContext.get(key);
	}

	@Override
	public void remove(String key) {
		ThreadContext.remove(key);
	}

	@Override
	public void clear() {
		ThreadContext.clearAll();
	}

	@Override
	public void push(String msg) {
		ThreadContext.push(msg);
	}

	@Override
	public String pop() {
		return ThreadContext.pop();
	}

	@Override
	public String peek() {
		return ThreadContext.peek();
	}
}
