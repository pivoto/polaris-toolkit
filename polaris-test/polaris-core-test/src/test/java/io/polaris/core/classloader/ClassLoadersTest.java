package io.polaris.core.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import io.polaris.core.io.Consoles;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import io.polaris.core.log.support.DynamicLoggerResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ClassLoadersTest {
	URLClassLoader log4jClassLoader = newLog4jClassLoader();

	@BeforeAll
	public static void beforeAll() {
		System.setProperty(DynamicLoggerResolver.PREFER_DYNAMIC_SLF4J, "true");
		Loggers.setResolver(new DynamicLoggerResolver());
	}

	private static URLClassLoader newLog4jClassLoader() {
		String prefix = "file:" + System.getProperty("user.home") + "/.m2/repository/";
		try {
			URLClassLoader cl = new DynamicURLClassLoader(Thread.currentThread().getContextClassLoader()
				,new URL[]{
				new URL(prefix + "org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"),
				new URL(prefix + "org/apache/logging/log4j/log4j-slf4j-impl/2.17.2/log4j-slf4j-impl-2.17.2.jar"),
				new URL(prefix + "org/apache/logging/log4j/log4j-core/2.17.2/log4j-core-2.17.2.jar"),
				new URL(prefix + "org/apache/logging/log4j/log4j-api/2.17.2/log4j-api-2.17.2.jar")
			});
			return cl;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void test03() {
		String msg3 = String.format("origin: %s %n", Thread.currentThread().getContextClassLoader());
		Consoles.print(msg3);
		Thread.currentThread().setContextClassLoader(log4jClassLoader);
		String msg2 = String.format("loader: %s %n", Thread.currentThread().getContextClassLoader());
		Consoles.print(msg2);

		for (int i = 0; i < 2; i++) {
			new Thread(() -> {
				String msg1 = String.format("%s loader: %s %n", Thread.currentThread().getName(), Thread.currentThread().getContextClassLoader());
				Consoles.print(msg1);

				new Thread(() -> {
					String msg = String.format("%s loader: %s %n", Thread.currentThread().getName(), Thread.currentThread().getContextClassLoader());
					Consoles.print(msg);
				}).start();

			}).start();
		}
	}

	@Test
	void test02() throws ClassNotFoundException {
		ClassLoader classLoader = ClassLoaders.INSTANCE.newTargetSideClassLoader(log4jClassLoader);
		Class<?> c = classLoader.loadClass("org/slf4j/LoggerFactory".replace("/", "."));
		Object[] args = new Object[]{c, c.getClassLoader()};
		Consoles.println(args);
	}

	@Test
	void test01() throws Exception {
//		ClassLoaders.INSTANCE.prependClassLoader(log4jClassLoader);
		ILogger logger = Loggers.of("root");
		Consoles.log("", logger);
		logger.debug("xxxx");
		logger.info("xxxx");
		logger.warn("xxxx");
		logger.error("xxxx");


		ClassLoaders.INSTANCE.prependClassLoader(log4jClassLoader);
		ClassLoaders.INSTANCE.prependClassLoader(newLog4jClassLoader());
		{
			try {
				Class<?> c = ClassLoaders.INSTANCE.loadClass("org/slf4j/LoggerFactory".replace("/", "."));
				Object[] args = new Object[]{c, c.getClassLoader()};
				Consoles.println(args);
			} catch (ClassNotFoundException e) {
				Consoles.printStackTrace(e);
			}
		}

		logger = Loggers.of("root");
		Consoles.log("", logger);
		logger.debug("xxxx");
		logger.info("xxxx");
		logger.warn("xxxx");
		logger.error("xxxx");

	}

}
