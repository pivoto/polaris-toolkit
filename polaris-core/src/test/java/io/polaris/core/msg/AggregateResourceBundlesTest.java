package io.polaris.core.msg;

import io.polaris.core.TestConsole;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

class AggregateResourceBundlesTest {

	@Test
	void test01() {
		ResourceBundle bundle = null;
		bundle = ResourceBundle.getBundle("io.polaris.Messages");
		TestConsole.println(bundle);
		bundle = AggregateResourceBundles.getBundle(Locale.CHINA, "io.polaris.Messages");
		TestConsole.println(bundle);
		TestConsole.println(bundle.keySet());
	}

	@Test
	void test02() {
		TestConsole.println(MessageResources.getDefaultMessageResource().getMessage("testkey"));
	}
}
