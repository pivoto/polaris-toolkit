package io.polaris.mybatis.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public abstract class StringTokenizerTypeHandler<T> extends BaseTypeHandler<T[]> {
	private Class<T> clazz;

	public StringTokenizerTypeHandler(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, T[] ts, JdbcType jdbcType) throws SQLException {
		StringBuilder result = new StringBuilder();
		if(ts.length>0){
			result.append(ts[0]);
			for (int idx = 1; idx < ts.length; idx++) {
				result.append(",").append(ts[idx]);
			}
		}
		ps.setString(i, result.toString());
	}

	@Override
	public T[] getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
		return toArray(resultSet.getString(columnName));
	}

	@Override
	public T[] getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
		return toArray(resultSet.getString(columnIndex));
	}

	@Override
	public T[] getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
		return toArray(callableStatement.getString(columnIndex));
	}

	T[] toArray(String columnValue) {
		if (columnValue == null) {
			return createArray(0);
		}
		String[] values = columnValue.split(",");
		T[] array = createArray(values.length);
		for (int i = 0; i < values.length; i++) {
			array[i] = parseString(values[i]);
		}
		return array;
	}

	T[] createArray(int size) {
		return (T[]) Array.newInstance(clazz, size);
	}

	abstract T parseString(String value);
}
