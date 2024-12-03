package io.polaris.validation;

import java.math.BigDecimal;
import java.util.List;

import io.polaris.core.collection.Lists;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class InEnumTest {

	@Test
	void test01() {
		A a = new A();
		Consoles.log("{}", Validations.validateQuietly(a));
		a.value = "C";
		a.list = Lists.asList("C");
		a.array = new String[]{"C"};
		Consoles.log("{}", Validations.validateQuietly(a));
		a.array = new String[]{};
		Consoles.log("{}", Validations.validateQuietly(a));
	}


	public static class A {

		@InEnum(value = {E.class, E.class})
		@Regexp(value = "\\d+")
		private String value;
		@InEnum(value = {E.class, E.class})
		@Regexp(value = "\\d+")
		private List<String> list;
		@InEnum(value = {E.class, E.class})
		@Regexp(value = "\\d+")
		private String[] array;
		@InNumber(10)
		private int v1;
		@InNumber(10)
		private long v2;
		@InNumber({10, 20})
		private BigDecimal[] v3 = new BigDecimal[]{new BigDecimal(10)};
	}

	public enum E {
		A, B
	}
}
