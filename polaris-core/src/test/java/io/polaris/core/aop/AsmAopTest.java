package io.polaris.core.aop;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.aop.sample.ProxyTargetBean;
import io.polaris.core.asm.internal.Signature;
import io.polaris.core.io.Consoles;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.ValueRef;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Qt
 * @since Apr 09, 2026
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AsmAopTest {
	static Logger log = Loggers.of(AsmAopTest.class);


	@Test
	@Order(1)
	void testBefore() throws Exception {
		BeforeAdvice beforeAdvice = (target, method, args) -> log.warn("before: {}, {}, {}", target, method, args);
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(beforeAdvice)
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(2)
	void testAfter() throws Exception {
		AfterAdvice afterAdvice = (target, method, args, retVal) -> log.warn("after: {}, {}, {}", target, method, args);
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(afterAdvice)
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(3)
	void testFixed() throws Exception {
		FixedAdvice fixedAdvice = (target, method, args) -> {
			log.warn("fixedValue: {}, {}, {}", target, method, args);
			return ValueRef.of("fixedValue");
		};
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(fixedAdvice)
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		log.info("", ((Supplier) proxy).get());
		((Consumer) proxy).accept("hello");
		log.info("", ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(4)
	void testAround() throws Exception {
		AroundAdvice aroundAdvice = (target, method, args, invocation) -> {
			log.warn("around before: {}, {}, {}, {}", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			log.warn("around after: {}, {}, {}, {}", target, method, args, invocation);
			return rs;
		};
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(aroundAdvice)
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(5)
	void testThrowing() throws Exception {
		ThrowingAdvice throwingAdvice = (target, method, args, e) -> {
			log.warn("throwing: {}, {}, {}, {}", target, method, args, Objects.toString(e,""));
		};
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(throwingAdvice)
			.get();

		try {
			((ProxyTargetBean) proxy).raise(1, 2);
		} catch (Exception e) {
			log.error("", e);
		}
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(6)
	void testFinally() throws Exception {
		FinallyAdvice finallyAdvice = (target, method, args, retVal, e) -> {
			log.warn("finally: {}, {}, {}, {}, {}", target, method, args, retVal, Objects.toString(e,""));
		};
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(finallyAdvice)
			.get();

		try {
			((ProxyTargetBean) proxy).raise(1, 2);
		} catch (Exception e) {
			log.error("", e);
		}
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(7)
	void testMatch() throws Exception {
		BeforeAdvice beforeAdvice = (target, method, args) -> log.warn("before: {}, {}, {}", target, method, args);
		AfterAdvice afterAdvice = (target, method, args, retVal) -> log.warn("after: {}, {}, {}", target, method, args);
		FixedAdvice fixedAdvice = (target, method, args) -> {
			log.warn("fixedValue: {}, {}, {}", target, method, args);
			return ValueRef.of("fixedValue");
		};
		AroundAdvice aroundAdvice = (target, method, args, invocation) -> {
			log.warn("around before: {}, {}, {}, {}", target, method, args, invocation);
			Object rs = invocation.invoke(target, args);
			log.warn("around after: {}, {}, {}, {}", target, method, args, invocation);
			return rs;
		};
		ThrowingAdvice throwingAdvice = (target, method, args, e) -> {
			log.warn("throwing: {}, {}, {}, {}", target, method, args, Objects.toString(e,""));
		};
		FinallyAdvice finallyAdvice = (target, method, args, retVal, e) -> {
			log.warn("finally: {}, {}, {}, {}, {}", target, method, args, retVal, Objects.toString(e,""));
		};

		Predicate<Signature> withGet = signature -> {
			log.warn("match: {}", signature);
			return signature.getName().equals("get");
		};
		Predicate<Signature> withApply = signature -> {
			log.warn("match: {}", signature);
			return signature.getName().equals("apply");
		};

		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(beforeAdvice)
			.addAdvice(withGet, fixedAdvice)
			.addAdvice(withApply, aroundAdvice)
			.addAdvice(afterAdvice)
			.addAdvice(throwingAdvice)
			.addAdvice(finallyAdvice)
			.addAdvice(finallyAdvice)
			.get();
		for (int i = 0; i < 3; i++) {
			log.info("");
			log.info(Strings.repeat("=", 80));
			log.info("");
			try {
				((ProxyTargetBean) proxy).raise(1, 2);
			} catch (Exception ignored) {
			}
			log.info("");
			log.info(Strings.repeat("=", 80));
			log.info("");
			((Supplier) proxy).get();
			log.info("");
			log.info(Strings.repeat("=", 80));
			log.info("");
			((Consumer) proxy).accept("hello");
			log.info("");
			log.info(Strings.repeat("=", 80));
			((Function) proxy).apply("hello");
			log.info("");
			log.info(Strings.repeat("=", 80));
			log.info("");
		}
	}

}
