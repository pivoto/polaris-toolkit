package io.polaris.validation.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.map.Maps;
import io.polaris.validation.InString;
import io.polaris.validation.Validations;

/**
 * @author Qt
 * @since Dec 03, 2024
 */
public class InStringValidator implements ConstraintValidator<InString, String> {

	private Set<String> values;

	@Override
	public void initialize(InString constraintAnnotation) {
		values = new HashSet<>();
		values.addAll(Arrays.asList(constraintAnnotation.value()));
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		// 校验通过
		if (values.contains(value)) {
			return true;
		}
		// 校验不通过，自定义提示语句（因为，注解上的 value 是枚举类，无法获得枚举类的实际值）
		context.disableDefaultConstraintViolation(); // 禁用默认的 message 的值
		Map<String, Object> map = Maps.newHashMap();
		map.put("value",values.toString());
		String message = Validations.formatValidationMessage(context.getDefaultConstraintMessageTemplate(), map);
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation(); // 重新添加错误提示语句
		return false;
	}

}
