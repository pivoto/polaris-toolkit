package io.polaris.json;

import io.polaris.core.io.Consoles;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Nov 21, 2024
 */
public class EnumJsonTest {

	@Test
	void test01() {
		String json = "{\"type\":\"D\"}";
		Demo demo = Jacksons.toJavaObject(json, Demo.class);
		Consoles.log(demo);
		Consoles.log(Jacksons.toJsonString(demo));
	}

	@Test
	void test02() {
		String json = "{\"type\":\"D\"}";
		Demo demo = Fastjsons.toJavaObject(json, Demo.class);
		Consoles.log(demo);
		Consoles.log(Fastjsons.toJsonString(demo));
	}

	@Data
	public static class Demo {
		private Type type;
	}

	public enum Type {
		A, B, C
	}
}
