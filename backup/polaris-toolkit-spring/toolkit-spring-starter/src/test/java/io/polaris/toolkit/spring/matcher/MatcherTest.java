package io.polaris.toolkit.spring.matcher;

import io.polaris.toolkit.spring.transaction.DynamicTransactionProperties;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.TypePatternClassFilter;

/**
 * @author Qt
 * @version Dec 31, 2021
 * @since 1.8
 */
public class MatcherTest {


	@Test
	void test01() {
		TypePatternClassFilter filter = new TypePatternClassFilter(
				"io.polaris.toolkit.spring..* and io.polaris.toolkit.spring.matcher.*");
		System.out.println(filter.matches(MatcherTest.class));
	}

	@Test
	void test02() {
		TypePatternClassFilter filter = new TypePatternClassFilter(
				"@within(org.springframework.boot.context.properties.ConfigurationProperties)");
		System.out.println(filter.matches(DynamicTransactionProperties.class));
	}
}
