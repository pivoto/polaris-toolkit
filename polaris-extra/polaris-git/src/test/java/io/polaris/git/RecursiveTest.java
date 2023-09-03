package io.polaris.git;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8,  Sep 03, 2023
 */
public class RecursiveTest {


	@Test
	void test03() {
		System.out.println(recursive(5));
		System.out.println(loop(5));
		System.out.println(recursive(15));
		System.out.println(loop(15));
	}

	static int recursive(int i) {
		if (i <= 0) {
			return 1;
		}
		return i * recursive(i - 1);
	}

	static Integer loop(int param) {
		BiFunction<Integer, Integer, Integer> func = (i, v) -> i * v;
		Function<Integer, Integer> next = i -> i - 1;
		Function<Integer, Integer> init = i -> i <= 0 ? 1 : null;
		return doLoop(param, next, func, init);
	}

	static <E, R> R doLoop(E param, Function<E, E> next, BiFunction<E, R, R> func, Function<E, R> init) {
		E arg = param;
		R rs = null;
		Deque<E> stack = new ArrayDeque<>();
		while (true) {
			rs = init.apply(arg);
			if (rs != null) {
				break;
			}
			stack.offerLast(arg);
			arg = next.apply(arg);
		}
		while (!stack.isEmpty()) {
			E last = stack.pollLast();
			rs = func.apply(last, rs);
		}
		return rs;
	}
}
