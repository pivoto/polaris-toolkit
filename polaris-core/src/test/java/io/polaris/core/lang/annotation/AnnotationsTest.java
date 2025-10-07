package io.polaris.core.lang.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.data.Anno1;
import io.polaris.core.lang.annotation.data.Anno1x1;
import io.polaris.core.lang.annotation.data.RepeatableAnno1;
import io.polaris.core.lang.annotation.data.RepeatableAnno1x1;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Oct 06, 2025
 */
public class AnnotationsTest {

	@Test
	void testNewInstance() {
		Map<String, Object> map = new HashMap<>();
		map.put("value", "test...");
		map.put("name", "test...");
		Anno1 anno1 = Annotations.newInstance(Anno1.class, map);
		Anno1 anno2 = Annotations.newInstance(Anno1.class, map);
		Object[] args4 = new Object[]{anno1.annotationType()};
		Consoles.println(args4);
		Consoles.println(anno1);
		Consoles.println(anno2);
		Consoles.println(anno1.equals(anno2));
		Object[] args3 = new Object[]{anno1.hashCode()};
		Consoles.println(args3);
		Object[] args2 = new Object[]{anno2.hashCode()};
		Consoles.println(args2);
		Object[] args1 = new Object[]{anno1.annotationType()};
		Consoles.println(args1);
		Object[] args = new Object[]{anno2.annotationType()};
		Consoles.println(args);
		Consoles.println(anno1.value());
		Consoles.println(anno2.value());
	}

	@Test
	void testMergedAnnotation() {
		Consoles.println("T1: " + Annotations.getMergedAnnotation(T1.class, Anno1.class));
		Consoles.println("T2: " + Annotations.getMergedAnnotation(T2.class, Anno1.class));
		Consoles.println("T3: " + Annotations.getMergedAnnotation(T3.class, Anno1.class));
		Consoles.println("T4: " + Annotations.getMergedAnnotation(T4.class, Anno1.class));
		Consoles.println("T5: " + Annotations.getMergedAnnotation(T5.class, Anno1.class));
		Consoles.println("T6: " + Annotations.getMergedAnnotation(T6.class, Anno1.class));
		Consoles.println("T7: " + Annotations.getMergedAnnotation(T7.class, Anno1.class));
		Consoles.println("T8: " + Annotations.getMergedAnnotation(T8.class, Anno1.class));
		Consoles.println("T9: " + Annotations.getMergedAnnotation(T9.class, Anno1.class));
	}

	@Test
	void testMergedRepeatableAnnotation() {
		Consoles.println("T1: " + (Annotations.getMergedRepeatableAnnotation(T1.class, RepeatableAnno1.class)));
		Consoles.println("T2: " + (Annotations.getMergedRepeatableAnnotation(T2.class, RepeatableAnno1.class)));
		Consoles.println("T3: " + (Annotations.getMergedRepeatableAnnotation(T3.class, RepeatableAnno1.class)));
		Consoles.println("T4: " + (Annotations.getMergedRepeatableAnnotation(T4.class, RepeatableAnno1.class)));
		Consoles.println("T5: " + (Annotations.getMergedRepeatableAnnotation(T5.class, RepeatableAnno1.class)));
		Consoles.println("T6: " + (Annotations.getMergedRepeatableAnnotation(T6.class, RepeatableAnno1.class)));
		Consoles.println("T7: " + (Annotations.getMergedRepeatableAnnotation(T7.class, RepeatableAnno1.class)));
		Consoles.println("T8: " + (Annotations.getMergedRepeatableAnnotation(T8.class, RepeatableAnno1.class)));
		Consoles.println("T9: " + (Annotations.getMergedRepeatableAnnotation(T9.class, RepeatableAnno1.class)));
	}

