package io.polaris.mybatis.scripting;

import java.util.List;

import io.polaris.core.jdbc.sql.SqlTextParsers;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since Mar 17, 2026
 */
class DelegateBoundSql extends BoundSql {
	private final BoundSql raw;
	private final String sql;

	public DelegateBoundSql(Configuration configuration, BoundSql raw) {
		super(configuration, raw.getSql(), raw.getParameterMappings(), raw.getParameterObject());
		String sql = raw.getSql();
		sql = SqlTextParsers.resolveTableRef(sql);
		this.raw = raw;
		this.sql = sql;
	}

	@Override
	public String getSql() {
		return this.sql;
	}

	@Override
	public List<ParameterMapping> getParameterMappings() {
		return raw.getParameterMappings();
	}

	@Override
	public Object getParameterObject() {
		return raw.getParameterObject();
	}

	@Override
	public boolean hasAdditionalParameter(String name) {
		return raw.hasAdditionalParameter(name);
	}

	@Override
	public void setAdditionalParameter(String name, Object value) {
		raw.setAdditionalParameter(name, value);
	}

	@Override
	public Object getAdditionalParameter(String name) {
		return raw.getAdditionalParameter(name);
	}
}
