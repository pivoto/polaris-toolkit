package io.polaris.core.converter;

import io.polaris.core.lang.TypeRefs;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

import java.util.Map;

class ConverterRegistryTest {
	@Test
	void testConvert() throws ClassNotFoundException {
		System.out.printf("convert: %s%n", ConverterRegistry.INSTANCE.<Object>convertQuietly(String.class, null));
		System.out.printf("convert: %s%n", ConverterRegistry.INSTANCE.<Object>convertQuietly(String.class, Strings.asMap("k1", "v1", "k2", "v2")));
		System.out.printf("convert: %s%n", ConverterRegistry.INSTANCE.<Object>convertQuietly(ConverterRegistryTest.class, Strings.asMap("k1", "v1", "k2", "v2")));


		System.out.printf("convert: %s%n", ConverterRegistry.INSTANCE.<Object>convertQuietly(Map.class, ConverterRegistry.INSTANCE.<Object>convertQuietly(String.class, Strings.asMap("k1", "v1", "k2", "v2"))));


		System.out.printf("convert: %s%n", ConverterRegistry.INSTANCE.<Object>convertQuietly(TypeRefs.getType("java.util.List<String>"), "[1,2,3, 12]"));

	}
}

