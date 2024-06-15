package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanCollectionExtractor<C extends Collection<T>, T> implements ResultExtractor<C> {
	private final ResultRowMapper<T> mapper;
	private final Supplier<C> collectionBuilder;

	public ResultBeanCollectionExtractor(Supplier<C> collectionBuilder, Class<T> beanType) {
		this(collectionBuilder, beanType, true, true);
	}

	public ResultBeanCollectionExtractor(Supplier<C> collectionBuilder, Class<T> beanType, boolean caseInsensitive, boolean caseCamel) {
		this.collectionBuilder = collectionBuilder;
		this.mapper = ResultRowMappers.ofBean(beanType, caseInsensitive, caseCamel);
	}
	public ResultBeanCollectionExtractor(Supplier<C> collectionBuilder, Type beanType) {
		this(collectionBuilder, beanType, true, true);
	}

	public ResultBeanCollectionExtractor(Supplier<C> collectionBuilder, Type beanType, boolean caseInsensitive, boolean caseCamel) {
		this.collectionBuilder = collectionBuilder;
		this.mapper = ResultRowMappers.ofBean(beanType, caseInsensitive, caseCamel);
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
			T bean = this.mapper.map(rs, keys);
			list.add(bean);
		}
		return list;
	}
}
