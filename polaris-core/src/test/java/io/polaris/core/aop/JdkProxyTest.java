package io.polaris.core.aop;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.aop.sample.ProxyTargetBean;
import io.polaris.core.asm.internal.Signature;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
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
public class JdkProxyTest {
	static Logger log = Loggers.of(JdkProxyTest.class);


	@Test
	@Order(1)
	void testProxyAdditionalInterfaces() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.get();

		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(2)
	void testProxyDefaultMethod() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.defaults(Signature.of(Function.class.getMethod("apply", Object.class)), args -> {
				log.info("default apply... {}", args);
				return "default";
			})
			.get();

		((Supplier) proxy).get();
		((Consumer) proxy).accept(new Object[]{"hello", "world"});
		((Function) proxy).apply(new Object[]{"hello", "world"});
	}


	@Test
	@Order(3)
	void testProxyOverrideMethod() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.jdkProxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.defaults(Signature.of(Function.class.getMethod("apply", Object.class)), args -> {
				log.info("default apply... {}", args);
				return "default";
			})
			.override(Signature.of(Function.class.getMethod("apply", Object.class)), args -> {
				log.info("override apply... {}", args);
				return "override";
			})
			.override(Signature.of(Consumer.class.getMethod("accept", Object.class)), args -> {
				log.info("override accept... {}", args);
				return null;
			})
			.override(Signature.of(Consumer.class.getMethod("andThen", Consumer.class)), args -> {
				log.info("override andThen... {}", args);
				return null;
			})
			.get();
		((Supplier) proxy).get();
		((Consumer) proxy).accept(new Object[]{"hello", "world"});
		((Consumer) proxy).andThen(args -> log.info("andThen... {}", args));
		((Function) proxy).apply(new Object[]{"hello", "world"});
	}


}
