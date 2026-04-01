package io.polaris.mybatis.consts;

import java.util.Collections;
import java.util.Set;
import java.util.function.Predicate;

import io.polaris.core.collection.Sets;

/**
 * @author Qt
 * @since Oct 10, 2025
 */
public interface MappingPropKeys {

	Set<String> PARAMETER_MAPPING_KEYS = Collections.unmodifiableSet(Sets.asSet("mode", "javaType", "jdbcType", "numericScale", "typeHandler"));
	Set<String> RESULT_MAPPING_KEYS = Collections.unmodifiableSet(Sets.asSet("jdbcType", "typeHandler"));

	Predicate<String> PARAMETER_MAPPING_KEYS_FILTER = MappingPropKeys.PARAMETER_MAPPING_KEYS::contains;
	Predicate<String> RESULT_MAPPING_KEYS_FILTER = MappingPropKeys.RESULT_MAPPING_KEYS::contains;

}
