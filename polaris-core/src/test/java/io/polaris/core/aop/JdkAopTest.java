package io.polaris.core.aop;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.polaris.core.aop.sample.ProxyTargetBean;
import io.polaris.core.aop.sample.Raiseable;
import io.polaris.core.aop.sample.Testable;
import io.polaris.core.asm.internal.Signature;
import io.polaris.core.function.ThrowableRunnable;
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
public class JdkAopTest {
	static Logger log = Loggers.of(JdkAopTest.class);
	BeforeAdvice beforeAdvice = (proxy, target, method, args) -> log.warn("Before: {}, {}", Signature.of(method), args);
	AfterAdvice afterAdvice = (proxy, target, method, args, retVal) -> log.warn("After: {}, {}", target, Signature.of(method), args);
	FixedAdvice fixedAdvice = (proxy, target, method, args) -> {
		log.warn("FixedValue: {}, {}", Signature.of(method), args);
		return ValueRef.of("fixedValue");
	};
	AroundAdvice aroundAdvice = (proxy, target, method, args, invocation) -> {
		log.warn("Around Before: {}, {}, {}", Signature.of(method), args, invocation);
		Object rs = invocation.invoke(target, args);
		log.warn("Around After: {}, {}, {}", Signature.of(method), args, invocation);
		return rs;
	};
	ThrowingAdvice throwingAdvice = (proxy, target, method, args, e) -> {
		log.warn("Throwing: {}, {}, {}", Signature.of(method), args, Objects.toString(e, ""));
	};
	FinallyAdvice finallyAdvice = (proxy, target, method, args, retVal, e) -> {
		log.warn("Finally: {}, {}, {}, {}", Signature.of(method), args, retVal, Objects.toString(e, ""));
	};

	private void execute(ThrowableRunnable executable) {
		log.info(Strings.repeat("-", 80));
		try {
			executable.run();
		} catch (Throwable e) {
			log.error("发现异常：{}", e);
		}
		log.info(Strings.repeat("~", 80));
		log.info("");
		log.info("");
	}

	@Test
	@Order(1)
	void testBefore() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(beforeAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(2)
	void testAfter() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(afterAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(3)
	void testFixed() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(fixedAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(4)
	void testAround() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(aroundAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(5)
	void testThrowing() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(throwingAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(6)
	void testFinally() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(new ProxyTargetBean())
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(finallyAdvice)
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

	@Test
	@Order(7)
	void testMatch() throws Exception {
		Predicate<Signature> withTest = signature -> signature.getName().equals("test");
		Predicate<Signature> withApply = signature -> signature.getName().equals("apply");

		Object proxy = Proxies.jdkProxyFactory(new ProxyTargetBean())
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.addAdvice(beforeAdvice)
			.addAdvice(withTest, fixedAdvice)
			.addAdvice(withApply, aroundAdvice)
			.addAroundAdvice((proxy0, target, method, args, invocation) -> {
				log.warn("around2 before: {}, {}, {}, {}", target, method, args, invocation);
				Object rs = invocation.invoke(target, args);
				log.warn("around2 after: {}, {}, {}, {}", target, method, args, invocation);
				return rs;
			})
			.addAdvice(afterAdvice)
			.addAdvice(throwingAdvice)
			.addAdvice(finallyAdvice)
			.addAdvice(finallyAdvice)
			.override(Signature.of("get", Object.class), args -> {
				throw new UnsupportedOperationException();
			})
			.get();

		execute(() -> ((Testable) proxy).test(1, 2));
		execute(() -> ((Raiseable) proxy).raise(1, 2));
		execute(() -> ((Supplier) proxy).get());
		execute(() -> ((Consumer) proxy).accept("hello"));
		execute(() -> ((Function) proxy).apply("hello"));
	}

}
