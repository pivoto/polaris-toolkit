package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.data.Anno;
import io.polaris.core.lang.annotation.data.AnnoSub;
import io.polaris.core.lang.annotation.data.CaseClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"SerializableNonStaticInnerClassWithoutSerialVersionUID", "serial"})
class AnnotationAttributesTest {

	@Test
	void testOf() {
		Class<CaseClass> caseClass = CaseClass.class;
		Anno annotation = caseClass.getAnnotation(Anno.class);
		AnnotationAttributes result = AnnotationAttributes.of(annotation);
		Consoles.log(annotation);
		Consoles.log(result.asMap());
		Assertions.assertEquals(annotation, result.asAnnotation());
	}

	@Test
	void testGetAnnotationMembers() {
		Method[] result = AnnotationAttributes.getAnnotationMembers(Anno.class);
		Consoles.log(result);
	}

	@Test
	void testAsAnnotation() {
		AnnotationAttributes result = AnnotationAttributes.of(Anno.class);
		Consoles.log(result.asMap());
		Assertions.assertNotNull(result.asAnnotation());
	}

	@Test
	void testAsMap() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Map<String, Object> result = annotationAttributes.asMap();
		Consoles.log(result);
		Assertions.assertTrue(result.size() > 1);
	}

	@Test
	void testGetMember() throws NoSuchMethodException {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		AnnotationAttributes.Member result = annotationAttributes.getMember("value");
		Consoles.log(result);
		Method method = Anno.class.getDeclaredMethod("value");
		Assertions.assertEquals(new AnnotationAttributes.Member(method, "", ""), result);
	}

	@Test
	void testSet() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		annotationAttributes.set(new HashMap<String, Object>() {{put("value", "test value");}});
		Consoles.log(annotationAttributes.asMap());
		Consoles.log(annotationAttributes.get("value"));
		Assertions.assertEquals("test value", annotationAttributes.get("value"));
	}

	@Test
	void testSet2() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		annotationAttributes.set("value", "test value");
		Consoles.log(annotationAttributes.asMap());
		Consoles.log(annotationAttributes.get("value"));
		Assertions.assertEquals("test value", annotationAttributes.get("value"));
	}

	@Test
	void testGet() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Object result = annotationAttributes.get("value");
		Consoles.log(result);
		Assertions.assertEquals("", result);
	}


	@Test
	void testGetString() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		String result = annotationAttributes.getString("value");
		Consoles.log(result);
		Assertions.assertEquals("", result);
	}

	@Test
	void testGetStringArray() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		String[] result = annotationAttributes.getStringArray("stringArrayValue");
		Assertions.assertArrayEquals(new String[]{}, result);
	}

	@Test
	void testGetBoolean() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Boolean result = annotationAttributes.getBoolean("booleanValue");
		Assertions.assertEquals(Boolean.FALSE, result);
	}

	@Test
	void testGetNumber() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Number result = annotationAttributes.getNumber("longValue");
		Assertions.assertEquals(0L, result);
	}

	@Test
	void testGetClass() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Class<?> result = annotationAttributes.getClass("classValue");
		Assertions.assertEquals(Object.class, result);
	}

	@Test
	void testGetClassArray() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Class[] result = annotationAttributes.getClassArray("classArrayValue");
		Assertions.assertArrayEquals(new Class[]{}, result);
	}

	@Test
	void testGetAnnotation() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Annotation result = annotationAttributes.getAnnotation("annoValue");
		Assertions.assertEquals(AnnotationAttributes.of(AnnoSub.class).asAnnotation(), result);
	}

	@Test
	void testGetAnnotationArray() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		Annotation[] result = annotationAttributes.getAnnotationArray("annoArrayValue");
		Assertions.assertArrayEquals(new Annotation[]{}, result);
	}

	@Test
	void testGetAnnotationAttributes() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		AnnotationAttributes result = annotationAttributes.getAnnotationAttributes("annoValue");
		Annotation annotation = AnnotationAttributes.of(AnnoSub.class).asAnnotation();
		Assertions.assertEquals(annotation, result.asAnnotation());
	}

	@Test
	void testGetAnnotationAttributesArray() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(CaseClass.class.getAnnotation(Anno.class));
		AnnotationAttributes[] result = annotationAttributes.getAnnotationAttributesArray("name");
		Assertions.assertArrayEquals(new AnnotationAttributes[]{null}, result);
	}

}

