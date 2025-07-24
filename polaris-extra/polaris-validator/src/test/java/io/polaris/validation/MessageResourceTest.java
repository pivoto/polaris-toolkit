package io.polaris.validation;

import io.polaris.core.err.MessageException;
import io.polaris.core.io.Consoles;
import io.polaris.core.msg.MessageResources;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Jul 23, 2025
 */
public class MessageResourceTest {

	@Test
	void test01() {

		Consoles.println(MessageResources.format("test: {0} {1} {2}", "test1", new Object[]{"arg0", "arg1", Strings.asMap("a", "1", "b", "2", "c", "3")}));
		Consoles.println(MessageResources.format("test: {0} {1} {2} {3}", "test1", new Object[]{"arg0", "arg1", Strings.asMap("a", "1", "b", "2", "c", "3")}));
		Consoles.println(MessageResources.format("test: {0} {1} {2} {3} {4} {5}", "test1", new Object[]{"arg0", "arg1", Strings.asMap("a", "1", "b", "2", "c", "3")}));
		Consoles.println(MessageResources.format("test: {a} {b} {} {} {} {}", "test1", new Object[]{"arg0", "arg1", Strings.asMap("a", "1", "b", "2", "c", "3")}));
		Consoles.println(MessageResources.format("test: {a} {b} {} {} {} {}", "test1", new Object[]{"arg0", "arg1", Strings.asMap("a", "1", "b", "2", "c", "3")}));
	}

	@Test
	void test02() {
		Consoles.println(MessageResources.getDefaultMessageResource().getMessage("javax.validation.constraints.Min.message", new Object[]{Strings.asMap("value", "1")}));
	}

	@Test
	void test03() {
		try {
			throw new MessageException("javax.validation.constraints.Min.message", "测试异常消息");
		} catch (Exception e) {
			Consoles.printStackTrace(e);
		}
	}
}
