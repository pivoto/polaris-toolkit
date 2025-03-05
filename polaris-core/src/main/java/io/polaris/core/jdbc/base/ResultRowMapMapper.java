package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since  Feb 06, 2024
 */
public class ResultRowMapMapper<T extends Map<String, Object>> extends BaseResultRowMapper<T> {

	private final Supplier<T> mapBuilder;

	@SuppressWarnings("unchecked")
	public ResultRowMapMapper() {
		this(() -> (T) Maps.newUpperCaseLinkedHashMap());
	}

	public ResultRowMapMapper(Class<T> type) {
		this( () -> Reflects.newInstanceIfPossible(type));
	}

	public ResultRowMapMapper(Supplier<T> mapBuilder) {
		this.mapBuilder = mapBuilder;
	}

	@Override
	protected T doMap(ResultSet rs, String[] columns) throws SQLException {
		T map = mapBuilder.get();
		for (int i = 1; i <= columns.length; i++) {
			String key = columns[i - 1];
			Object val = BeanMappings.getResultValue(rs, i, Object.class);
			map.put(key, val);
		}
		return map;
	}

}
