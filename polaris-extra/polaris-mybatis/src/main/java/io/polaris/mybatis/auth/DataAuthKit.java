package io.polaris.mybatis.auth;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.jdbc.sql.consts.Relation;
import io.polaris.core.spi.Service;
import io.polaris.core.spi.SpiLoader;
import io.polaris.core.spi.SpiLoaders;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;
import io.polaris.mybatis.auth.impl.DefaultDataAuthConfigurator;
import io.polaris.mybatis.auth.impl.DefaultDataAuthCtxProvider;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

/**
 * @author Qt
 * @since Aug 28, 2023
 */
@Slf4j
public class DataAuthKit {

	private static final DataAuthKit instance = new DataAuthKit();
	private final Map<String, DataScopeProvider> dataScopeProviders = new ConcurrentHashMap<>();
	private volatile DataAuthConfigurator configurator;
	private volatile DataAuthCtxProvider dataAuthCtxProvider;

	private DataAuthKit() {
		try {
			SpiLoader<DataScopeProvider> services = SpiLoaders.load(DataScopeProvider.class);
			for (Service<DataScopeProvider> service : services) {
				DataScopeProvider provider = service.getSingleton();
				String name = provider.name();
				if (Strings.isNotBlank(name)) {
					log.info("加载DataScopeProvider成功：{}={}", name, provider.getClass().getName());
					dataScopeProviders.putIfAbsent(name, provider);
				}
			}
		} catch (Exception e) {
			log.error("加载DataScopeProvider失败", e);
		}

		try {
			SpiLoader<DataAuthConfigurator> services = SpiLoaders.load(DataAuthConfigurator.class);
			DataAuthConfigurator configurator = services.getSingleton();
			if (configurator == null) {
				configurator = new DefaultDataAuthConfigurator();
			}
			log.info("加载DataAuthConfigurator成功：{}", configurator.getClass().getName());
			this.configurator = configurator;
		} catch (Exception e) {
			log.error("加载DataAuthConfigurator失败", e);
		}

		try {
			SpiLoader<DataAuthCtxProvider> services = SpiLoaders.load(DataAuthCtxProvider.class);
			DataAuthCtxProvider dataAuthCtxProvider = services.getSingleton();
			if (dataAuthCtxProvider == null) {
				dataAuthCtxProvider = new DefaultDataAuthCtxProvider();
			}
			log.info("加载DataAuthCtxProvider成功：{}", dataAuthCtxProvider.getClass().getName());
			this.dataAuthCtxProvider = dataAuthCtxProvider;
		} catch (Exception e) {
			log.error("加载DataAuthCtxProvider失败", e);
		}
	}

	public static DataAuthKit instance() {
		return instance;
	}

	@Nullable
	public DataAuthConfigurator getConfigurator() {
		return configurator;
	}

	public void setConfigurator(@Nullable DataAuthConfigurator configurator) {
		this.configurator = configurator;
	}

	@Nullable
	public DataAuthCtxProvider getDataAuthCtxProvider() {
		return dataAuthCtxProvider;
	}

	public void setDataAuthCtxProvider(DataAuthCtxProvider dataAuthCtxProvider) {
		this.dataAuthCtxProvider = dataAuthCtxProvider;
	}

	@Nullable
	public DataScopeProvider getDataScopeProvider(String scopeName) {
		return dataScopeProviders.get(scopeName);
	}

	public Set<String> getDataScopeNames() {
		return Collections.unmodifiableSet(dataScopeProviders.keySet());
	}

	public void unregisterDataScopeProvider(String scopeName) {
		if (Strings.isNotBlank(scopeName)) {
			dataScopeProviders.remove(scopeName);
		}
	}

	public boolean registerDataScopeProvider(DataScopeProvider provider) {
		return registerDataScopeProvider(provider, true);
	}

	public boolean registerDataScopeProvider(DataScopeProvider provider, boolean override) {
		if (provider == null) {
			return false;
		}
		String name = provider.name();
		if (Strings.isNotBlank(name)) {
			if (override) {
				dataScopeProviders.put(name, provider);
				return true;
			} else {
				DataScopeProvider old = dataScopeProviders.putIfAbsent(provider.name(), provider);
				return old == null;
			}
		}
		return false;
	}

	/**
	 * 是否标准名称，非空，且只由字母、数字、下划线组成，不包括引号、反引号等字符
	 *
	 * @param name 表名、字段名等标识名
	 * @return true表示标准名称，false表示非标准名称
	 */
	public boolean isStandardName(@Nullable String name) {
		if (name == null || name.isEmpty()) {
			return false;
		}
		char[] chs = name.toCharArray();
		if (!isStandardChar(chs[0])) {
			return false;
		}
		if (chs.length > 1 && !isStandardChar(chs[chs.length - 1])) {
			return false;
		}
		for (int i = 1; i < chs.length - 1; i++) {
			if (!isStandardChar(chs[i])) {
				return false;
			}
		}
		return true;
	}

	private boolean isStandardChar(char ch) {
		if (ch >= '0' && ch <= '9') {
			return true;
		}
		if (ch >= 'a' && ch <= 'z') {
			return true;
		}
		if (ch >= 'A' && ch <= 'Z') {
			return true;
		}
		if (ch == '_') {
			return true;
		}
		return false;
	}

