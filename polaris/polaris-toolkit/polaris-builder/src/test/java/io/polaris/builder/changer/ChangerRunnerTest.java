package io.polaris.builder.changer;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class ChangerRunnerTest {

	@Test
	void test01() throws DocumentException, IOException {
		ChangerRunner.change(new File("src/assembly/conf/change/change.xml"));
	}

	@Test
	void test02() throws DocumentException, IOException {
		ChangerRunner.change(new File("src/assembly/conf/change/change-reverse.xml"));
	}

}
