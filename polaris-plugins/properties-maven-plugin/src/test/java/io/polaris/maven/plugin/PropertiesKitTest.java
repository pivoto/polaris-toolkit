package io.polaris.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertiesKitTest {
	private String targetDir;

	@BeforeEach
	void beforeEach() {
		String dir = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		targetDir = dir.replaceFirst("test-classes/$", "");
		System.out.println("targetDir: " + targetDir);
	}

	@Test
	void test01() throws IOException {
		File file = new File(targetDir + "test.properties");
		PropertiesKit.writeBytes(file,
			( //"#测试\r\n" +
				"test=测试")
				.getBytes(StandardCharsets.UTF_8));

		SystemStreamLog log = new SystemStreamLog();
		PropertiesKit.nativeToAscii(log,file, "UTF-8");
	}
}