	/**
	 * 是否启用mybatis拦截器
	 *
	 * @return true表示启用，false表示不启用
	 */
	public boolean isMybatisInterceptorEnabled() {
		return configurator == null || configurator.isMybatisInterceptorEnabled();
	}

	@Nonnull
	public Tuple2<TableWhereGenerator, TableColumnFilter> getCurrentResolver() {
		Set<String> globalScopes = configurator == null ? Collections.emptySet() : configurator.getGlobalScopes();
		Relation relation;
		Set<String> includedScopes;
		Set<String> excludedScopes;
		if (dataAuthCtxProvider == null) {
			relation = DataAuthCtx.getRelation();
			includedScopes = DataAuthCtx.getIncludedScopes();
			excludedScopes = DataAuthCtx.getExcludedScopes();
		} else {
			relation = dataAuthCtxProvider.getRelation();
			includedScopes = dataAuthCtxProvider.getIncludedScopes();
			excludedScopes = dataAuthCtxProvider.getExcludedScopes();
		}
		final Set<String> scopes;
		if (excludedScopes.isEmpty()) {
			if (globalScopes.isEmpty()) {
				scopes = includedScopes;
			} else if (includedScopes.isEmpty()) {
				scopes = globalScopes;
			} else {
				scopes = new LinkedHashSet<>();
				scopes.addAll(globalScopes);
				scopes.addAll(includedScopes);
			}
		} else {
			if (includedScopes.isEmpty() && globalScopes.isEmpty()) {
				scopes = Collections.emptySet();
			} else {
				scopes = new LinkedHashSet<>();
				scopes.addAll(globalScopes);
				scopes.addAll(includedScopes);
				scopes.removeAll(excludedScopes);
			}
		}
		if (scopes.isEmpty()) {
			return Tuples.of(null, null);
		}

		TableWhereGenerator whereGenerator = tables -> {
			if (tables == null || tables.isEmpty()) {
				return null;
			}
			StringBuilder whereSql = new StringBuilder();
			String conj = relation == Relation.AND ? " AND " : " OR ";
			//String defaultSql = relation == Relation.AND ? " 1=2 " : " 1=1 ";

			for (Table table : tables) {
				String tableName = table.getName();
				if (Strings.isBlank(tableName)) {
					continue;
				}
				StringBuilder tableSql = new StringBuilder();
				String tableAlias = table.getAlias() == null ? null : table.getAlias().getName();
				for (String scope : scopes) {
					boolean scopeEnabled = configurator != null && configurator.isScopeEnabled(tableName, scope);
					if (!scopeEnabled) {
						continue;
					}
					DataScopeProvider provider = dataScopeProviders.get(scope);
					if (provider == null) {
						continue;
					}
					String where = provider.where(tableName, tableAlias);
					if (Strings.isNotBlank(where)) {
						if (tableSql.length() > 0) {
							tableSql.append(conj);
						}
						tableSql.append("(").append(where).append(")");
					}
				}
				if (tableSql.length() > 0) {
					if (whereSql.length() > 0) {
						whereSql.append(" AND ");
					}
					whereSql.append("(").append(tableSql).append(")");
				}
			}
			if (whereSql.length() == 0) {
				return null;
			}
			return whereSql.toString();
		};
		TableColumnFilter columnFilter = (table, colunmName) -> {
			String tableName = table.getName();
			if (Strings.isBlank(tableName)) {
				return true;
			}
			if (relation == Relation.AND) {
				for (String scope : scopes) {
					boolean scopeEnabled = configurator != null && configurator.isScopeEnabled(tableName, scope);
					if (!scopeEnabled) {
						continue;
					}
					DataScopeProvider provider = dataScopeProviders.get(scope);
					if (provider == null) {
						continue;
					}
					boolean selected = provider.select(tableName, colunmName);
					if (!selected) {
						return false;
					}
				}
				return true;
			} else {
				boolean denied = false;
				for (String scope : scopes) {
					boolean scopeEnabled = configurator != null && configurator.isScopeEnabled(tableName, scope);
					if (!scopeEnabled) {
						continue;
					}
					DataScopeProvider provider = dataScopeProviders.get(scope);
					if (provider == null) {
						continue;
					}
					boolean selected = provider.select(tableName, colunmName);
					if (selected) {
						return true;
					} else {
						denied = true;
					}
				}
				return !denied;
			}
		};
		return Tuples.of(whereGenerator, columnFilter);
	}

	public String resolveSelect(@Nonnull String sql) throws JSQLParserException {
		Tuple2<TableWhereGenerator, TableColumnFilter> tuple = getCurrentResolver();
		TableWhereGenerator whereGenerator = tuple.getFirst();
		TableColumnFilter columnFilter = tuple.getSecond();
		return resolveSelect(sql, whereGenerator, columnFilter);
	}

	public String resolveSelect(@Nonnull String sql, @Nullable TableWhereGenerator conditionAppender) throws JSQLParserException {
		return resolveSelect(sql, conditionAppender, null);
	}

