package io.polaris.core.lang.annotation;

import java.util.Arrays;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.case02.BaseAnno;
import io.polaris.core.lang.annotation.case02.BaseClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Mar 18, 2026
 */
public class Case02AnnotationsTest {


	@Test
	@DisplayName("基础测试")
	void testBase() {

		Class<BaseClass> ele = BaseClass.class;
		Consoles.println("getRawAnnotation", Annotations.getRawAnnotation(ele, BaseAnno.class));
		Consoles.println("getMergedAnnotation", Annotations.getMergedAnnotation(ele, BaseAnno.class));
		Consoles.println("getRawRepeatableAnnotation", Arrays.toString(Annotations.getRawRepeatableAnnotation(ele, BaseAnno.class)));
		Consoles.println("getMergedRepeatableAnnotation", Annotations.getMergedRepeatableAnnotation(ele, BaseAnno.class));
		Consoles.println("getTopMergedRepeatableAnnotation", Annotations.getTopMergedRepeatableAnnotation(ele, BaseAnno.class));

	}
}
