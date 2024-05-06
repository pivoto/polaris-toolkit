package io.polaris.core.jdbc.dbv;

import java.util.Set;

import lombok.Data;

/**
 * @author Qt
 * @since 1.8,  May 06, 2024
 */
@Data
public class DbvColumnFieldsMeta {
	private Set<String> columns;
	private Set<DbvColumnFieldMeta> fields;
}
