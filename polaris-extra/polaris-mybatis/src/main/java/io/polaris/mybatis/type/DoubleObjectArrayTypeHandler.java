package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(Double[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class DoubleObjectArrayTypeHandler extends StringTokenizerTypeHandler<Double> {
	public DoubleObjectArrayTypeHandler() {
		super(Double.class);
	}

	@Override
	Double parseString(String value) {
		return Double.valueOf(value);
	}
}
