package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import io.polaris.core.reflect.Reflects;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultMapCollectionExtractor<C extends Collection<T>, T extends Map<String, Object>> implements ResultExtractor<C> {

	private final Supplier<C> collectionBuilder;
	private final ResultRowMapper<T> mapper;

	@SuppressWarnings({"unchecked"})
	public ResultMapCollectionExtractor(Supplier<C> collectionBuilder, Class<T> mapType) {
		this.collectionBuilder = collectionBuilder;
		if (mapType == null) {
			mapper = (ResultRowMapper<T>) ResultRowMappers.ofMap();
		} else {
			mapper = ResultRowMappers.ofMap(mapType);
		}
	}

	public ResultMapCollectionExtractor(Supplier<C> collectionBuilder) {
		this(collectionBuilder, null);
	}

	public ResultMapCollectionExtractor(Class<C> collectionType, Class<T> mapType) {
		this(() -> Reflects.newInstanceIfPossible(collectionType), mapType);
	}

	@Override
	public C extract(ResultSet rs) throws SQLException {
		C list = collectionBuilder.get();

		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		String[] keys = new String[cnt];
		for (int i = 1; i <= cnt; i++) {
			keys[i - 1] = meta.getColumnLabel(i);
		}
		while (rs.next()) {
			T map = mapper.map(rs, keys);
			list.add(map);
		}
		return list;
	}
}
