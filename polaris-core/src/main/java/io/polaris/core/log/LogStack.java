package io.polaris.core.log;


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
			org.apache.logging.log4j.ThreadContext.peek(); // 执行log4j2的方法, 确定可用
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

}
