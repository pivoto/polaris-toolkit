package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingCollectionExtractor<C extends Collection<T>,T> implements ResultExtractor<C> {

	private final Supplier<C> collectionBuilder;
	private final ResultRowMapper<T> mapper;

	public ResultBeanMappingCollectionExtractor(Supplier<C> collectionBuilder,BeanMapping<T> mapping) {
		this.collectionBuilder = collectionBuilder;
		this.mapper = ResultRowMappers.ofMapping(mapping);
	}

	@Override
	public C extract(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		String[] keys = new String[cnt];
		for (int i = 1; i <= cnt; i++) {
			keys[i - 1] = meta.getColumnLabel(i);
		}

		C list = collectionBuilder.get();
		while (rs.next()) {
			T bean = this.mapper.map(rs, keys);
			list.add(bean);
		}
		return list;
	}
}
