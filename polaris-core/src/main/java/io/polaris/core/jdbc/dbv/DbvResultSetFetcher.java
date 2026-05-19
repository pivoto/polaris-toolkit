package io.polaris.core.jdbc.dbv;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.map.CaseInsensitiveMap;

/**
 * @author Qt
 */
public class DbvResultSetFetcher {
	private static final Logger log = Loggers.of(DbvResultSetFetcher.class);

	public static Map<String, Object> fetch(final ResultSet rs) throws SQLException {
		final ResultSetMetaData meta = rs.getMetaData();
		final int cnt = meta.getColumnCount();
		final Map<String, Object> map = new CaseInsensitiveMap<>(new LinkedHashMap<>(), true);
		for (int i = 1; i <= cnt; i++) {
			map.put(meta.getColumnLabel(i).toUpperCase(), rs.getObject(i));
		}
		return map;
	}

	public static List<Map<String, Object>> fetchList(final ResultSet rs)
		throws SQLException {
		final List<Map<String, Object>> list = new ArrayList<>();
		while (rs.next()) {
			list.add(fetch(rs));
		}
		return list;
	}

	public static Object fetch(final ResultSet rs, final Object obj) throws SQLException {
		DbvColumnFieldsMeta columnFields = DbvColumnFieldsMeta.of(rs, obj.getClass());
		return fetch(columnFields, rs, obj);
	}

	public static <T> List<T> fetchList(final ResultSet rs, final Class<T> clazz)
		throws SQLException, ReflectiveOperationException {
		DbvColumnFieldsMeta columnFields = DbvColumnFieldsMeta.of(rs, clazz);
		final List<T> list = new ArrayList<T>();
		while (rs.next()) {
			final T object = clazz.newInstance();
			fetch(columnFields, rs, object);
			list.add(object);
		}
		return list;
	}

	public static Object fetch(DbvColumnFieldsMeta columnFields, ResultSet rs, Object obj) {
		Set<DbvColumnFieldMeta> fields = columnFields.getFields();
		for (DbvColumnFieldMeta meta : fields) {
			DbvColumnGetter handler = meta.getGetter();
			try {
				meta.set(obj, handler.getColumnValue(rs, meta.getColumn()));
			} catch (SQLException | IllegalAccessException e) {
				log.warn(e.getMessage(), e);
			}
		}
		return obj;
	}

	public static Set<String> getColumnNames(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount();
		Set<String> colSet = new LinkedHashSet<>(columnCount);
		for (int i = 1; i <= columnCount; i++) {
			colSet.add(meta.getColumnName(i).toUpperCase());
		}
		return colSet;
	}


}
