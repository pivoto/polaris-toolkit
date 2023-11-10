package io.polaris.core.msg;

import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

class AggregateResourceBundlesTest {

	@Test
	void test01() {
		ResourceBundle bundle = null;
		bundle = ResourceBundle.getBundle("io.polaris.Messages");
		System.out.println(bundle);
		bundle = AggregateResourceBundles.getBundle(Locale.CHINA, "io.polaris.Messages");
		System.out.println(bundle);
		System.out.println(bundle.keySet());
	}

	@Test
	void test02() {
		System.out.println(MessageResources.getDefaultMessageResource().getMessage("testkey"));
	}
}
