package io.polaris.core.converter.support;

import io.polaris.core.converter.AbstractSimpleConverter;
import io.polaris.core.converter.Converters;
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

			Object value4 = map.get("className");
			final String declaringClass = Converters.convertQuietly(String.class, value4);
			Object value3 = map.get("methodName");
			final String methodName = Converters.convertQuietly(String.class, value3);
			Object value2 = map.get("fileName");
			final String fileName = Converters.convertQuietly(String.class, value2);
			Object value1 = map.get("lineNumber");
			final Integer lineNumber = Converters.convertQuietly(Integer.class, value1);

			return new StackTraceElement(declaringClass, methodName, fileName, lineNumber == null ? 0 : lineNumber);
		}
		return null;
	}
}
