package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(double[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class DoubleArrayTypeHandler extends StringTokenizerTypeHandler<Double> {
	public DoubleArrayTypeHandler() {
		super(double.class);
	}

	@Override
	Double parseString(String value) {
		return Double.valueOf(value);
	}
}
