package io.polaris.mybatis.consts;

import java.util.Collections;
import java.util.Set;

import io.polaris.core.collection.Sets;

/**
 * @author Qt
 * @since Oct 10, 2025
 */
public interface MappingKeys {


	Set<String> PARAMETER_MAPPING_KEYS = Collections.unmodifiableSet(Sets.asSet(
		"mode"
		, "javaType"
		, "jdbcType"
		, "numericScale"
		, "typeHandler"
	));
	// RESULT_MAPPING_KEYS
}
