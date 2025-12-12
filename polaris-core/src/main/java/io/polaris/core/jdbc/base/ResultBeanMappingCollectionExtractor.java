package io.polaris.core.jdbc.base;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Supplier;

import io.polaris.core.jdbc.sql.query.Pageable;

/**
 * @author Qt
 * @since 1.8
 */
public class ResultBeanMappingCollectionExtractor<C extends Collection<T>, T> implements ResultExtractor<C> {

	private final Supplier<C> collectionBuilder;
	private final ResultRowMapper<T> mapper;

	public ResultBeanMappingCollectionExtractor(Supplier<C> collectionBuilder, BeanMapping<T> mapping) {
		this.collectionBuilder = collectionBuilder;
		this.mapper = ResultRowMappers.ofMapping(mapping);
	}

	@Override
	public C extract(ResultSet rs) throws SQLException {
		C list = collectionBuilder.get();
		ResultExtractors.extract(rs, mapper, list);
		return list;
	}

	@Override
	public C extract(ResultSet rs, Pageable pageable) throws SQLException {
		C list = collectionBuilder.get();
		ResultExtractors.extract(rs, mapper, list, pageable);
		return list;
	}
}
