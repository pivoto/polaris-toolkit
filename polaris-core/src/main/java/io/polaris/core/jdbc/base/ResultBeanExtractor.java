package io.polaris.core.jdbc.base;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.polaris.core.jdbc.sql.query.Pageable;
import io.polaris.core.lang.bean.CaseModeOption;

/**
 * @author Qt
 */
public class ResultBeanExtractor<T> implements ResultExtractor<T> {
	private final ResultRowMapper<T> mapper;

	public ResultBeanExtractor(Class<T> beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultBeanExtractor(Type beanType) {
		this(beanType, CaseModeOption.all());
	}

	public ResultBeanExtractor(Class<T> beanType, CaseModeOption caseMode) {
		this.mapper = ResultRowMappers.ofBean(beanType, caseMode);
	}

	public ResultBeanExtractor(Type beanType, CaseModeOption caseMode) {
		this.mapper = ResultRowMappers.ofBean(beanType, caseMode);
	}

	@Override
	public T extract(ResultSet rs) throws SQLException {
		return ResultExtractors.extract(rs, mapper);
	}

	@Override
	public T extract(ResultSet rs, Pageable pageable) throws SQLException {
		return extract(rs);
	}
}
