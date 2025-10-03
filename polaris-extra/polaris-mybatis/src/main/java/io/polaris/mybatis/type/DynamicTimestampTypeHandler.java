package io.polaris.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
@MappedTypes(Timestamp.class)
@MappedJdbcTypes(value = {JdbcType.DATE, JdbcType.TIMESTAMP, JdbcType.TIME}, includeNullJdbcType = true)
@Alias("dynamicTimestampTypeHandler")
public class DynamicTimestampTypeHandler extends BaseTypeHandler<Object> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
		throws SQLException {
		if (parameter == null) {
			ps.setNull(i, Types.TIMESTAMP);
			return;
		}
		if (parameter instanceof Timestamp) {
			ps.setTimestamp(i, (Timestamp) parameter);
		} else if (parameter instanceof Date) {
			ps.setTimestamp(i, new Timestamp(((Date) parameter).getTime()));
		} else if (parameter instanceof Number) {
			ps.setTimestamp(i, new Timestamp(((Number) parameter).longValue()));
		} else {
			Date date = Converters.convert(Date.class, parameter);
			ps.setTimestamp(i, new Timestamp(date.getTime()));
		}
	}

	@Override
	public Timestamp getNullableResult(ResultSet rs, String columnName)
		throws SQLException {
		Timestamp sqlTimestamp = rs.getTimestamp(columnName);
		return sqlTimestamp;
	}

	@Override
	public Timestamp getNullableResult(ResultSet rs, int columnIndex)
		throws SQLException {
		Timestamp sqlTimestamp = rs.getTimestamp(columnIndex);
		return sqlTimestamp;
	}

	@Override
	public Timestamp getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {
		Timestamp sqlTimestamp = cs.getTimestamp(columnIndex);
		return sqlTimestamp;
	}
}
