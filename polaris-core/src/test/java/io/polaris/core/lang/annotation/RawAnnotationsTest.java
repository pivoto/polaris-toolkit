package io.polaris.core.lang.annotation;

import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.case02.BaseAnno;
import io.polaris.core.lang.annotation.case02.BaseClass;
import io.polaris.core.lang.annotation.case02.RepeatableClass;
import io.polaris.core.lang.annotation.case02.RepeatedAnno;
import io.polaris.core.lang.annotation.case02.RepeatableAnno;
import org.junit.jupiter.api.Test;

class RawAnnotationsTest {

	@Test
	void test() {
		Consoles.println("", RawAnnotations.findAnnotation(BaseClass.class, BaseAnno.class::isAssignableFrom));
		Consoles.println("", RawAnnotations.findAnnotation(RepeatableClass.class, RepeatableAnno.class::isAssignableFrom));
		Consoles.println("", RawAnnotations.findAnnotation(RepeatableClass.class, RepeatedAnno.class::isAssignableFrom));
	}

}
