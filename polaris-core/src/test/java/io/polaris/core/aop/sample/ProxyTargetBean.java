package io.polaris.core.aop.sample;

import java.util.function.Supplier;

import io.polaris.core.io.Consoles;
import io.polaris.core.random.Randoms;

/**
 * @author Qt
 * @since May 14, 2024
 */
public class ProxyTargetBean implements Supplier {

	public void test(int a, int b) {
		Consoles.log("args: a={},b={}", a, b);
	}

	@Override
	public Object get() {
		return Randoms.randomString(10);
	}
}
