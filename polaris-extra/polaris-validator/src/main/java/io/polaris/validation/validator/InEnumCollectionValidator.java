package io.polaris.validation.validator;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.polaris.core.map.Maps;
import io.polaris.validation.InEnum;
import io.polaris.validation.Validations;

/**
 * @author Qt
 * @since Dec 03, 2024
 */
public class InEnumCollectionValidator implements ConstraintValidator<InEnum, Collection<String>> {

	private Set<String> values;

	@Override
	public void initialize(InEnum constraintAnnotation) {
		values = new HashSet<>();
		for (Class<? extends Enum<?>> c : constraintAnnotation.value()) {
			Enum<?>[] enums = c.getEnumConstants();
			for (Enum<?> enumConstant : enums) {
				values.add(enumConstant.name());
			}
		}
	}

	@Override
	public boolean isValid(Collection<String> value, ConstraintValidatorContext context) {
		// 为空时，默认不校验，即认为通过
		if (value == null) {
			return true;
		}
		if (value.isEmpty() && values.isEmpty()) {
			return true;
		}
		// 校验通过
		if (values.containsAll(value)) {
			return true;
		}
		// 校验不通过，自定义提示语句（因为，注解上的 value 是枚举类，无法获得枚举类的实际值）
		context.disableDefaultConstraintViolation(); // 禁用默认的 message 的值
		Map<String, Object> map = Maps.newHashMap();
		map.put("value", values.toString());
		String message = Validations.formatValidationMessage(context.getDefaultConstraintMessageTemplate(), map);
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation(); // 重新添加错误提示语句
		return false;
	}

}
