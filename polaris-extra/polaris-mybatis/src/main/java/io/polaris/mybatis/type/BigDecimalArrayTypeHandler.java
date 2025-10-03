package io.polaris.mybatis.type;

import java.math.BigDecimal;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@MappedTypes(BigDecimal[].class)
@MappedJdbcTypes(value = {JdbcType.VARCHAR, JdbcType.CHAR}, includeNullJdbcType = true)
public class BigDecimalArrayTypeHandler extends StringTokenizerTypeHandler<BigDecimal> {
	public BigDecimalArrayTypeHandler() {
		super(BigDecimal.class);
	}

	@Override
	BigDecimal parseString(String value) {
		return new BigDecimal(value);
	}
}
