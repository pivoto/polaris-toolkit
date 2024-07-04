package io.polaris.mybatis.scripting;

import java.util.List;

import io.polaris.core.jdbc.sql.SqlTextParsers;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * 通过`@Lang(TableRefResolvableDriver.class)`配置添加对解析实体表与字段的引用表达式的支持
 * @author Qt
 * @since  Feb 21, 2024
 */
public class TableRefResolvableDriver extends XMLLanguageDriver {

	@Override
	public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		return new TableRefResolvableSqlSource(configuration, sqlSource);
	}

	@Override
	public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
		SqlSource sqlSource = super.createSqlSource(configuration, script, parameterType);
		return new TableRefResolvableSqlSource(configuration, sqlSource);
	}

	static class TableRefResolvableSqlSource implements SqlSource {
		private final Configuration configuration;
		private final SqlSource sqlSource;

		public TableRefResolvableSqlSource(Configuration configuration, SqlSource sqlSource) {
			this.configuration = configuration;
			this.sqlSource = sqlSource;
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
			return new DelegateBoundSql(configuration, boundSql);
		}
	}

	static class DelegateBoundSql extends BoundSql{
		private final BoundSql raw;
		private final String sql;

		public DelegateBoundSql(Configuration configuration,BoundSql raw) {
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
}
