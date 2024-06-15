package io.polaris.validation;

import io.polaris.validation.group.Create;
import io.polaris.validation.group.Delete;
import io.polaris.validation.group.Retrieve;
import io.polaris.validation.group.Update;
import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
public class TestValidations2 {


	@Test
	void test01() throws Exception {
		A a = new A();
		System.out.println(
			Validations.validateQuietly(a, Create.class)
		);
		System.out.println(
			Validations.validateQuietly(a, Update.class)
		);
		System.out.println(
			Validations.validateQuietly(a, Delete.class)
		);
		System.out.println(
			Validations.validateQuietly(a, Update.class, Retrieve.class)
		);
		System.out.println(
			Validations.validateQuietly(a, Create.class, Retrieve.class)
		);
	}

	@Test
	void test02() throws Exception {
		A a = new A();
		Method method = A.class.getMethod("run", new Class[]{String.class, List.class});
		Set<ConstraintViolation<A>> set = Validations.getDefaultValidator().forExecutables().validateParameters(a, method,
			new Object[]{null, new ArrayList<>()},
			Default.class, Create.class);
		for (ConstraintViolation<A> v : set) {
			System.out.printf("%s - %s : %s %s%n", v.getRootBeanClass(), v.getPropertyPath().toString(), v.getMessage(), v.getMessageTemplate());
		}
		System.out.println(Validations.formatValidationMessage("{javax.validation.constraints.Min.message} {ddd} xx", new HashMap<>()));
	}


	@Data
	static class A {
		@NotEmpty(groups = {Create.class, Update.class})
		private String id;

		public void run(@NotNull String id, @NotNull List<String> ids) {

		}
	}
}
