package io.polaris.builder.changer;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

class ChangerTest {

	@Test
	void doChange() throws DocumentException, IOException, NoSuchAlgorithmException {
		ChangerRunner.change(new File("src/assembly/conf/change/change.xml"));
	}

	@Test
	void doChangeReverse() throws DocumentException, IOException, NoSuchAlgorithmException {
		ChangerRunner.change(new File("src/assembly/conf/change/change-reverse.xml"));
	}

}
