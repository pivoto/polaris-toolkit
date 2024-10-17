package io.polaris.validation;

import javax.validation.ConstraintValidatorContext;

/**
 * 通过实现此接口，实现{@link CustomValidated}自定义验证。
 * <pre>
 * <code>
 *   class MyCustomValidation implements CustomValidation {
 *     public boolean isValid(ConstraintValidatorContext context, Object value, String... arguments) {
 *       // 验证是否通过
 *       if(isInvalid(value)){
 *         // 添加自定义错误信息
 *         context.disableDefaultConstraintViolation();
 *         context.buildConstraintViolationWithTemplate("自定义错误信息")
 *                .addPropertyNode("属性名")
 *                .addConstraintViolation();
 *       }
 *       return true;
 *     }
 *   }
 * </code>
 * </pre>
 *
 * @author Qt
 * @since 1.8
 */
public interface CustomValidation {
	/**
	 * 验证
	 *
	 * @param context
	 * @param value
	 * @param arguments {@link CustomValidated#arguments()}
	 * @return
	 */
	boolean isValid(ConstraintValidatorContext context, Object value, String... arguments);
}
