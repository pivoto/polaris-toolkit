package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
@MappedTypes(Short[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class ShortArrayTypeHandler extends StringTokenizerTypeHandler<Short> {
	public ShortArrayTypeHandler() {
		super(Short.class);
	}

	@Override
	Short parseString(String value) {
		return Short.valueOf(value);
	}
}
