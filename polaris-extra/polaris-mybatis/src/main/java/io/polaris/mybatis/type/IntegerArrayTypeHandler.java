package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(int[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class IntegerArrayTypeHandler extends StringTokenizerTypeHandler<Integer> {
	public IntegerArrayTypeHandler() {
		super(int.class);
	}

	@Override
	Integer parseString(String value) {
		return Integer.valueOf(value);
	}
}
