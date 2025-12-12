package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultSingleCollectionExtractor<C extends Collection<T>, T> implements ResultExtractor<C> {
	private final Supplier<C> collectionBuilder;
	private final ResultRowMapper<T> mapper;

	public ResultSingleCollectionExtractor(Supplier<C> collectionBuilder) {
		this(collectionBuilder, Object.class);
	}

	public ResultSingleCollectionExtractor(Supplier<C> collectionBuilder, Type type) {
		this.collectionBuilder = collectionBuilder;
		this.mapper = ResultRowMappers.ofSingle(type);
	}

	@Override
	public C extract(ResultSet rs) throws SQLException {
		C list = collectionBuilder.get();
		ResultExtractors.extract(rs, mapper, list, (String[]) null);
		return list;
	}

	@Override
	public C extract(ResultSet rs, Pageable pageable) throws SQLException {
		C list = collectionBuilder.get();
		ResultExtractors.extract(rs, mapper, list, pageable);
		return list;
	}
}
