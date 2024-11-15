package io.polaris.core.lang.copier;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CopyOptionsTest {

	@Test
	void test01() {
		Consoles.log(CopyOptions.DEFAULT.converter());

		CopyOptions a = CopyOptions.create()
			.ignoreCapitalize(true)
			.ignoreCase(true)
			.immutable();
		Consoles.log(a);
		Consoles.log(a.clone());
		Consoles.log(a.clone());
		Consoles.log(a.equals(a.clone()));
		Consoles.log(a.clone().equals(a.clone()));
	}
}
