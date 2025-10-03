package io.polaris.mybatis.type;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(Boolean[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class BooleanObjectArrayTypeHandler extends StringTokenizerTypeHandler<Boolean> {
	public BooleanObjectArrayTypeHandler() {
		super(Boolean.class);
	}

	@Override
	Boolean parseString(String value) {
		return Boolean.valueOf(value);
	}
}
