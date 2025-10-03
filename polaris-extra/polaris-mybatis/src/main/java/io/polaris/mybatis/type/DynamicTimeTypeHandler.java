package io.polaris.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Date;

import io.polaris.core.converter.Converters;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.Alias;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * @author Qt
 * @see DateTypeHandler
 * @see LocalDateTypeHandler
 * @since Aug 28, 2023
 */
@Slf4j
@MappedTypes(Time.class)
@MappedJdbcTypes(value = {JdbcType.DATE, JdbcType.TIMESTAMP, JdbcType.TIME}, includeNullJdbcType = true)
@Alias("dynamicTimeTypeHandler")
public class DynamicTimeTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.TIMESTAMP);
			return;
		}
		if (parameter instanceof Time) {
			ps.setTime(i, (Time) parameter);
		} else if (parameter instanceof Date) {
			ps.setTime(i, new Time(((Date) parameter).getTime()));
		} else if (parameter instanceof Number) {
			ps.setTime(i, new Time(((Number) parameter).longValue()));
		} else {
			Date date = Converters.convert(Date.class, parameter);
			ps.setTime(i, new Time(date.getTime()));
		}
	}

	@Override
	public Time getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		Time sqlTime = rs.getTime(columnName);
		return sqlTime;
	}

	@Override
	public Time getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		Time sqlTime = rs.getTime(columnIndex);
		return sqlTime;
	}

	@Override
	public Time getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		Time sqlTime = cs.getTime(columnIndex);
		return sqlTime;
	}
}
