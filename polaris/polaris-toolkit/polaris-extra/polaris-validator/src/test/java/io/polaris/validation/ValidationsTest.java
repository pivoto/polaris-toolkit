package io.polaris.validation;

import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidator;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

class ValidationsTest {


	@Test
	void test01() throws NoSuchMethodException {
		List<Class<? extends ConstraintValidator<Numeric, ?>>> list
			= Validations.getConstraintValidator(Numeric.class, String.class);
		System.out.println(list);

		System.out.println(Arrays.toString(getClass().getDeclaredClasses()));


		B b = new B();
		b.setBigDecimal(new BigDecimal("3.14"));
		b.setDoubleVal(123d);

		Validations.validate(b);

	}

	@Data
	static class B {
		@DecimalScale(min = 0, max = 1)
		private BigDecimal bigDecimal;

		@Max(100)
		private Double doubleVal;
	}
}

