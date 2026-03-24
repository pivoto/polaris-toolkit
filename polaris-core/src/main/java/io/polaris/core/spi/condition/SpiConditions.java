package io.polaris.core.spi.condition;

import java.lang.annotation.Annotation;
import java.util.Map;

import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.lang.annotation.Annotations;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since Mar 19, 2026
 */
public class SpiConditions {

	public static SpiConditionResult matches(Class<?> target) {
		return matches(target, true);
	}

	public static SpiConditionResult matches(Class<?> target, boolean failfast) {
		Map<Class<? extends Annotation>, AnnotationAttributes> annotationAttributesMap = Annotations.findMergedAnnotationAttributes(target, a -> a.isAnnotationPresent(SpiConditional.class));

		if (annotationAttributesMap.isEmpty()) {
			return SpiConditionResult.match("无需任何条件");
		}
		if (failfast) {
			for (Map.Entry<Class<? extends Annotation>, AnnotationAttributes> entry : annotationAttributesMap.entrySet()) {
				Class<? extends Annotation> annotationType = entry.getKey();
				AnnotationAttributes annotationAttributes = entry.getValue();
				SpiConditional spiConditional = annotationType.getAnnotation(SpiConditional.class);
				if (spiConditional != null) {
					Class<? extends SpiCondition>[] spiConditions = spiConditional.value();
					for (Class<? extends SpiCondition> spiCondition : spiConditions) {
						SpiCondition instance = null;
						try {
							instance = Reflects.newInstance(spiCondition);
						} catch (ReflectiveOperationException e) {
							return SpiConditionResult.noMatch(annotationType.getName() + " 所需条件类无法实例化");
						}
						if (instance != null) {
							boolean matches = instance.matches(annotationType, annotationAttributes, target, target.getClassLoader());
							if (!matches) {
								return SpiConditionResult.noMatch(annotationType.getName() + " 条件不满足");
							}
						}
					}
				}
			}
			return SpiConditionResult.match("满足所有条件");
		} else {
			boolean noMatch = false;
			StringBuilder msg = new StringBuilder();
			for (Map.Entry<Class<? extends Annotation>, AnnotationAttributes> entry : annotationAttributesMap.entrySet()) {
				Class<? extends Annotation> annotationType = entry.getKey();
				AnnotationAttributes annotationAttributes = entry.getValue();
				SpiConditional spiConditional = annotationType.getAnnotation(SpiConditional.class);
				if (spiConditional != null) {
					Class<? extends SpiCondition>[] spiConditions = spiConditional.value();
					for (Class<? extends SpiCondition> spiCondition : spiConditions) {
						SpiCondition instance = null;
						try {
							instance = Reflects.newInstance(spiCondition);
						} catch (ReflectiveOperationException e) {
							noMatch = true;
							msg.append(annotationType.getName()).append(" 所需条件类无法实例化！");
						}
						if (instance != null) {
							boolean matches = instance.matches(annotationType, annotationAttributes, target, target.getClassLoader());
							if (!matches) {
								noMatch = true;
								msg.append(annotationType.getName()).append(" 条件不满足！");
							} else {
								msg.append(annotationType.getName()).append(" 条件满足！");
							}
						}
					}
				}
			}
			if (noMatch) {
				return SpiConditionResult.noMatch(msg.toString());
			}
			StringBuilder matchMsg = new StringBuilder().append("满足所有条件");
			boolean first = true;
			for (Map.Entry<Class<? extends Annotation>, AnnotationAttributes> entry : annotationAttributesMap.entrySet()) {
				Class<? extends Annotation> annotationType = entry.getKey();
				if (first) {
					first = false;
					matchMsg.append("：[");
				} else {
					matchMsg.append(", ");
				}
				matchMsg.append(annotationType.getName());
			}
			if (!first) {
				matchMsg.append("]");
			}
			return SpiConditionResult.match(matchMsg.toString());
		}
	}
}
