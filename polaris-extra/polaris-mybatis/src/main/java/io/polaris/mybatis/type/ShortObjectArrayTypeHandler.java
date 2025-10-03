package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(Short[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class ShortObjectArrayTypeHandler extends StringTokenizerTypeHandler<Short> {
	public ShortObjectArrayTypeHandler() {
		super(Short.class);
	}

	@Override
	Short parseString(String value) {
		return Short.valueOf(value);
	}
}
