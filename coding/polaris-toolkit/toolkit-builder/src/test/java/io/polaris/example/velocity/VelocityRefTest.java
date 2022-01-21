package io.polaris.example.velocity;

import org.junit.Test;

import java.lang.reflect.Field;

/**
 * @author Qt
 * @version Jul 26, 2019
 */
public class VelocityRefTest {
	@Test
	public void test01() throws  Exception {
		Class<?> c = Class.forName("java.util.Locale");
		Field f = c.getField("ENGLISH");
		System.out.println(f.get(null));
	}
}
