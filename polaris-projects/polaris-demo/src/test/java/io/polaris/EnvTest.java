package io.polaris;

import io.polaris.core.env.GlobalStdEnv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.StandardEnvironment;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class EnvTest {


	@Test
	void test01() {
		EnumerablePropertySource propertySource = new EnumerablePropertySource("StdEnv") {

			@Override
			public Object getProperty(String name) {
				return GlobalStdEnv.get(name);
			}

			@Override
			public String[] getPropertyNames() {
				return GlobalStdEnv.keys().toArray(new String[0]);
			}
		};

		StandardEnvironment standardEnvironment = new StandardEnvironment();
		standardEnvironment.getPropertySources().addLast(propertySource);

		String key = "key.test";
		String val = "1234";
		Assertions.assertNotNull(standardEnvironment.getProperty("user.home"));
		Assertions.assertNull(standardEnvironment.getProperty(key));
		GlobalStdEnv.set(key, val);
		Assertions.assertEquals(val, standardEnvironment.getProperty(key));
	}

}
