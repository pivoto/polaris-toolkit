package io.polaris.builder.code;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * @author Qt
 * @since 1.8
 */
public class CodeTest {
	private String targetDir;
	@BeforeEach
	void beforeEach(){
		String dir = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
		targetDir = dir.replaceFirst("test-classes/$","");
		System.out.println(targetDir);
	}


	@Test
	void test01() throws Exception {
		Codes.generate("src/test/resources/code/test.code.xml",
			"src/test/resources/code/test.jdbc.xml",
			"target/code/test.data.xml");
	}
}
