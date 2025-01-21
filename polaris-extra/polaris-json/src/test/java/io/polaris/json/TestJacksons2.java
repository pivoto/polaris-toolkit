package io.polaris.json;

import java.util.Map;

import io.polaris.core.converter.Converters;
import io.polaris.core.io.Consoles;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jan 15, 2025
 */
public class TestJacksons2 {

	@Test
	void test01() {
		TestBean bean = new TestBean();
		bean.setName("test");
		bean.setAge(18);
		bean.setMan(true);
		bean.setScore(99.99);
		bean.setTags(new String[]{"tag1", "tag2"});

		Consoles.log("{}",Jacksons.toJsonString(bean));
		Map map = Jacksons.toJavaObject("{\"name\":\"test\",\"age\":\"18\",\"man\":\"true\",\"score\":\"99.99x\",\"tags\":[\"tag1\"]}", Map.class);
		Consoles.log("{}",map);
		TestBean bean2 = Converters.convert(TestBean.class, map);
		Consoles.log("{}", bean2);
		Consoles.log("{}", Jacksons.toJavaObject("{\"name\":\"test\",\"age\":\"18\",\"man\":\"true\",\"score\":\"99.99\",\"tags\":[\"tag1\"]}", Object.class));
		Consoles.log("{}", Jacksons.toJavaObject("{\"name\":\"test\",\"age\":\"18\",\"man\":\"true\",\"score\":\"99.99\",\"tags\":[\"tag1\"]}", TestBean.class));
		Consoles.log("{}", Jacksons.toJavaObject("null", TestBean.class));
	}


	@Data
	public static class TestBean {
		private String name;
		private int age;
		private boolean man;
		private double score;
		private String[] tags;
	}
}
