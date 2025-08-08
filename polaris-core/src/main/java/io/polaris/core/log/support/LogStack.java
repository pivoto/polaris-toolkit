package io.polaris.core.log.support;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * @author Qt
 * @since 1.8
 */
public class LogStack {

	private static Stack stack;

	static {
		try {
			org.apache.logging.log4j.ThreadContext.peek(); // 执行log4j2的方法, 确定可用
			stack = new Log4j2Stack();
		} catch (Throwable ignored) {
		}
		if (stack == null) {
			try{
				// noinspection ResultOfMethodCallIgnored
				org.slf4j.MDC.getMDCAdapter(); // 执行slf4j的方法, 确定可用
				stack = new Slf4jStack();
			}catch (Throwable ignored){
			}
		}
		if (stack == null) {
			stack = new NoopStack();
		}
	}

	public static void put(String key, String val) {
		stack.put(key, val);
	}

	public static String get(String key) {
		return stack.get(key);
	}

	public static void remove(String key) {
		stack.remove(key);
	}

	public static void clear() {
		stack.clear();
	}

	public static void push(String msg) {
		stack.push(msg);
	}

	public static void pushIfAbsent(String msg) {
		String last = stack.peek();
		if (!Objects.equals(last, msg)) {
			stack.push(msg);
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
		return stack.pop();
	}

	public static String peek() {
		return stack.peek();
	}

}
