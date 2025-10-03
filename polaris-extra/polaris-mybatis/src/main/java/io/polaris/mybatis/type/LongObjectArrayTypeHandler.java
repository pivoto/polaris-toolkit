package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(Long[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class LongObjectArrayTypeHandler extends StringTokenizerTypeHandler<Long> {
	public LongObjectArrayTypeHandler() {
		super(Long.class);
	}

	@Override
	Long parseString(String value) {
		return Long.valueOf(value);
	}
}
