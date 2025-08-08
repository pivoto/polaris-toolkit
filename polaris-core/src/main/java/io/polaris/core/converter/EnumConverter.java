package io.polaris.core.converter;

import java.lang.reflect.Method;

import io.polaris.core.lang.JavaType;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since 1.8
 */
public class EnumConverter<T extends Enum<T>> extends AbstractSimpleConverter<T> {
	private static final ILogger log = Loggers.of(EnumConverter.class);
	private final static String[] parseMethodNames = new String[]{"parseOf", "parse", "of",};
	private final JavaType<T> targetType;
	private final Class<T> enumClass;
	private final Method parseMethod;

	public EnumConverter(Class<T> enumClass) {
		this.targetType = JavaType.of(enumClass);
		this.enumClass = enumClass;

		Method method = null;
		for (String name : parseMethodNames) {
			method = Reflects.getInheritableStaticMethod(enumClass, name, new Class[]{String.class}, enumClass);
			if (method != null) {
				break;
			}
		}
		this.parseMethod = method;
	}

	@Override
	public JavaType<T> getTargetType() {
		return targetType;
	}

	@Override
	protected T doConvert(Object value, JavaType<T> targetType) {
		if (value == null) {
			return null;
		}
		if (value instanceof Number) {
			int i = ((Number) value).intValue();
			T[] enumConstants = enumClass.getEnumConstants();
			return i >= 0 && i < enumConstants.length ? enumConstants[i] : null;
		} else {
			String str = asSimpleString(value);
			T rs = null;
			if (parseMethod != null) {
				try {
					rs = Reflects.invokeStatic(parseMethod, str);
					if (rs != null) {
						return rs;
					}
				} catch (ReflectiveOperationException e) {
					if (log.isDebugEnabled()) {
						log.debug("枚举类型转换失败：{}", e.getMessage(), e);
					}
				}
			}
			try {
				rs = Enum.valueOf(targetType.getRawClass(), str);
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("枚举类型转换失败：{}", e.getMessage(), e);
				}
			}
			return rs;
		}
	}

}
