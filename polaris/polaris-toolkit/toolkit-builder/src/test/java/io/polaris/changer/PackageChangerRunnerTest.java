package io.polaris.changer;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PackageChangerRunnerTest {

	@Test
	void test01() throws DocumentException, IOException {
		PackageChangerRunner.change(getClass().getResourceAsStream("change.xml"));
	}
}
