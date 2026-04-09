package io.polaris.core.aop;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.aop.sample.ProxyTargetBean;
import io.polaris.core.io.Consoles;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.random.Randoms;
import org.junit.jupiter.api.DisplayName;
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
public class AsmProxyTest {
	static Logger log = Loggers.of(AsmProxyTest.class);


	@Test
	@Order(1)
	void testProxyAdditionalInterfaces() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept("hello");
		((Function) proxy).apply("hello");
	}

	@Test
	@Order(2)
	void testProxyDefaultMethod() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.defaults(Function.class.getMethod("apply", Object.class), args -> {
				log.info("default apply... {}", args);
				return "default";
			})
			.get();

		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept(new Object[]{"hello", "world"});
		((Function) proxy).apply(new Object[]{"hello", "world"});
	}


	@Test
	@Order(3)
	void testProxyOverrideMethod() throws Exception {
		ProxyTargetBean target = new ProxyTargetBean();
		Object proxy = Proxies.proxyFactory(target)
			.interfaces(new Class[]{Supplier.class, Function.class, Consumer.class})
			.defaults(Function.class.getMethod("apply", Object.class), args -> {
				log.info("default apply... {}", args);
				return "default";
			})
			.override(Function.class.getMethod("apply", Object.class), args -> {
				log.info("override apply... {}", args);
				return "override";
			})
			.override((method)->{
				if (method.getName().equals("accept") && method.getParameterCount() == 1 && method.getParameterTypes()[0] == Object.class){
					return (args) -> {
						log.info("override accept... {}", args);
						return null;
					};
				}
				return null;
			})
//			.override(Consumer.class.getMethod("accept", Object.class), args -> {
//				log.info("override accept... {}", args);
//				return null;
//			})
//			.override(ProxyTargetBean.class.getMethod("accept", Object.class), args -> {
//				log.info("override accept... {}", args);
//				return null;
//			})
			.override(Consumer.class.getMethod("andThen", Consumer.class), args -> {
				log.info("override andThen... {}", args);
				return null;
			})
			.override(ProxyTargetBean.class.getMethod("test", int.class, int.class), args -> {
				log.info("override test... {}", args);
				return null;
			})
			.get();
		((ProxyTargetBean) proxy).test(1, 2);
		((Supplier) proxy).get();
		((Consumer) proxy).accept(new Object[]{"hello", "world"});
		((Consumer) proxy).andThen(args -> log.info("andThen... {}", args));
		((Function) proxy).apply(new Object[]{"hello", "world"});
	}


}