	public String resolveSelect(@Nonnull String sql, @Nullable TableColumnFilter columnFilter) throws JSQLParserException {
		return resolveSelect(sql, null, columnFilter);
	}

	public String resolveSelect(@Nonnull String sql, @Nullable TableWhereGenerator whereGenerator, @Nullable TableColumnFilter columnFilter) throws JSQLParserException {
		if (whereGenerator == null && columnFilter == null) {
			return sql;
		}
		Statement stmt = CCJSqlParserUtil.parse(sql);
		if (!(stmt instanceof Select)) {
			return sql;
		}
		SelectBody selectBody = ((Select) stmt).getSelectBody();
		resolve(selectBody, whereGenerator, columnFilter);
		return stmt.toString();
	}

	public boolean resolveSelect(@Nonnull Statement stmt, TableWhereGenerator whereGenerator, TableColumnFilter columnFilter) throws JSQLParserException {
		if (whereGenerator == null && columnFilter == null) {
			return false;
		}
		if (!(stmt instanceof Select)) {
			return false;
		}
		SelectBody selectBody = ((Select) stmt).getSelectBody();
		resolve(selectBody, whereGenerator, columnFilter);
		List<WithItem> withItemsList = ((Select) stmt).getWithItemsList();
		if (withItemsList != null && !withItemsList.isEmpty()) {
			for (WithItem withItem : withItemsList) {
				resolve(withItem, whereGenerator, columnFilter);
			}
		}
		return true;
	}

	private void resolve(SelectBody selectBody, TableWhereGenerator whereGenerator, TableColumnFilter columnFilter) throws JSQLParserException {
		if (selectBody instanceof PlainSelect) {
			PlainSelect plainSelect = (PlainSelect) selectBody;
			resolve(plainSelect, whereGenerator, columnFilter);
		} else if (selectBody instanceof WithItem) {
			WithItem withItem = (WithItem) selectBody;
			if (withItem.getSubSelect() != null) {
				resolve(withItem.getSubSelect().getSelectBody(), whereGenerator, columnFilter);
			}
		} else if (selectBody instanceof SetOperationList) {
			SetOperationList operationList = (SetOperationList) selectBody;
			if (operationList.getSelects() != null && !operationList.getSelects().isEmpty()) {
				List<SelectBody> plainSelects = operationList.getSelects();
				for (SelectBody plainSelect : plainSelects) {
					resolve(plainSelect, whereGenerator, columnFilter);
				}
			}
		}
	}

	private void resolve(PlainSelect plainSelect, TableWhereGenerator whereGenerator,
		TableColumnFilter columnFilter) throws JSQLParserException {
		Map<String, Table> tables = new LinkedHashMap<>();
		{
			FromItem fromItem = plainSelect.getFromItem();
			fetchTables(tables, fromItem);
		}
		List<Join> joins = plainSelect.getJoins();
		if (joins != null) {
			for (Join join : joins) {
				FromItem item = join.getRightItem();
				fetchTables(tables, item);
			}
		}
		if (whereGenerator != null) {
			String conditionSql = Strings.trimToNull(whereGenerator.apply(tables.values()));
			if (conditionSql != null) {
				Expression condition = CCJSqlParserUtil.parseCondExpression(conditionSql);
				Expression where = plainSelect.getWhere();
				if (where == null) {
					plainSelect.setWhere(condition);
				} else {
					if (!(where instanceof Parenthesis)) {
						where = new Parenthesis(where);
					}
					if (!(condition instanceof Parenthesis)) {
						condition = new Parenthesis(condition);
					}
					AndExpression andExpression = new AndExpression(where, condition);
					plainSelect.setWhere(andExpression);
				}
			}
		}
		if (columnFilter != null) {
			List<SelectItem> selectItems = plainSelect.getSelectItems();
			Iterator<SelectItem> iter = selectItems.iterator();
			while (iter.hasNext()) {
				SelectItem selectItem = iter.next();
				if (selectItem instanceof SelectExpressionItem) {
					((SelectExpressionItem) selectItem).getAlias();
					Expression expression = ((SelectExpressionItem) selectItem).getExpression();
					if (expression instanceof Column) {
						Table colTable = ((Column) expression).getTable();
						if (colTable != null) {
							String tableAlias = colTable.getName();
							Table table = tables.get(tableAlias);
							if (table != null) {
								boolean filtered = columnFilter.apply(table, ((Column) expression).getColumnName());
								if (!filtered) {
									iter.remove();
								}
							}
						}
					} else {
						// 其他表达式忽略
					}
				} else if (selectItem instanceof AllTableColumns) {
					// 其他表达式忽略
				} else if (selectItem instanceof AllColumns) {
					// 其他表达式忽略
				}
			}
		}
	}

	private void fetchTables(Map<String, Table> tables, FromItem fromItem) {
		if (fromItem instanceof Table) {
			Alias alias = ((Table) fromItem).getAlias();
			if (alias != null) {
				tables.put(alias.getName(), (Table) fromItem);
			} else {
				tables.put(((Table) fromItem).getName(), (Table) fromItem);
			}
		}
	}

}
