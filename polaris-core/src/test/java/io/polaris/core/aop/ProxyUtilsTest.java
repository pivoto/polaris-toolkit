package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

public class ProxyUtilsTest extends BaseAsmTest {

	@Test
	void test01() {
		BeforeAdvice beforeAdvice = (target, method, args) -> Consoles.log("before: ", target, method, args);
		AfterAdvice afterAdvice = (target, method, args, retVal) -> Consoles.log("after: ", target, method, args);
		FixedAdvice fixedAdvice = (target, method, args) -> {
			Consoles.log("fixedValue: ", target, method, args);
			return null;
		};
		AroundAdvice aroundAdvice1 = (target, method, args, invocation) -> {
			Consoles.log("around1 before: ", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			Consoles.log("around1 after: ", target, method, args, invocation);
			return rs;
		};
		AroundAdvice aroundAdvice2 = (target, method, args, invocation) -> {
			Consoles.log("around2 before: ", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			Consoles.log("around2 after: ", target, method, args, invocation);
			return rs;
		};
		ThrowingAdvice throwingAdvice = (target, method, args, e) -> Consoles.log("throwing: ", target, method, args, e);
		FinallyAdvice finallyAdvice = (target, method, args, retVal, e) -> Consoles.log("finally: ", target, method, args, retVal, e);

		Predicate<Method> predicate = method -> {
			return method.getName().equals("toString")
				|| method.getName().equals("get");
		};

		ProxyTarget01 target = new ProxyTarget01();
		{
			ProxyTarget01 proxy = ProxyUtils.proxyFactory(target)
				.interfaces(new Class[]{Supplier.class})
				.addAdvice(predicate,
					beforeAdvice, afterAdvice, fixedAdvice, aroundAdvice1, aroundAdvice2, throwingAdvice, finallyAdvice)
				.get();
			Object[] args2 = new Object[]{proxy.get()};
			Consoles.log("exec: \n\t{}", args2);
			Object[] args1 = new Object[]{proxy.toString()};
			Consoles.log("exec: \n\t{}", args1);
			Object[] args = new Object[]{proxy.hashCode()};
			Consoles.log("exec: \n\t{}", args);
			Consoles.log("exec: \n\t{}", proxy.equals(target));
		}
		{
			Supplier proxy = ProxyUtils.jdkProxyFactory(target)
				.interfaces(new Class[]{Supplier.class})
				.addAdvice(predicate,
					beforeAdvice, afterAdvice, fixedAdvice, aroundAdvice1, aroundAdvice2, throwingAdvice, finallyAdvice)
				.get();
			Object[] args2 = new Object[]{proxy.get()};
			Consoles.log("exec: \n\t{}", args2);
			Object[] args1 = new Object[]{proxy.toString()};
			Consoles.log("exec: \n\t{}", args1);
			Object[] args = new Object[]{proxy.hashCode()};
			Consoles.log("exec: \n\t{}", args);
			Consoles.log("exec: \n\t{}", proxy.equals(target));
		}
	}
}
