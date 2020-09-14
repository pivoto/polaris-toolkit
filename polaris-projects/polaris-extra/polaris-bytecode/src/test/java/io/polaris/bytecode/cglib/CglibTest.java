package io.polaris.bytecode.cglib;

import io.polaris.core.string.Strings;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public class CglibTest {
	@Test
	void test01() {
		ExecutorService executor = new ThreadPoolExecutor(1, 5, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
		executor.submit(() -> {
			System.out.println(Strings.format("{}> running...", Thread.currentThread().getName()));
		});

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(executor.getClass());
		enhancer.setCallback(new MethodInterceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				Object[] nargs = new Object[args.length];
				for (int i = 0; i < args.length; i++) {
					if (args[i] instanceof Runnable) {
						Runnable r = (Runnable) args[i];
						nargs[i] = (Runnable) (() -> {
							System.out.println(Strings.format("{}> before...", Thread.currentThread().getName()));
							r.run();
							System.out.println(Strings.format("{}> after...", Thread.currentThread().getName()));
						});
					} else {
						nargs[i] = args[i];
					}
				}
				return proxy.invokeSuper(obj, nargs);
			}
		});
		Class aClass = enhancer.createClass();
		executor = (ExecutorService) enhancer.create();

		executor.submit(() -> {
			System.out.println(Strings.format("{}> running...", Thread.currentThread().getName()));
		});
	}
}
