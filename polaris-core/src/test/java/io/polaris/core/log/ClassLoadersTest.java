package io.polaris.core.log;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import io.polaris.core.classloader.ClassLoaders;
import io.polaris.core.classloader.DynamicURLClassLoader;
import io.polaris.core.io.Consoles;
import io.polaris.core.log.support.DynamicLoggerResolver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 06, 2025
 */
public class ClassLoadersTest {
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
				, new URL[]{
//				new URL(prefix + "org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"), // 在运行依赖中存在时，这里会有加载冲突
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
		Consoles.log(String.format("origin: %s %n", Thread.currentThread().getContextClassLoader()));
		Thread.currentThread().setContextClassLoader(log4jClassLoader);
		Consoles.log(String.format("loader: %s %n", Thread.currentThread().getContextClassLoader()));

		for (int i = 0; i < 2; i++) {
			new Thread(() -> {
				Consoles.log(String.format("%s loader: %s %n", Thread.currentThread().getName(), Thread.currentThread().getContextClassLoader()));

				new Thread(() -> {
					Consoles.log(String.format("%s loader: %s %n", Thread.currentThread().getName(), Thread.currentThread().getContextClassLoader()));
				}).start();

			}).start();
		}
	}

	@Test
	void test02() throws ClassNotFoundException {
		ClassLoader classLoader = ClassLoaders.INSTANCE.newTargetSideClassLoader(log4jClassLoader);
		Class<?> c = classLoader.loadClass("org/slf4j/LoggerFactory".replace("/", "."));
		Consoles.log("", c, c.getClassLoader());
	}

	@Test
	void test01() throws Exception {
		ClassLoaders.INSTANCE.prependClassLoader(log4jClassLoader);
//		ClassLoaders.INSTANCE.prependClassLoader(ClassLoaders.INSTANCE.newTargetSideClassLoader(log4jClassLoader));
		{
			Class<?> c = ClassLoaders.INSTANCE.loadClass("org/slf4j/LoggerFactory".replace("/", "."));
			Consoles.log("", c, c.getClassLoader());
		}

		ILogger logger = Loggers.of("root");
		Consoles.log("", logger);
		logger.debug("xxxx");
		logger.info("xxxx");
		logger.warn("xxxx");
		logger.error("xxxx");


//		ClassLoaders.INSTANCE.prependClassLoader(log4jClassLoader);
		ClassLoaders.INSTANCE.prependClassLoader(newLog4jClassLoader());
		{
			Class<?> c = ClassLoaders.INSTANCE.loadClass("org/slf4j/LoggerFactory".replace("/", "."));
			Consoles.log("", c, c.getClassLoader());
		}

		logger = Loggers.of("root");
		Consoles.log("", logger);
		logger.debug("xxxx");
		logger.info("xxxx");
		logger.warn("xxxx");
		logger.error("xxxx");

	}

}
