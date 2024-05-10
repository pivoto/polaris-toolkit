package io.polaris.core.log;

/**
 * @author Qt
 * @since  Aug 04, 2023
 */
interface IStack {
	void put(String key, String val);

	String get(String key);

	void remove(String key);

	void clear();

	void push(String msg);

	String pop();

	String peek();
}
