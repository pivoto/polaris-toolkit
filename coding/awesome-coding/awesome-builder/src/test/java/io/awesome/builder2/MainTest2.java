package io.awesome.builder2;

import io.awesome.builder.Main;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Qt
 * @version Mar 10, 2020
 */
public class MainTest2 {

	private void run(String... args) throws IOException {
		Main.main(args);
	}

	@Test
	public void test01() throws IOException {
		run("--jdbcCfg", getClass().getPackage().getName().replace(".", "/") + "/jdbc.xml",
			"--codegen", getClass().getPackage().getName().replace(".", "/") + "/code.xml");

		System.out.println(new File(".").getAbsolutePath());
	}
}
