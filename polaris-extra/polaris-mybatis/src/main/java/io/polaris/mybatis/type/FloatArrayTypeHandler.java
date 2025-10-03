package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(float[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class FloatArrayTypeHandler extends StringTokenizerTypeHandler<Float> {
	public FloatArrayTypeHandler() {
		super(float.class);
	}

	@Override
	Float parseString(String value) {
		return Float.valueOf(value);
	}
}
