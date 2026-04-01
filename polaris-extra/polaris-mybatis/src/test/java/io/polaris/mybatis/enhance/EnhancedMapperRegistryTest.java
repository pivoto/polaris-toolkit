package io.polaris.mybatis.enhance;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnhancedMapperRegistryTest {

	@Test
	void test01() {
		EnhancedMapperRegistry enhancedMapperRegistry = new EnhancedMapperRegistry(new EnhancedConfiguration());
		Consoles.println(enhancedMapperRegistry.getKnownMappers());
		EnhancedMapperRegistry	enhancedMapperRegistry2 = new EnhancedMapperRegistry(enhancedMapperRegistry);
		Consoles.println(enhancedMapperRegistry2.getKnownMappers());
	}
}
