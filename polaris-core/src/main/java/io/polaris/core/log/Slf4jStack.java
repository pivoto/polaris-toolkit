package io.polaris.core.log;

import org.slf4j.MDC;

/**
 * @author Qt
 * @since  Aug 04, 2023
 */
class Slf4jStack implements IStack {
	public static final String DELIMITER = "|";
	public static final String STACK_KEY = "MSG";

	@Override
	public void put(String key, String val) {
		MDC.put(key, val);
	}

	@Override
	public String get(String key) {
		return MDC.get(key);
	}

	@Override
	public void remove(String key) {
		MDC.remove(key);
	}

	@Override
	public void clear() {
		MDC.clear();
	}

	@Override
	public void push(String msg) {
		String old = MDC.get(STACK_KEY);
		if (old == null || old.trim().length() == 0) {
			MDC.put(STACK_KEY, msg);
		} else {
			if (!old.contains(msg)) {
				MDC.put(STACK_KEY, old + DELIMITER + msg);
			}
		}
	}

	@Override
	public String pop() {
		String old = MDC.get(STACK_KEY);
		if (old == null || old.trim().length() == 0) {
			return "";
		}
		old = old.trim();
		int i = old.lastIndexOf(DELIMITER);
		if (i >= 0) {
			String pop = old.substring(i + 1).trim();
			String msg = old.substring(0, i).trim();
			if (msg == null || msg.trim().length() == 0) {
				MDC.remove(STACK_KEY);
			} else {
				MDC.put(STACK_KEY, msg);
			}
			return pop;
		}
		MDC.remove(STACK_KEY);
		return old;
	}

	@Override
	public String peek() {
		String old = MDC.get(STACK_KEY);
		if (old == null || old.trim().length() == 0) {
			return "";
		}
		old = old.trim();
		int i = old.lastIndexOf(DELIMITER);
		if (i >= 0) {
			String pop = old.substring(i + 1).trim();
			return pop;
		}
		return old;
	}
}
