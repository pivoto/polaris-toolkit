package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Feb 06, 2024
 */
public class ResultRowMappers {
	public static final ResultRowMapMapper<Map<String, Object>> MAP_MAPPER = new ResultRowMapMapper<>();
	public static final ResultRowVoidMapper VOID_MAPPER = new ResultRowVoidMapper();

	public static <T> ResultRowMapper<T> ofBean(Class<T> type) {
		return new ResultRowBeanMapper<>(type);
	}

	public static <T> ResultRowMapper<T> ofBean(Type type) {
		return new ResultRowBeanMapper<>(type);
	}

	public static <T> ResultRowMapper<T> ofBean(Class<T> type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultRowBeanMapper<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultRowMapper<T> ofBean(Type type, boolean caseInsensitive, boolean caseCamel) {
		return new ResultRowBeanMapper<>(type, caseInsensitive, caseCamel);
	}

	public static <T> ResultRowMapper<T> ofSingle(Class<T> type) {
		return new ResultRowSingleMapper<>(type);
	}

	public static <T> ResultRowMapper<T> ofSingle(Type type) {
		return new ResultRowSingleMapper<>(type);
	}

	public static <T> ResultRowMapper<T> ofMapping(BeanMapping<T> mapping) {
		return new ResultRowBeanMappingMapper<>(mapping);
	}

	public static ResultRowMapper<Map<String, Object>> ofMap() {
		return MAP_MAPPER;
	}

	public static <T extends Map<String, Object>> ResultRowMapper<T> ofMap(Class<T> type) {
		return new ResultRowMapMapper<>(type);
	}

	public static ResultRowMapper<Void> ofVoid() {
		return VOID_MAPPER;
	}

	public static String[] getColumns(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		String[] cols = new String[cnt];
		for (int i = 1; i <= cnt; i++) {
			cols[i - 1] = meta.getColumnLabel(i);
		}
		return cols;
	}
}
