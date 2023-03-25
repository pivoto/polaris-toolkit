package io.polaris.toolkit.spring.logging.plugin;

import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class PluginDatWriterTest {
	@Test
	public void test01() throws IOException {
		PluginDatWriter writer = new PluginDatWriter();
		final URL location = PluginDatWriterTest.class.getProtectionDomain().getCodeSource().getLocation();
		File file = new File(new File(location.getFile()).getParentFile().getParentFile(), "src/main/resources/META-INF/org/apache/logging/log4j/core/config/plugins/Log4j2Plugins.dat");
		writer.collect();
		try (FileOutputStream fis = new FileOutputStream(file);) {
			writer.write(fis);
		}
	}
}
