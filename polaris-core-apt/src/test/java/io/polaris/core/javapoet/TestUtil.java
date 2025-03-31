package io.polaris.core.javapoet;

import java.util.Collection;

import javax.lang.model.element.Element;

final class TestUtil {
	static <E extends Element> E findFirst(Collection<E> elements, String name) {
		for (E element : elements) {
			if (element.getSimpleName().toString().equals(name)) {
				return element;
			}
		}
		throw new IllegalArgumentException(name + " not found in " + elements);
	}
}
