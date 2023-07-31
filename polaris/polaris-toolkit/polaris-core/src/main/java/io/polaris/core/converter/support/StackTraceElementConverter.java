package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.JavaType;

import java.util.Map;

/**
 * @author Qt
 * @since 1.8
 */
public class StackTraceElementConverter extends AbstractSimpleConverter<StackTraceElement> {
	private final JavaType<StackTraceElement> targetType = JavaType.of(StackTraceElement.class);

	@Override
	public JavaType<StackTraceElement> getTargetType() {
		return targetType;
	}

	@Override
	protected StackTraceElement doConvert(Object value, JavaType<StackTraceElement> targetType) {
		if (value instanceof Map) {
			final Map<?, ?> map = (Map<?, ?>) value;

			final String declaringClass = ConverterRegistry.INSTANCE.convertQuietly(String.class, map.get("className"));
			final String methodName = ConverterRegistry.INSTANCE.convertQuietly(String.class, map.get("methodName"));
			final String fileName = ConverterRegistry.INSTANCE.convertQuietly(String.class, map.get("fileName"));
			final Integer lineNumber = ConverterRegistry.INSTANCE.convertQuietly(Integer.class, map.get("lineNumber"));

			return new StackTraceElement(declaringClass, methodName, fileName, lineNumber == null ? 0 : lineNumber);
		}
		return null;
	}
}
