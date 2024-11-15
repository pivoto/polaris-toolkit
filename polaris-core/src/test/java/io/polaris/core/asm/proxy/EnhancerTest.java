package io.polaris.core.asm.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.collection.ObjectArrays;
import io.polaris.core.io.Consoles;
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
			String msg = ObjectArrays.toString(f.get(null));
			Consoles.log(msg);
		}
		{
			Field f = c.getDeclaredField("GENERATED$STATIC_INTERCEPTOR");
			f.setAccessible(true);
			String msg = ObjectArrays.toString(f.get(null));
			Consoles.log(msg);
		}
		Source01 o = (Source01) enhancer.create();
		Object[] args5 = new Object[]{o.hashCode()};
		Consoles.log("", args5);
		String msg = o.toString();
		Consoles.log(msg);
		Object[] args4 = o.testVArgs(1, 2, 3, 4);
		Consoles.log("", args4);
		Consoles.log("", o.andThen(x -> 123));
		Object[] args3 = new Object[]{o.apply(null)};
		Consoles.log("", args3);
		Object[] args2 = new Object[]{((Function) o).apply(null)};
		Consoles.log("", args2);
		Object[] args1 = new Object[]{o.intVal(123)};
		Consoles.log("", args1);

		((Consumer)o).accept(123);
		Object[] args = new Object[]{((Supplier)o).get()};
		Consoles.log("", args);

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
			String msg = ObjectArrays.toString(f.get(null));
			Consoles.log(msg);
		}
		{
			Field f = c.getDeclaredField("GENERATED$STATIC_INTERCEPTOR");
			f.setAccessible(true);
			String msg = ObjectArrays.toString(f.get(null));
			Consoles.log(msg);
		}
		Source01 o = (Source01) enhancer.create();
		Object[] args5 = new Object[]{o.hashCode()};
		Consoles.log("", args5);
		String msg = o.toString();
		Consoles.log(msg);
		Object[] args4 = o.testVArgs(1, 2, 3, 4);
		Consoles.log("", args4);
		Consoles.log("", o.andThen(x -> 123));
		Object[] args3 = new Object[]{o.apply(null)};
		Consoles.log("", args3);
		Object[] args2 = new Object[]{((Function) o).apply(null)};
		Consoles.log("", args2);
		Object[] args1 = new Object[]{o.intVal(123)};
		Consoles.log("", args1);

		((Consumer)o).accept(123);
		Object[] args = new Object[]{((Supplier)o).get()};
		Consoles.log("", args);
	}
}
