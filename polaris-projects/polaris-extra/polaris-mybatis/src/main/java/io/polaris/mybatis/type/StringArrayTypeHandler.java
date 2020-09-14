package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@MappedTypes(String[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class StringArrayTypeHandler extends StringTokenizerTypeHandler<String> {
	public StringArrayTypeHandler() {
		super(String.class);
	}

	@Override
	String parseString(String value) {
		return String.valueOf(value);
	}
}
