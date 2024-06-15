package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultSingleCollectionExtractor<C extends Collection<Object>> implements ResultExtractor<C> {
	private final Supplier<C> collectionBuilder;
	private final ResultRowMapper<Object> rowMapper;

	public ResultSingleCollectionExtractor(Supplier<C> collectionBuilder) {
		this(collectionBuilder, Object.class);
	}

	public ResultSingleCollectionExtractor(Supplier<C> collectionBuilder, Type type) {
		this.collectionBuilder = collectionBuilder;
		this.rowMapper = ResultRowMappers.ofSingle(type);
	}

	@Override
	public C extract(ResultSet rs) throws SQLException {
		C list = collectionBuilder.get();
		while (rs.next()) {
			list.add(rowMapper.map(rs, null));
		}
		return list;
	}
}
