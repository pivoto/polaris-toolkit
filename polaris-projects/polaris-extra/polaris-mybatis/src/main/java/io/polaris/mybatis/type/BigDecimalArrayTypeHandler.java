package io.polaris.mybatis.type;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@MappedTypes(BigDecimal[].class)
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.CHAR})
public class BigDecimalArrayTypeHandler extends StringTokenizerTypeHandler<BigDecimal> {
	public BigDecimalArrayTypeHandler() {
		super(BigDecimal.class);
	}

	@Override
	BigDecimal parseString(String value) {
		return new BigDecimal(value);
	}
}
