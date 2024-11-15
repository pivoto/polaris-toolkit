package io.polaris.core.msg;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;

class AggregateResourceBundlesTest {

	@Test
	void test01() {
		ResourceBundle bundle = null;
		bundle = ResourceBundle.getBundle("io.polaris.Messages");
		Consoles.println(bundle);
		bundle = AggregateResourceBundles.getBundle(Locale.CHINA, "io.polaris.Messages");
		Consoles.println(bundle);
		Object[] args = new Object[]{bundle.keySet()};
		Consoles.println(args);
	}

	@Test
	void test02() {
		String msg = MessageResources.getDefaultMessageResource().getMessage("testkey");
		Consoles.println(msg);
	}
}
