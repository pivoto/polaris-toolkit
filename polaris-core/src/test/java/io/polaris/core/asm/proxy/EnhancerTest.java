package io.polaris.core.asm.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.TestConsole;
import io.polaris.core.aop.AfterAdvice;
import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.collection.ObjectArrays;
import org.junit.jupiter.api.Test;

class EnhancerTest extends BaseAsmTest {

	@Test
	void test01() throws Exception {
		Enhancer enhancer = new Enhancer();
		enhancer.withFinal(false);
		enhancer.superclass(Source01.class);
		enhancer.interfaces(new Class[]{Consumer.class, Supplier.class});
		enhancer.interceptor(new Interceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
				System.out.println("before..." + method);
				Object rs = invocation.invoke(obj, args);
				System.out.println("after..." + method);
				return rs;
			}
		});
		Class<?> c = enhancer.createClass();
		{
			Field f = c.getDeclaredField("GENERATED$TARGET_METHODS");
			f.setAccessible(true);
			Method[] m = (Method[]) f.get(null);
			TestConsole.printx(ObjectArrays.toString(f.get(null)));
		}
		{
			Field f = c.getDeclaredField("GENERATED$STATIC_INTERCEPTOR");
			f.setAccessible(true);
			TestConsole.printx(ObjectArrays.toString(f.get(null)));
		}
		Source01 o = (Source01) enhancer.create();
		TestConsole.printx(o.hashCode());
		TestConsole.printx(o.toString());
		TestConsole.printx(o.testVArgs(1, 2, 3, 4));
		TestConsole.printx(o.andThen(x -> 123));
		TestConsole.printx(o.apply(null));
		TestConsole.printx(((Function) o).apply(null));
		TestConsole.printx(o.intVal(123));

		((Consumer)o).accept(123);
		TestConsole.printx(((Supplier)o).get());

	}


	@Test
	void test02() throws Exception {
		BulkEnhancer enhancer = new BulkEnhancer();
		enhancer.superclass(Source01.class);
		enhancer.interfaces(new Class[]{Consumer.class, Supplier.class});
		enhancer.interceptors(new Interceptor() {
			@Override
			public Object intercept(Object obj, Method method, Object[] args, Invocation invocation) throws Throwable {
				System.out.println();
				System.out.println("before..." + method);
				Object rs = invocation.invoke(obj, args);
				System.out.println("after..." + method);
				return rs;
			}
		});
		Class<?> c = enhancer.createClass();
		{
			Field f = c.getDeclaredField("GENERATED$TARGET_METHODS");
			f.setAccessible(true);
			TestConsole.printx(ObjectArrays.toString(f.get(null)));
		}
		{
			Field f = c.getDeclaredField("GENERATED$STATIC_INTERCEPTOR");
			f.setAccessible(true);
			TestConsole.printx(ObjectArrays.toString(f.get(null)));
		}
		Source01 o = (Source01) enhancer.create();
		TestConsole.printx(o.hashCode());
		TestConsole.printx(o.toString());
		TestConsole.printx(o.testVArgs(1, 2, 3, 4));
		TestConsole.printx(o.andThen(x -> 123));
		TestConsole.printx(o.apply(null));
		TestConsole.printx(((Function) o).apply(null));
		TestConsole.printx(o.intVal(123));

		((Consumer)o).accept(123);
		TestConsole.printx(((Supplier)o).get());
	}
}