	@Test
	void testTopMergedRepeatableAnnotation() {
		Consoles.println("T1: " + (Annotations.getTopMergedRepeatableAnnotation(T1.class, RepeatableAnno1.class)));
		Consoles.println("T2: " + (Annotations.getTopMergedRepeatableAnnotation(T2.class, RepeatableAnno1.class)));
		Consoles.println("T3: " + (Annotations.getTopMergedRepeatableAnnotation(T3.class, RepeatableAnno1.class)));
		Consoles.println("T4: " + (Annotations.getTopMergedRepeatableAnnotation(T4.class, RepeatableAnno1.class)));
		Consoles.println("T5: " + (Annotations.getTopMergedRepeatableAnnotation(T5.class, RepeatableAnno1.class)));
		Consoles.println("T6: " + (Annotations.getTopMergedRepeatableAnnotation(T6.class, RepeatableAnno1.class)));
		Consoles.println("T7: " + (Annotations.getTopMergedRepeatableAnnotation(T7.class, RepeatableAnno1.class)));
		Consoles.println("T8: " + (Annotations.getTopMergedRepeatableAnnotation(T8.class, RepeatableAnno1.class)));
		Consoles.println("T9: " + (Annotations.getTopMergedRepeatableAnnotation(T9.class, RepeatableAnno1.class)));
	}

	@Test
	void testRawAnnotation() {
		//Consoles.println("T1: " + AnnotationAttributes.of(Annotations.getRawAnnotation(T1.class, Anno1.class)).asAnnotation());
		Consoles.println("T1: " + Annotations.getRawAnnotation(T1.class, Anno1.class));
		Consoles.println("T2: " + Annotations.getRawAnnotation(T2.class, Anno1.class));
		Consoles.println("T3: " + Annotations.getRawAnnotation(T3.class, Anno1.class));
		Consoles.println("T4: " + Annotations.getRawAnnotation(T4.class, Anno1.class));
		Consoles.println("T5: " + Annotations.getRawAnnotation(T5.class, Anno1.class));
		Consoles.println("T6: " + Annotations.getRawAnnotation(T6.class, Anno1.class));
		Consoles.println("T7: " + Annotations.getRawAnnotation(T7.class, Anno1.class));
		Consoles.println("T8: " + Annotations.getRawAnnotation(T8.class, Anno1.class));
		Consoles.println("T9: " + Annotations.getRawAnnotation(T9.class, Anno1.class));
	}

	@Test
	void testRawRepeatableAnnotation() {
		Consoles.println("T1: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T1.class, RepeatableAnno1.class)));
		Consoles.println("T2: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T2.class, RepeatableAnno1.class)));
		Consoles.println("T3: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T3.class, RepeatableAnno1.class)));
		Consoles.println("T4: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T4.class, RepeatableAnno1.class)));
		Consoles.println("T5: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T5.class, RepeatableAnno1.class)));
		Consoles.println("T6: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T6.class, RepeatableAnno1.class)));
		Consoles.println("T7: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T7.class, RepeatableAnno1.class)));
		Consoles.println("T8: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T8.class, RepeatableAnno1.class)));
		Consoles.println("T9: " + Arrays.toString(Annotations.getRawRepeatableAnnotation(T9.class, RepeatableAnno1.class)));
	}

	@Test
	void testAnnotationProxy() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
		Anno1 Anno1 = Annotations.getRawAnnotation(T1.class, Anno1.class);
		InvocationHandler handler = Proxy.getInvocationHandler(Anno1);
		Consoles.println(handler.getClass());
		Field field = handler.getClass().getDeclaredField("memberValues");
		field.setAccessible(true);
		Consoles.println(field.get(handler));

		{
			Method value = Anno1x1.class.getMethod("key");
			Consoles.println(value.getDefaultValue());
		}
	}

	@Anno1("I1")
	@RepeatableAnno1("I1.1")
	@RepeatableAnno1("I1.2")
	public static interface I1 {
	}

	@Anno1("I2")
	@RepeatableAnno1("I2.1")
	@RepeatableAnno1("I2.2")
	public static interface I2 {
	}

	@Anno1("T1")
	@RepeatableAnno1("T1.1")
	@RepeatableAnno1("T1.2")
	public static class T1 implements I1, I2 {
	}

	public static class T2 extends T1 implements I1, I2 {
	}

	@Anno1x1
	@RepeatableAnno1("T3.1")
	@RepeatableAnno1x1
	public static class T3 {
	}

	public static class T4 implements I1, I2 {
	}

	@Anno1x1
	@RepeatableAnno1x1
	public static class T5 implements I1, I2 {
	}

	public static class T6 extends T3 {
	}

	public static class T7 extends T4 {
	}

	public static class T8 extends T5 {
	}

	public static class T9 implements I2, I1 {
	}
}
