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
		Consoles.println("", RawAnnotations.findAnnotation(BaseClass.class, a -> {
			return a instanceof BaseAnno;
		}));
		Consoles.println("", RawAnnotations.findAnnotation(RepeatableClass.class, a -> {
			return a instanceof RepeatableAnno;
		}));
		Consoles.println("", RawAnnotations.findAnnotation(RepeatableClass.class, a -> {
			return a instanceof RepeatedAnno;
		}));
	}
}
