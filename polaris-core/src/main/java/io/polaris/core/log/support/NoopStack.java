package io.polaris.core.log.support;


/**
 * @author Qt
 * @since  Aug 04, 2023
 */
class NoopStack implements Stack {

	@Override
	public void put(String key, String val) {
	}

	@Override
	public String get(String key) {
		return null;
	}

	@Override
	public void remove(String key) {
	}

	@Override
	public void clear() {
	}

	@Override
	public void push(String msg) {
	}

	@Override
	public String pop() {
		return null;
	}

	@Override
	public String peek() {
		return null;
	}
}
