package io.polaris.core.asm.proxy;

import java.util.function.Function;

/**
 * @author Qt
 * @since May 11, 2024
 */
public class Source01 implements java.io.Serializable
	, Function<Source01, Source01> {

	public Source01() {
	}

	public Source01(int i) {
	}

	public Object[] testVArgs(Object... args) {
		return args;
	}

	public int intVal(int a) {
		testInt1(a);
		return a;
	}

	public void testInt1(int a) {
	}

	public void testInt2(int a, int b) {
	}

	@Override
	public Source01 apply(Source01 o) {
		return o;
	}
//
//	@Override
//	public <V> Function<Source01, V> andThen(Function<? super Source01, ? extends V> after) {
//		return Function.super.andThen(after);
//	}


	private Sub sub = new Sub();

	public String subToString(){
		return sub.toString();
	}

	private class Sub {

		public void test(){
			Source01.this.testVArgs();
		}
	}
}
