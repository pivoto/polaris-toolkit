package io.polaris.core.io;

import org.junit.jupiter.api.Test;

class FilenamesTest {

	@Test
	void test01() {
		Consoles.println(Filenames.normalizePath("//aaa//bbb/\\ccc\\ddd"));
		Consoles.println(Filenames.normalizePath("//aaa/../bbb/\\ccc\\ddd"));
		Consoles.println(Filenames.normalizePath("//aaa/../../bbb/\\ccc\\ddd"));
		Consoles.println(Filenames.normalizePath("..//aaa/../bbb/../\\ccc\\ddd"));
		Consoles.println(Filenames.normalizePath("..//aaa/../bbb/./././\\ccc\\ddd"));

	}
}
