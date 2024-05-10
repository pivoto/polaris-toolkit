package io.polaris.mybatis.type;


import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since  Aug 28, 2023
 */
@MappedTypes(Boolean[].class)
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.CHAR})
public class BooleanArrayTypeHandler extends StringTokenizerTypeHandler<Boolean> {
	public BooleanArrayTypeHandler() {
		super(Boolean.class);
	}

	@Override
	Boolean parseString(String value) {
		return Boolean.valueOf(value);
	}
}
