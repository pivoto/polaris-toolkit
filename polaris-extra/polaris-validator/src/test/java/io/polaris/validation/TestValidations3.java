package io.polaris.validation;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.polaris.core.lang.Objs;
import io.polaris.validation.group.Create;
import io.polaris.validation.group.Delete;
import io.polaris.validation.group.Retrieve;
import io.polaris.validation.group.Update;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since 1.8
 */
public class TestValidations3 {


	@Test
	void test01() throws Exception {
		A a = new A();
		System.out.println(
			Validations.validateQuietly(a)
		);
		Set<ConstraintViolation<A>> set = Validations.getDefaultValidator().validate(a);
		System.out.println(set);
	}

	@Data
	@FieldNameConstants
	@CustomValidated(AValidator.class)
	static class A {
		@NotEmpty(groups = {Create.class, Update.class})
		private String id;
	}

	public static class AValidator implements CustomValidation {
		@Override
		public boolean isValid(ConstraintValidatorContext context, Object value, String... arguments) {
			A o = (A) value;
			if (!Objs.equals(o.getId(), "123456")) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("必须是123456")
					.addBeanNode().inIterable().atKey("aa")
					.addConstraintViolation()
//					.addPropertyNode(A.Fields.id)
//					.addConstraintViolation()
				;
				context.buildConstraintViolationWithTemplate("必须是123456")
					.addPropertyNode(A.Fields.id)
					.addConstraintViolation()
				;
				return false;
			}
			return true;
		}
	}
}
