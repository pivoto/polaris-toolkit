package io.polaris.mybatis.enhance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnhancedMapperAnnotationBuilderTest {


	@Test
	void test() {
		EnhancedMapperAnnotationBuilder builder = new EnhancedMapperAnnotationBuilder(new EnhancedConfiguration(), TestMapper.class);
	}
}
