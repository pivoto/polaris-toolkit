package io.polaris.core.lang.bean;

import io.polaris.core.io.Consoles;

import static org.junit.jupiter.api.Assertions.*;

class CaseModeOptionTest {

	@org.junit.jupiter.api.Test
	void test() {
		assertTrue(CaseModeOption.of(CaseMode.CAMEL).is(CaseMode.CAMEL));
		Consoles.println(CaseModeOption.empty());
		Consoles.println(CaseModeOption.of(1));
		Consoles.println(CaseModeOption.of(2));
		Consoles.println(CaseModeOption.of(3));
		Consoles.println(CaseModeOption.of(4));
		Consoles.println(CaseModeOption.of(5));
		Consoles.println(CaseModeOption.of(6));
		Consoles.println(CaseModeOption.of(7));
		Consoles.println(CaseModeOption.of(7).minus(CaseMode.INSENSITIVE));
		Consoles.println(CaseModeOption.of(7).minus(CaseMode.CAMEL));
		Consoles.println(CaseModeOption.of(7).minus(CaseMode.CAMEL).minus(CaseMode.INSENSITIVE));
	}

}
