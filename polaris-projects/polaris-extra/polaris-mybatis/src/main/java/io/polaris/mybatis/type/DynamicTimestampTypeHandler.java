package io.polaris.mybatis.type;

import io.polaris.core.converter.Converters;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.*;

import java.sql.*;
import java.util.Date;

/**
 * @author Qt
 * @see DateTypeHandler
 * @see LocalDateTypeHandler
 * @since  Aug 28, 2023
 */
@Slf4j
@MappedTypes(Timestamp.class)
@MappedJdbcTypes({JdbcType.DATE, JdbcType.TIMESTAMP})
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
