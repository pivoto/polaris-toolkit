package io.polaris.mybatis.type;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(boolean[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class BooleanArrayTypeHandler extends StringTokenizerTypeHandler<Boolean> {
	public BooleanArrayTypeHandler() {
		super(boolean.class);
	}

	@Override
	Boolean parseString(String value) {
		return Boolean.valueOf(value);
	}
}
