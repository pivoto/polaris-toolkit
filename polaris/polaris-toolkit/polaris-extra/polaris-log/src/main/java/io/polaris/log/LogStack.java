package io.polaris.log;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class LogStack {

	private static IStack iStack;

	static {
		try {
			ThreadContext.peek(); // 执行log4j2的方法, 确定可用
			iStack = new Log4j2Stack();
		} catch (Throwable e) {
			iStack = new Slf4jStack();
		}
	}

	public static void put(String key, String val) {
		iStack.put(key, val);
	}

	public static String get(String key) {
		return iStack.get(key);
	}

	public static void remove(String key) {
		iStack.remove(key);
	}

	public static void clear() {
		iStack.clear();
	}

	public static void push(String msg) {
		iStack.push(msg);
	}

	public static void pushIfAbsent(String msg) {
		String last = iStack.peek();
		if (!Objects.equals(last, msg)) {
			iStack.push(msg);
		}
	}

	public static void pop(String msg) {
		Deque<String> queue = new ArrayDeque<>();
		while (true) {
			String pop = pop();
			if (pop == null || pop.trim().length() == 0 || pop.equals(msg)) {
				break;
			} else {
				queue.addLast(pop);
			}
		}
		for (String s : queue) {
			push(s);
		}
	}

	public static String pop() {
		return iStack.pop();
	}

	public static String peek() {
		return iStack.peek();
	}

	static interface IStack {
		void put(String key, String val);

		String get(String key);

		void remove(String key);

		void clear();

		void push(String msg);

		String pop();

		String peek();
	}

	static class Log4j2Stack implements IStack {

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

	static class Slf4jStack implements IStack {
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
}
