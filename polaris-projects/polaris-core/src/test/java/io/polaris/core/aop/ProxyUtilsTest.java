package io.polaris.core.aop;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.TestConsole;
import io.polaris.core.asm.BaseAsmTest;
import io.polaris.core.random.Randoms;
import io.polaris.core.tuple.ValueRef;
import org.junit.jupiter.api.Test;

public class ProxyUtilsTest extends BaseAsmTest {

	@Test
	void test01() {
		BeforeAdvice beforeAdvice = (target, method, args) -> TestConsole.printx("before: ", target, method, args);
		AfterAdvice afterAdvice = (target, method, args, retVal) -> TestConsole.printx("after: ", target, method, args);
		FixedAdvice fixedAdvice = (target, method, args) -> {
			TestConsole.printx("fixedValue: ", target, method, args);
			return null;
		};
		AroundAdvice aroundAdvice1 = (target, method, args, invocation) -> {
			TestConsole.printx("around1 before: ", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			TestConsole.printx("around1 after: ", target, method, args, invocation);
			return rs;
		};
		AroundAdvice aroundAdvice2 = (target, method, args, invocation) -> {
			TestConsole.printx("around2 before: ", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			TestConsole.printx("around2 after: ", target, method, args, invocation);
			return rs;
		};
		ThrowingAdvice throwingAdvice = (target, method, args, e) -> TestConsole.printx("throwing: ", target, method, args, e);
		FinallyAdvice finallyAdvice = (target, method, args, retVal, e) -> TestConsole.printx("finally: ", target, method, args, retVal, e);

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
			TestConsole.printx("exec: \n\t{}", proxy.get());
			TestConsole.printx("exec: \n\t{}", proxy.toString());
			TestConsole.printx("exec: \n\t{}", proxy.hashCode());
			TestConsole.printx("exec: \n\t{}", proxy.equals(target));
		}
		{
			Supplier proxy = ProxyUtils.jdkProxyFactory(target)
				.interfaces(new Class[]{Supplier.class})
				.addAdvice(predicate,
					beforeAdvice, afterAdvice, fixedAdvice, aroundAdvice1, aroundAdvice2, throwingAdvice, finallyAdvice)
				.get();
			TestConsole.printx("exec: \n\t{}", proxy.get());
			TestConsole.printx("exec: \n\t{}", proxy.toString());
			TestConsole.printx("exec: \n\t{}", proxy.hashCode());
			TestConsole.printx("exec: \n\t{}", proxy.equals(target));
		}
	}
}
