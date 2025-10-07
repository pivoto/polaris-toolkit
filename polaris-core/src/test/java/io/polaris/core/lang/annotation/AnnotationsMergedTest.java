package io.polaris.core.lang.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.data.Anno1;
import io.polaris.core.lang.annotation.data.RepeatableAnno1;
import io.polaris.core.reflect.Reflects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Oct 06, 2025
 */
public class AnnotationsMergedTest {

	@Test
	@DisplayName("类注解测试")
	void testClassAlias01() {
		Anno1 anno_C1 = MergedAnnotations.of(C1.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1);
		Anno1 anno_C1_1 = MergedAnnotations.of(C1_1.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_1);
		Anno1 anno_C1_1x = MergedAnnotations.of(C1_1x.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_1x);
		Anno1 anno_C1_2 = MergedAnnotations.of(C1_2.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2);
		Anno1 anno_C1_2x = MergedAnnotations.of(C1_2x.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2x);
		Anno1 anno_C1_2_1 = MergedAnnotations.of(C1_2_1.class).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2_1);

		Assertions.assertNotNull(anno_C1);
		Assertions.assertEquals("C1", anno_C1.name());
		Assertions.assertEquals(anno_C1.value(), anno_C1.name());
		Assertions.assertNotNull(anno_C1_1);
		Assertions.assertEquals("C1_1", anno_C1_1.name());
		Assertions.assertEquals(anno_C1.value(), anno_C1.name());
		Assertions.assertNotNull(anno_C1_1x);
		Assertions.assertEquals("C1_1", anno_C1_1x.name());
		Assertions.assertEquals(anno_C1_1x.value(), anno_C1_1x.name());
		Assertions.assertNotNull(anno_C1_2);
		Assertions.assertEquals("C1_2", anno_C1_2.name());
		Assertions.assertEquals(anno_C1_2.value(), anno_C1_2.name());
		Assertions.assertNotNull(anno_C1_2x);
		Assertions.assertEquals("C1_2", anno_C1_2x.name());
		Assertions.assertEquals(anno_C1_2x.value(), anno_C1_2x.name());
		Assertions.assertNotNull(anno_C1_2_1);
		Assertions.assertEquals("C1_2_1", anno_C1_2_1.name());
		Assertions.assertEquals(anno_C1_2_1.value(), anno_C1_2_1.name());
	}

	@Test
	@DisplayName("方法注解测试")
	void testClassAlias02() {
		Anno1 anno_C1 = MergedAnnotations.of(Reflects.getMethod(C1.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1);
		Anno1 anno_C1_1 = MergedAnnotations.of(Reflects.getMethod(C1_1.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_1);
		Anno1 anno_C1_1x = MergedAnnotations.of(Reflects.getMethod(C1_1x.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_1x);
		Anno1 anno_C1_2 = MergedAnnotations.of(Reflects.getMethod(C1_2.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2);
		Anno1 anno_C1_2x = MergedAnnotations.of(Reflects.getMethod(C1_2x.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2x);
		Anno1 anno_C1_2_1 = MergedAnnotations.of(Reflects.getMethod(C1_2_1.class, "m1")).getMergedAnnotation(Anno1.class);
		Consoles.println(anno_C1_2_1);


		Assertions.assertNotNull(anno_C1);
		Assertions.assertEquals("C1.m1", anno_C1.name());
		Assertions.assertEquals(anno_C1.value(), anno_C1.name());
		Assertions.assertNotNull(anno_C1_1);
		Assertions.assertEquals("C1_1.m1", anno_C1_1.name());
		Assertions.assertEquals(anno_C1.value(), anno_C1.name());
		Assertions.assertNotNull(anno_C1_1x);
		Assertions.assertEquals("C1_1.m1", anno_C1_1x.name());
		Assertions.assertEquals(anno_C1_1x.value(), anno_C1_1x.name());
		Assertions.assertNotNull(anno_C1_2);
		Assertions.assertEquals("C1_2.m1", anno_C1_2.name());
		Assertions.assertEquals(anno_C1_2.value(), anno_C1_2.name());
		Assertions.assertNotNull(anno_C1_2x);
		Assertions.assertEquals("C1_2.m1", anno_C1_2x.name());
		Assertions.assertEquals(anno_C1_2x.value(), anno_C1_2x.name());
		Assertions.assertNotNull(anno_C1_2_1);
		Assertions.assertEquals("C1_2_1.m1", anno_C1_2_1.name());
		Assertions.assertEquals(anno_C1_2_1.value(), anno_C1_2_1.name());
	}

	@Test
	@DisplayName("可重复类注解测试")
	void testClassAliasRepeatable01() {
		Set<RepeatableAnno1> annoSet_C1 = MergedAnnotations.of(C1.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1);
		Set<RepeatableAnno1> annoSet_C1_1 = MergedAnnotations.of(C1_1.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_1);
		Set<RepeatableAnno1> annoSet_C1_1x = MergedAnnotations.of(C1_1x.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_1x);
		Set<RepeatableAnno1> annoSet_C1_2 = MergedAnnotations.of(C1_2.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2);
		Set<RepeatableAnno1> annoSet_C1_2x = MergedAnnotations.of(C1_2x.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2x);
		Set<RepeatableAnno1> annoSet_C1_2_1 = MergedAnnotations.of(C1_2_1.class).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2_1);
	}

	@Test
	@DisplayName("可重复方法注解测试")
	void testClassAliasRepeatable02() {
		Set<RepeatableAnno1> annoSet_C1 = MergedAnnotations.of(Reflects.getMethod(C1.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1);
		Set<RepeatableAnno1> annoSet_C1_1 = MergedAnnotations.of(Reflects.getMethod(C1_1.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_1);
		Set<RepeatableAnno1> annoSet_C1_1x = MergedAnnotations.of(Reflects.getMethod(C1_1x.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_1x);
		Set<RepeatableAnno1> annoSet_C1_2 = MergedAnnotations.of(Reflects.getMethod(C1_2.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2);
		Set<RepeatableAnno1> annoSet_C1_2x = MergedAnnotations.of(Reflects.getMethod(C1_2x.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2x);
		Set<RepeatableAnno1> annoSet_C1_2_1 = MergedAnnotations.of(Reflects.getMethod(C1_2_1.class, "m1")).getMergedRepeatableAnnotation(RepeatableAnno1.class);
		Consoles.println(annoSet_C1_2_1);
	}

	@Anno1(name = "C1")
	@RepeatableAnno1(name = "C1.0")
	@RepeatableAnno1(name = "C1.1")
	static class C1 {
		@Anno1(name = "C1.m1")
		@RepeatableAnno1(name = "C1.m1.0")
		@RepeatableAnno1(name = "C1.m1.1")
		void m1() {
		}
	}

	@Anno1(name = "C1_1")
	@RepeatableAnno1(name = "C1_1.0")
	@RepeatableAnno1(name = "C1_1.1")
	static class C1_1 extends C1 {
		@Anno1(name = "C1_1.m1")
		@RepeatableAnno1(name = "C1_1.m1.0")
		@RepeatableAnno1(name = "C1_1.m1.1")
		void m1() {
		}
	}

	static class C1_1x extends C1_1 {
		void m1() {
		}
	}

	@Anno1(name = "C1_2")
	@RepeatableAnno1(name = "C1_2.0")
	@RepeatableAnno1(name = "C1_2.1")
	static class C1_2 extends C1 {
		@Anno1(name = "C1_2.m1")
		@RepeatableAnno1(name = "C1_2.m1.0")
		@RepeatableAnno1(name = "C1_2.m1.1")
		void m1() {
		}
	}

	static class C1_2x extends C1_2 {
		void m1() {
		}
	}

	@Anno1(name = "C1_2_1")
	@RepeatableAnno1(name = "C1_2_1.0")
	@RepeatableAnno1(name = "C1_2_1.1")
	static class C1_2_1 extends C1_2 {
		@Anno1(name = "C1_2_1.m1")
		@RepeatableAnno1(name = "C1_2_1.m1.0")
		@RepeatableAnno1(name = "C1_2_1.m1.1")
		void m1() {
		}
	}
}
