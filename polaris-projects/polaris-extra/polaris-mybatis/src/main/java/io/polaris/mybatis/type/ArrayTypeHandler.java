package io.polaris.mybatis.type;

import org.apache.ibatis.type.*;

import java.sql.*;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
@MappedTypes(Object[].class)
@MappedJdbcTypes({JdbcType.ARRAY,JdbcType.VARCHAR,JdbcType.CHAR})
public class ArrayTypeHandler extends BaseTypeHandler<Object[]> {
	private static final String TYPE_NAME_VARCHAR = "VARCHAR";
	private static final String TYPE_NAME_INTEGER = "INTEGER";
	private static final String TYPE_NAME_BOOLEAN = "BOOLEAN";
	private static final String TYPE_NAME_NUMERIC = "NUMERIC";

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object[] parameter, JdbcType jdbcType) throws SQLException {

		/*
		StringBuilder arrayString = new StringBuilder("{");
		for (int j = 0, l = parameter.length; j < l; j++) {
			arrayString.append(parameter[j]);
			if (j < l - 1) {
				arrayString.append(",");
			}
		}
		arrayString.append("}");
		ps.setString(i, arrayString.toString());
	 	*/
		String typeName = null;
		if (parameter instanceof Integer[]) {
			typeName = TYPE_NAME_INTEGER;
		} else if (parameter instanceof String[]) {
			typeName = TYPE_NAME_VARCHAR;
		} else if (parameter instanceof Boolean[]) {
			typeName = TYPE_NAME_BOOLEAN;
		} else if (parameter instanceof Double[]) {
			typeName = TYPE_NAME_NUMERIC;
		}

		if (typeName == null) {
			throw new TypeException("ArrayTypeHandler parameter typeName error, your type is " + parameter.getClass().getName());
		}

		Connection conn = ps.getConnection();
		Array array = conn.createArrayOf(typeName, parameter);
		ps.setArray(i, array);
	}

	@Override
	public Object[] getNullableResult(ResultSet rs, String columnName)
		throws SQLException {

		return getArray(rs.getArray(columnName));
	}

	@Override
	public Object[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {

		return getArray(rs.getArray(columnIndex));
	}

	@Override
	public Object[] getNullableResult(CallableStatement cs, int columnIndex)
		throws SQLException {

		return getArray(cs.getArray(columnIndex));
	}

	private Object[] getArray(Array array) {

		if (array == null) {
			return null;
		}

		try {
			return (Object[]) array.getArray();
		} catch (Exception e) {
		}

		return null;
	}
}
