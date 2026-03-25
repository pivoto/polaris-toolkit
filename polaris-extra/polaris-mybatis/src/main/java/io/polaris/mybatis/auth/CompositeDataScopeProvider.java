package io.polaris.mybatis.auth;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.string.Strings;
import io.polaris.mybatis.auth.consts.DataScopeRelation;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
public interface CompositeDataScopeProvider extends DataScopeProvider {


	/**
	 * @return 需要组合的多个数据权限名称
	 */
	Set<String> compositeNames();

	/**
	 * @return 组合关系
	 */
	default DataScopeRelation compositeRelation() {
		return DataScopeRelation.OR;
	}

	@Override
	default String where(@Nonnull String tableName, @Nullable String tableAlias) {
		Set<String> names = compositeNames();
		if (names == null || names.isEmpty()) {
			return null;
		}
		DataScopeRelation relation = compositeRelation();
		String conj = relation == DataScopeRelation.AND ? " AND " : " OR ";
		StringBuilder sql = new StringBuilder();
		for (String name : names) {
			DataScopeProvider provider = DataAuthKit.instance().getDataScopeProvider(name);
			if (provider == null) {
				continue;
			}
			String where = provider.where(tableName, tableAlias);
			if (Strings.isNotBlank(where)) {
				if (sql.length() > 0) {
					sql.append(conj);
				}
				sql.append("(").append(where).append(")");
			}
		}
		if (sql.length() == 0) {
			return null;
		}
		return sql.toString();
	}

	@Override
	default boolean select(@Nonnull String tableName, String colunmName) {
		Set<String> names = compositeNames();
		if (names == null || names.isEmpty()) {
			return true;
		}
		DataScopeRelation relation = compositeRelation();

		if (relation == DataScopeRelation.AND) {
			for (String name : names) {
				DataScopeProvider provider = DataAuthKit.instance().getDataScopeProvider(name);
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
			for (String name : names) {
				DataScopeProvider provider = DataAuthKit.instance().getDataScopeProvider(name);
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
	}
}
