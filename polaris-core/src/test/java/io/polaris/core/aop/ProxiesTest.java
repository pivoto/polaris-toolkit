//package io.polaris.core.aop;
//
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//import java.util.Arrays;
//import java.util.function.Consumer;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.function.Supplier;
//
//import io.polaris.core.aop.sample.ProxyTargetBean;
//import io.polaris.core.asm.BaseAsmTest;
//import io.polaris.core.asm.internal.Signature;
//import io.polaris.core.io.Consoles;
//import io.polaris.core.random.Randoms;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//public class ProxiesTest extends BaseAsmTest {
//
//	@Test
//	void testProxy() {
//		BeforeAdvice beforeAdvice = (target, method, args) -> Consoles.log("before: ", target, method, args);
//		AfterAdvice afterAdvice = (target, method, args, retVal) -> Consoles.log("after: ", target, method, args);
//		FixedAdvice fixedAdvice = (target, method, args) -> {
//			Consoles.log("fixedValue: ", target, method, args);
//			return null;
//		};
//		AroundAdvice aroundAdvice1 = (target, method, args, invocation) -> {
//			Consoles.log("around1 before: ", target, method, args, invocation);
//			Object rs = invocation.invoke(target, args);
//			Consoles.log("around1 after: ", target, method, args, invocation);
//			return rs;
//		};
//		AroundAdvice aroundAdvice2 = (target, method, args, invocation) -> {
//			Consoles.log("around2 before: ", target, method, args, invocation);
//			Object rs = invocation.invoke(target, args);
//			Consoles.log("around2 after: ", target, method, args, invocation);
//			return rs;
//		};
//		ThrowingAdvice throwingAdvice = (target, method, args, e) -> Consoles.log("throwing: ", target, method, args, e);
//		FinallyAdvice finallyAdvice = (target, method, args, retVal, e) -> Consoles.log("finally: ", target, method, args, retVal, e);
//
//		Predicate<Method> predicate = method -> {
//			return method.getName().equals("toString")
//				|| method.getName().equals("get");
//		};
//
//		ProxyTargetBean target = new ProxyTargetBean();
//		{
//			ProxyTargetBean proxy = Proxies.proxyFactory(target)
//				.interfaces(new Class[]{Supplier.class})
//				.addAdviogce(predicate,
//					beforeAdvice, afterAdvice, fixedAdvice, aroundAdvice1, aroundAdvice2, throwingAdvice, finallyAdvice)
//				.get();
////			Object[] args2 = new Object[]{proxy.get()};
////			Consoles.log("exec: \n\t{}", args2);
//			Object[] args1 = new Object[]{proxy.toString()};
//			Consoles.log("exec: \n\t{}", args1);
//			Object[] args = new Object[]{proxy.hashCode()};
//			Consoles.log("exec: \n\t{}", args);
//			Consoles.log("exec: \n\t{}", proxy.equals(target));
//		}
//		{
//			Supplier proxy = Proxies.jdkProxyFactory(target)
//				.interfaces(new Class[]{Supplier.class})
//				.addAdvice(predicate,
//					beforeAdvice, afterAdvice, fixedAdvice, aroundAdvice1, aroundAdvice2, throwingAdvice, finallyAdvice)
//				.get();
//			Object[] args2 = new Object[]{proxy.get()};
//			Consoles.log("exec: \n\t{}", args2);
//			Object[] args1 = new Object[]{proxy.toString()};
//			Consoles.log("exec: \n\t{}", args1);
//			Object[] args = new Object[]{proxy.hashCode()};
//			Consoles.log("exec: \n\t{}", args);
//			Consoles.log("exec: \n\t{}", proxy.equals(target));
//		}
//	}
//
//	@Test
//	void testProxy2() throws NoSuchMethodException {
//		ProxyTargetBean target = new ProxyTargetBean();
//		Object proxy = Proxies.proxyFactory(target)
//			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
//			.override(Signature.of(Consumer.class.getDeclaredMethod("accept", Object.class)), args -> {
//				Consoles.println("accept", args);
//				return null;
//			})
//			.override(Signature.of(Function.class.getDeclaredMethod("apply", Object.class)), args -> {
//				Consoles.println("apply", args);
//				return Randoms.randomNumberString(6);
//			})
//			.get();
//		Consoles.println("", ((Supplier) proxy).get());
//		((Consumer) proxy).accept("test");
//		Consoles.println("", ((Function) proxy).apply("test"));
//	}
//
//}
