package io.polaris.builder.changer;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PackageChangerRunnerTest {

	@Test
	void test01() throws DocumentException, IOException {
		PackageChangerRunner.change(new File("src/assembly/conf/change/change.xml"));
	}

}
