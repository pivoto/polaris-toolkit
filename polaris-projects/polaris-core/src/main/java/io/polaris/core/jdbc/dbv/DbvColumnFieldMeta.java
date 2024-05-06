package io.polaris.core.jdbc.dbv;

import java.lang.reflect.Field;

import lombok.Data;

/**
 * @author Qt
 * @since 1.8,  May 06, 2024
 */
@Data
public class DbvColumnFieldMeta {
	private Field field;
	private DbvColumnGetter getter;
	private String column;
}
