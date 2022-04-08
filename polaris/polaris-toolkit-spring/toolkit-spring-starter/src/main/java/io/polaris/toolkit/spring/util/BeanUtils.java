package io.polaris.toolkit.spring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author Qt
 * @version Jan 03, 2022
 * @since 1.8
 */
@Slf4j
public class BeanUtils extends org.springframework.beans.BeanUtils {

	public static void copyPropertiesQuietly(Object source, Object target,
			Predicate<String> propertyFilter, BiPredicate<String, Object> valueFilter) throws BeansException {
		copyPropertiesQuietly(source, target, null, propertyFilter, valueFilter);
	}

	public static void copyPropertiesQuietly(Object source, Object target, @Nullable Class<?> editable,
			Predicate<String> propertyFilter, BiPredicate<String, Object> valueFilter) throws BeansException {

		Assert.notNull(source, "Source must not be null");
		Assert.notNull(target, "Target must not be null");

		Class<?> actualEditable = target.getClass();
		if (editable != null) {
			if (!editable.isInstance(target)) {
				throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
						"] not assignable to Editable class [" + editable.getName() + "]");
			}
			actualEditable = editable;
		}
		PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
		for (PropertyDescriptor targetPd : targetPds) {
			if (propertyFilter != null && !propertyFilter.test(targetPd.getName())) {
				continue;
			}
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod == null) {
				continue;
			}

			PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(), targetPd.getName());
			if (sourcePd != null) {
				Method readMethod = sourcePd.getReadMethod();
				if (readMethod != null &&
						ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
					try {
						if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
							readMethod.setAccessible(true);
						}
						Object value = readMethod.invoke(source);
						if (valueFilter != null && !valueFilter.test(targetPd.getName(), value)) {
							continue;
						}
						if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
							writeMethod.setAccessible(true);
						}
						writeMethod.invoke(target, value);
					} catch (Throwable ex) {
						log.warn("", ex);
					}
				}
			}
		}
	}
}
