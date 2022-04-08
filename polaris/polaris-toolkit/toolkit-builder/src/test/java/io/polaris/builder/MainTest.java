package io.polaris.builder;

import org.junit.Test;

import java.io.IOException;

/**
 * @author Qt
 * @version Oct 08, 2019
 */
public class MainTest {

	private void run(String... args) throws IOException {
		Main.main(args);
	}

	@Test
	public void test() throws IOException {
		run("--jdbcCfg", "/jdbc.xml", "--codegen", "/code.xml");
	}
	@Test
	public void test00() throws IOException {
		run("--jdbcCfg", "/jdbc.xml", "--codegen", "/code.xml", "--xmlData", "/tables.xml");
	}

	@Test
	public void test01() throws IOException {
		run("--jdbcCfg", "/jdbc.xml", "--codegen"
			, MainTest.class.getPackage().getName().replace(".", "/") + "/code.xml");
	}
}
