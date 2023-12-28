package io.polaris.core.lang.bean;

import io.polaris.core.lang.bean.impl.ListBeanPropertyBuilder;
import io.polaris.core.lang.bean.impl.StdBeanPropertyBuilder;

import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public class BeanPropertyBuilders {

	public static <T> BeanPropertyBuilder<T> of(T dest) {
		return new StdBeanPropertyBuilder<>(dest);
	}

	public static <T> BeanPropertyBuilder<T> of(Class<T> destType) {
		return new StdBeanPropertyBuilder<>(destType);
	}

	public static <T> BeanPropertyBuilder<List<T>> of(List<T> list, Class<T> type) {
		return new ListBeanPropertyBuilder<T>(list, type);
	}

	public static <T> BeanPropertyBuilder<List<T>> of(List<T> list, Class<T> type, int size) {
		return new ListBeanPropertyBuilder<T>(list, type, size);
	}


}
