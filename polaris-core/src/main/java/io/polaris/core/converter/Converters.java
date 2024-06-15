package io.polaris.core.converter;

import io.polaris.core.annotation.AnnotationProcessing;

import javax.annotation.Nullable;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Type;

/**
 * @author Qt
 * @since  Dec 27, 2023
 */
public class Converters {

	private static volatile ConverterRegistry defaultRegistry;

	public static ConverterRegistry getDefaultRegistry() {
		if (defaultRegistry != null) {
			return defaultRegistry;
		}
		synchronized (Converters.class) {
			if (defaultRegistry == null) {
				defaultRegistry = new ConverterRegistry();
			}
		}
		return defaultRegistry;
	}

	@Nullable
	public static <T> Converter<T> getConverter(Type type) {
		return getDefaultRegistry().getConverter(type);
	}

	public static <T> Converter<T> getConverterOrDefault(Type type, Converter<T> defaults) {
		return getDefaultRegistry().getConverterOrDefault(type, defaults);
	}

	public static <T> T convert(Type type, Type valueType, Object value, T defaultValue) {
		return getDefaultRegistry().convert(type, valueType, value, defaultValue);
	}

	public static <T> T convert(Type type, Type valueType, Object value) {
		return getDefaultRegistry().convert(type, valueType, value);
	}

	public static <T> T convert(Type type, Object value, T defaultValue) {
		return getDefaultRegistry().convert(type, value, defaultValue);
	}

	@AnnotationProcessing
	public static <T> T convert(Type type, Object value) {
		return getDefaultRegistry().convert(type, value);
	}

	public static <T> T convertQuietly(Type type, Type valueType, Object value, T defaultValue) {
		return getDefaultRegistry().convertQuietly(type, valueType, value, defaultValue);
	}

	public static <T> T convertQuietly(Type type, Type valueType, Object value) {
		return getDefaultRegistry().convertQuietly(type, valueType, value);
	}

	public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
		return getDefaultRegistry().convertQuietly(type, value, defaultValue);
	}

	public static <T> T convertQuietly(Type type, Object value) {
		return getDefaultRegistry().convertQuietly(type, value);
	}

	public static <T> T convertByPropertyEditor(Class type, Object value, T defaultValue) {
		PropertyEditor sourceEditor = PropertyEditorManager.findEditor(value.getClass());
		PropertyEditor targetEditor = PropertyEditorManager.findEditor(type);
		if (sourceEditor != null && targetEditor != null) {
			sourceEditor.setValue(value);
			targetEditor.setAsText(sourceEditor.getAsText());
			return (T) targetEditor.getValue();
		}
		return defaultValue;
	}


}
