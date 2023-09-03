package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@MappedTypes(Long[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class LongArrayTypeHandler extends StringTokenizerTypeHandler<Long> {
	public LongArrayTypeHandler() {
		super(Long.class);
	}

	@Override
	Long parseString(String value) {
		return Long.valueOf(value);
	}
}
