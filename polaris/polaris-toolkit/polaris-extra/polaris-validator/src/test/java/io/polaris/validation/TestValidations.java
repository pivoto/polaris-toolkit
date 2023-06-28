package io.polaris.validation;

import io.polaris.core.err.ValidationException;
import io.polaris.validation.validator.RuntimeValidator;
import lombok.Data;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;

class TestValidations {


	@Test
	void test01() throws Exception {
		{
//			Validations.getDefaultValidator().forExecutables().validateParameters()
			List<Class<? extends ConstraintValidator<Max, ?>>> list
				= Validations.getConstraintValidator(Max.class, Long.class);
			System.out.println(list);
			System.out.println(Arrays.toString(getClass().getDeclaredClasses()));
		}
		{
			DecimalScale decimalScale = B.class.getDeclaredField("bigDecimal").getAnnotation(DecimalScale.class);

			Annotation annotation = decimalScale;

			Class<? extends ConstraintValidator> validatorClass = Validations.getFirstConstraintValidator(annotation.annotationType(), BigDecimal.class);

			ConstraintValidator constraintValidator = validatorClass.newInstance();
			constraintValidator.initialize(annotation);

			RuntimeValidator.bind((context, value, formatter) -> {
				if (value instanceof BigDecimal) {

					if (!constraintValidator.isValid(value, context)) {
						Map<String, Object> attrs = new HashMap<>();
						try {
							for (Method method : annotation.annotationType().getDeclaredMethods()) {
								if (method.getParameterCount() > 0
									|| method.getName().equals("hashCode")
									|| method.getName().equals("toString")
									|| method.getName().equals("annotationType")
								) {
									continue;
								}
								Object rs = method.invoke(annotation);
								attrs.put(method.getName(), rs);
							}
						} catch (ReflectiveOperationException e) {
							e.printStackTrace();
						}
						System.out.println(attrs);
						try {
							InvocationHandler invocationHandler = Proxy.getInvocationHandler(decimalScale);
							Field f = invocationHandler.getClass().getDeclaredField("memberValues");
							f.setAccessible(true);
							Map<String, Object> o = (Map) f.get(invocationHandler);
							attrs.putAll(o);
						} catch (ReflectiveOperationException e) {
							e.printStackTrace();
						}
						System.out.println(attrs);
						String msg = formatter.formatValidationMessage((String) attrs.get("message"), context, value, params -> {
							params.putAll(attrs);
						});
						return ValidationResult.error(msg);
					}
				}
				return null;
			});

			R r = new R();
			r.setData(BigDecimal.valueOf(3.14));
			try {
				Validations.validate(r);
			} catch (ValidationException e) {
				e.printStackTrace();
			}
		}

		{
			B b = new B();
			b.setBigDecimal(new BigDecimal("3.14"));
			b.setDoubleVal(123d);
			try {
				Validations.validate(b);
			} catch (ValidationException e) {
				e.printStackTrace();
			}
		}

	}

	@Test
	void test02() {
		B b = new B();
		b.setBigDecimal(BigDecimal.valueOf(1.11));
		b.setDoubleVal(101D);
		W w = new W();
		w.setInput(b);
		Validations.validate(w);
	}
	@Test
	void test03() throws Exception{
		B b = new B();
		b.setBigDecimal(BigDecimal.valueOf(1.11));
		b.setDoubleVal(101D);
		Set<ConstraintViolation<M>> set = Validations.getDefaultValidator().forExecutables().validateParameters(new M(), M.class.getMethod("exec", B.class),
			new Object[]{b});
		System.out.println(set);
	}

	static class M{@Valid
		public Object exec(@Valid B b){
			return null;
		}
	}

	@Data
	static class R {
		@RuntimeValidated
		private Object data;
	}

	@Data
	static class B {
		@DecimalScale(min = 0, max = 1)
		private BigDecimal bigDecimal;
		@Max(100)
		private Double doubleVal;
	}

	@Data
	static class W {
		@Valid
		private Object input;
		@Valid
		private Set<Object> args = new LinkedHashSet<>();
	}
}

