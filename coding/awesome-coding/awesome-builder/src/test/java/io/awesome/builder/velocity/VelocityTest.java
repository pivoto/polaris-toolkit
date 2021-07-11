package io.awesome.builder.velocity;

import io.awesome.builder.velocity.VelocityTemplate;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.junit.Test;

import java.io.StringWriter;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
public class VelocityTest {

	@Test
	public void test01() {
		StringWriter writer = new StringWriter();
		Context context = new VelocityContext();
		context.put("a", 1);
		VelocityTemplate.merge("io/awesome/builder/velocity/test.vm", "utf-8", context, writer);
		System.out.println(writer.toString());
	}

}
