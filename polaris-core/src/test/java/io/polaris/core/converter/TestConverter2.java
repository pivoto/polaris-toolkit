package io.polaris.core.converter;

import java.util.List;
import java.util.Set;

import io.polaris.core.collection.Lists;
import io.polaris.core.io.Consoles;
import io.polaris.core.lang.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Qt
 * @since Dec 09, 2024
 */
public class TestConverter2 {

	@Test
	void test01() {
		List<String> list = Lists.asList("1", "2", "3");

		Set<Integer> set = Converters.convert(new TypeRef<Set<Integer>>() {}.getType(), list);
		Consoles.log(set);
		Assertions.assertEquals(3, set.size());

	}
}
