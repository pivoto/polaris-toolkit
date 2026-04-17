package io.polaris.core.jdbc.sql;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.polaris.core.collection.Sets;
import io.polaris.core.jdbc.annotation.EntityDelete;
import io.polaris.core.jdbc.annotation.EntityInsert;
import io.polaris.core.jdbc.annotation.EntityMerge;
import io.polaris.core.jdbc.annotation.EntitySelect;
import io.polaris.core.jdbc.annotation.EntityUpdate;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.string.Strings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Qt
 * @since Mar 31, 2026
 */
@EqualsAndHashCode
@ToString
public class BindingKeyModel {
	public static final BindingKeyModel DEFAULT = new BindingKeyModel().freeze();
	/** 实体对象参数 */
	private String entity = BindingKeys.ENTITY;
	/** where条件参数 */
	private String where = BindingKeys.WHERE;
	/** 排序列参数 */
	private String orderBy = BindingKeys.ORDER_BY;
	/** insert或update中需要包含的空值列集合或数组，包括NULL与空串 */
	private String includeEmptyColumns = BindingKeys.INCLUDE_EMPTY_COLUMNS;
	/** insert或update中需要包含空值处理的标志 */
	private String includeAllEmpty = BindingKeys.INCLUDE_ALL_EMPTY;
	/** insert或update中需要包含的列列集合或数组 */
	private String includeColumns = BindingKeys.INCLUDE_COLUMNS;
	/** insert或update中需要排除的列列集合或数组 */
	private String excludeColumns = BindingKeys.EXCLUDE_COLUMNS;
	/** sql 语句型参数 */
	private String sql = BindingKeys.SQL;
	/** where条件中需要包含的空值列列集合或数组，包括NULL与空串 */
	private String whereIncludeEmptyColumns = BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS;
	/** where条件中是否包含空值处理的标志，包括NULL与空串 */
	private String whereIncludeAllEmpty = BindingKeys.WHERE_INCLUDE_ALL_EMPTY;
	/** where条件中需要包含的列列集合或数组 */
	private String whereIncludeColumns = BindingKeys.WHERE_INCLUDE_COLUMNS;
	/** where条件中需要排除的列列集合或数组 */
	private String whereExcludeColumns = BindingKeys.WHERE_EXCLUDE_COLUMNS;
	/** 是否支持逻辑删除处理的标志 */
	private String withLogicDeleted = BindingKeys.WITH_LOGIC_DELETED;
	/** 是否使用Count语句查询存在性 */
	private String existsByCount = BindingKeys.EXISTS_BY_COUNT;

	private boolean frozen = false;

	/**
	 * 生成空值列的处理开关控制器，绑定健同时支持 entity 与 where 关联键，适用于不需要区分 entity 与 where 类型的场景，如 select、delete
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toFusionColumnPredicate(Map<String, Object> bindings) {
		BindingKeyModel bindingKeyModel = this;
		// 兼容 where keys
		Set<String> includeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeColumns()),
			Strings.trimToEmpty(bindingKeyModel.includeColumns()));
		includeColumnsKey.remove("");
		Set<String> excludeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.trimToEmpty(bindingKeyModel.whereExcludeColumns()),
			Strings.trimToEmpty(bindingKeyModel.excludeColumns()));
		excludeColumnsKey.remove("");
		Set<String> includeEmptyColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeEmptyColumns()),
			Strings.trimToEmpty(bindingKeyModel.includeEmptyColumns()));
		includeEmptyColumnsKey.remove("");
		Set<String> includeAllEmptyKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeAllEmpty()),
			Strings.trimToEmpty(bindingKeyModel.includeAllEmpty()));
		includeEmptyColumnsKey.remove("");

		return ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey.toArray(new String[0]),
			null, excludeColumnsKey.toArray(new String[0]),
			null, includeEmptyColumnsKey.toArray(new String[0]),
			false, includeAllEmptyKey.toArray(new String[0]));
	}

	/**
	 * 生成空值列的处理开关控制器，绑定健同时支持 entity 与 where 关联键，适用于不需要区分 entity 与 where 类型的场景，如 select、delete
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toFusionColumnPredicate(Map<String, Object> bindings, io.polaris.core.jdbc.annotation.segment.ColumnPredicate columnedPredicate) {
		BindingKeyModel bindingKeyModel = this;
		// 兼容 where keys
		Set<String> includeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equalsAny(columnedPredicate.includeColumnsKey(), BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeColumns()),
			Strings.trimToEmpty(bindingKeyModel.includeColumns()));
		includeColumnsKey.remove("");
		Set<String> excludeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equalsAny(columnedPredicate.excludeColumnsKey(), BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.excludeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereExcludeColumns()),
			Strings.trimToEmpty(bindingKeyModel.excludeColumns()));
		excludeColumnsKey.remove("");
		Set<String> includeEmptyColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equalsAny(columnedPredicate.includeEmptyColumnsKey(), BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeEmptyColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeEmptyColumns()),
			Strings.trimToEmpty(bindingKeyModel.includeEmptyColumns()));
		includeEmptyColumnsKey.remove("");
		Set<String> includeAllEmptyKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equalsAny(columnedPredicate.includeAllEmptyKey(), BindingKeys.WHERE_INCLUDE_ALL_EMPTY, BindingKeys.INCLUDE_ALL_EMPTY) ? "" : Strings.trimToEmpty(columnedPredicate.includeAllEmptyKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeAllEmpty()),
			Strings.trimToEmpty(bindingKeyModel.includeAllEmpty()));
		includeAllEmptyKey.remove("");

		return ConfigurableColumnPredicate.of(bindings,
			columnedPredicate.includeColumns(),
			includeColumnsKey.toArray(new String[0]),
			columnedPredicate.excludeColumns(),
			excludeColumnsKey.toArray(new String[0]),
			columnedPredicate.includeEmptyColumns(),
			includeEmptyColumnsKey.toArray(new String[0]),
			columnedPredicate.includeAllEmpty(),
			includeAllEmptyKey.toArray(new String[0]));
	}

	/**
	 * 生成空值列的处理开关控制器，适用于 update、insert 子句
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toEntityColumnPredicate(Map<String, Object> bindings) {
		BindingKeyModel bindingKeyModel = this;
		String includeColumnsKey = bindingKeyModel.includeColumns();
		String excludeColumnsKey = bindingKeyModel.excludeColumns();
		String includeEmptyColumnsKey = bindingKeyModel.includeEmptyColumns();
		String includeAllEmptyKey = bindingKeyModel.includeAllEmpty();
		return ConfigurableColumnPredicate.of(bindings, null, includeColumnsKey, null, excludeColumnsKey, null, includeEmptyColumnsKey, false, includeAllEmptyKey);
	}

	/**
	 * 生成空值列的处理开关控制器，适用于 update、insert 子句
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toEntityColumnPredicate(Map<String, Object> bindings, io.polaris.core.jdbc.annotation.segment.ColumnPredicate columnedPredicate) {
		BindingKeyModel bindingKeyModel = this;

		Set<String> includeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeColumnsKey(), BindingKeys.INCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.includeColumns()));
		includeColumnsKey.remove("");
		Set<String> excludeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.excludeColumnsKey(), BindingKeys.EXCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.excludeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.excludeColumns()));
		excludeColumnsKey.remove("");
		Set<String> includeEmptyColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeEmptyColumnsKey(), BindingKeys.INCLUDE_EMPTY_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeEmptyColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.includeEmptyColumns()));
		includeEmptyColumnsKey.remove("");
		Set<String> includeAllEmptyKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeAllEmptyKey(), BindingKeys.INCLUDE_ALL_EMPTY) ? "" : Strings.trimToEmpty(columnedPredicate.includeAllEmptyKey()),
			Strings.trimToEmpty(bindingKeyModel.includeAllEmpty()));
		includeAllEmptyKey.remove("");

		return ConfigurableColumnPredicate.of(bindings,
			columnedPredicate.includeColumns(),
			includeColumnsKey.toArray(new String[0]),
			columnedPredicate.excludeColumns(),
			excludeColumnsKey.toArray(new String[0]),
			columnedPredicate.includeEmptyColumns(),
			includeEmptyColumnsKey.toArray(new String[0]),
			columnedPredicate.includeAllEmpty(),
			includeAllEmptyKey.toArray(new String[0]));
	}


	/**
	 * 生成 where 列处理开关控制器，适用于 where 语句
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toWhereColumnPredicate(Map<String, Object> bindings) {
		BindingKeyModel bindingKeyModel = this;
		String whereIncludeColumnsKey = bindingKeyModel.whereIncludeColumns();
		String whereExcludeColumnsKey = bindingKeyModel.whereExcludeColumns();
		String whereIncludeEmptyColumnsKey = bindingKeyModel.whereIncludeEmptyColumns();
		String whereIncludeAllEmptyKey = bindingKeyModel.whereIncludeAllEmpty();
		return ConfigurableColumnPredicate.of(bindings, null, whereIncludeColumnsKey, null, whereExcludeColumnsKey, null, whereIncludeEmptyColumnsKey, false, whereIncludeAllEmptyKey);
	}

	/**
	 * 生成 where 列处理开关控制器，适用于 where 语句
	 *
	 * @param bindings 绑定参数
	 * @return 空值列处理控制器
	 */
	public ColumnPredicate toWhereColumnPredicate(Map<String, Object> bindings, io.polaris.core.jdbc.annotation.segment.ColumnPredicate columnedPredicate) {
		BindingKeyModel bindingKeyModel = this;
		Set<String> includeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeColumnsKey(), BindingKeys.WHERE_INCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeColumns()));
		includeColumnsKey.remove("");
		Set<String> excludeColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.excludeColumnsKey(), BindingKeys.WHERE_EXCLUDE_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.excludeColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereExcludeColumns()));
		excludeColumnsKey.remove("");
		Set<String> includeEmptyColumnsKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeEmptyColumnsKey(), BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) ? "" : Strings.trimToEmpty(columnedPredicate.includeEmptyColumnsKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeEmptyColumns()));
		includeEmptyColumnsKey.remove("");
		Set<String> includeAllEmptyKey = Sets.asSet(() -> new LinkedHashSet<>(),
			Strings.equals(columnedPredicate.includeAllEmptyKey(), BindingKeys.WHERE_INCLUDE_ALL_EMPTY) ? "" : Strings.trimToEmpty(columnedPredicate.includeAllEmptyKey()),
			Strings.trimToEmpty(bindingKeyModel.whereIncludeAllEmpty()));
		includeAllEmptyKey.remove("");

		return ConfigurableColumnPredicate.of(bindings,
			columnedPredicate.includeColumns(),
			includeColumnsKey.toArray(new String[0]),
			columnedPredicate.excludeColumns(),
			excludeColumnsKey.toArray(new String[0]),
			columnedPredicate.includeEmptyColumns(),
			includeEmptyColumnsKey.toArray(new String[0]),
			columnedPredicate.includeAllEmpty(),
			includeAllEmptyKey.toArray(new String[0]));
	}


	public boolean isFrozen() {
		return frozen;
	}

	public BindingKeyModel freeze() {
		this.frozen = true;
		return this;
	}

	protected void checkState() {
		if (frozen) {
			throw new IllegalStateException();
		}
	}

	// region setter

	public BindingKeyModel entity(String entity) {
		checkState();
		if (Strings.isBlank(entity)) {
			return this;
		}
		this.entity = entity;
		return this;
	}

	public BindingKeyModel where(String where) {
		checkState();
		if (Strings.isBlank(where)) {
			return this;
		}
		this.where = where;
		return this;
	}

	public BindingKeyModel orderBy(String orderBy) {
		checkState();
		if (Strings.isBlank(orderBy)) {
			return this;
		}
		this.orderBy = orderBy;
		return this;
	}


	public BindingKeyModel includeEmptyColumns(String includeEmptyColumns) {
		checkState();
		if (Strings.isBlank(includeEmptyColumns)) {
			return this;
		}
		this.includeEmptyColumns = includeEmptyColumns;
		return this;
	}

	public BindingKeyModel includeAllEmpty(String includeAllEmpty) {
		checkState();
		if (Strings.isBlank(includeAllEmpty)) {
			return this;
		}
		this.includeAllEmpty = includeAllEmpty;
		return this;
	}

	public BindingKeyModel includeColumns(String includeColumns) {
		checkState();
		if (Strings.isBlank(includeColumns)) {
			return this;
		}
		this.includeColumns = includeColumns;
		return this;
	}

	public BindingKeyModel excludeColumns(String excludeColumns) {
		checkState();
		if (Strings.isBlank(excludeColumns)) {
			return this;
		}
		this.excludeColumns = excludeColumns;
		return this;
	}

	public BindingKeyModel sql(String sql) {
		checkState();
		if (Strings.isBlank(sql)) {
			return this;
		}
		this.sql = sql;
		return this;
	}


	public BindingKeyModel whereIncludeEmptyColumns(String whereIncludeEmptyColumns) {
		checkState();
		if (Strings.isBlank(whereIncludeEmptyColumns)) {
			return this;
		}
		this.whereIncludeEmptyColumns = whereIncludeEmptyColumns;
		return this;
	}

	public BindingKeyModel whereIncludeAllEmpty(String whereIncludeAllEmpty) {
		checkState();
		if (Strings.isBlank(whereIncludeAllEmpty)) {
			return this;
		}
		this.whereIncludeAllEmpty = whereIncludeAllEmpty;
		return this;
	}

	public BindingKeyModel whereIncludeColumns(String whereIncludeColumns) {
		checkState();
		if (Strings.isBlank(whereIncludeColumns)) {
			return this;
		}
		this.whereIncludeColumns = whereIncludeColumns;
		return this;
	}

	public BindingKeyModel whereExcludeColumns(String whereExcludeColumns) {
		checkState();
		if (Strings.isBlank(whereExcludeColumns)) {
			return this;
		}
		this.whereExcludeColumns = whereExcludeColumns;
		return this;
	}

	public BindingKeyModel withLogicDeleted(String withLogicDeleted) {
		checkState();
		if (Strings.isBlank(withLogicDeleted)) {
			return this;
		}
		this.withLogicDeleted = withLogicDeleted;
		return this;
	}

	public BindingKeyModel existsByCount(String existsByCount) {
		checkState();
		if (Strings.isBlank(existsByCount)) {
			return this;
		}
		this.existsByCount = existsByCount;
		return this;
	}

	// endregion

	// region getter

	public String entity() {
		return entity;
	}

	public String entity(EntityInsert entityInsert) {
		String entityKey = entityInsert.entityKey();
		if (BindingKeys.ENTITY.equals(entityKey) || Strings.isBlank(entityKey)) {
			entityKey = entity;
		}
		return entityKey;
	}

	public String entity(EntityDelete entityDelete) {
		String entityKey = entityDelete.entityKey();
		if (BindingKeys.ENTITY.equals(entityKey) || Strings.isBlank(entityKey)) {
			entityKey = entity;
		}
		return entityKey;
	}

	public String entity(EntityUpdate entityUpdate) {
		String entityKey = entityUpdate.entityKey();
		if (BindingKeys.ENTITY.equals(entityKey) || Strings.isBlank(entityKey)) {
			entityKey = entity;
		}
		return entityKey;
	}

	public String entity(EntitySelect entitySelect) {
		String entityKey = entitySelect.entityKey();
		if (BindingKeys.ENTITY.equals(entityKey) || Strings.isBlank(entityKey)) {
			entityKey = entity;
		}
		return entityKey;
	}
	public String entity(EntityMerge entityMerge) {
		String entityKey = entityMerge.entityKey();
		if (BindingKeys.ENTITY.equals(entityKey) || Strings.isBlank(entityKey)) {
			entityKey = entity;
		}
		return entityKey;
	}

	public String where() {
		return where;
	}

	public String where(EntityDelete entityDelete) {
		String whereKey = entityDelete.whereKey();
		if (BindingKeys.WHERE.equals(whereKey) || Strings.isBlank(whereKey)) {
			whereKey = where;
		}
		return whereKey;
	}

	public String where(EntityUpdate entityUpdate) {
		String whereKey = entityUpdate.whereKey();
		if (BindingKeys.WHERE.equals(whereKey) || Strings.isBlank(whereKey)) {
			whereKey = where;
		}
		return whereKey;
	}

	public String where(EntitySelect entitySelect) {
		String whereKey = entitySelect.whereKey();
		if (BindingKeys.WHERE.equals(whereKey) || Strings.isBlank(whereKey)) {
			whereKey = where;
		}
		return whereKey;
	}

	public String orderBy() {
		return orderBy;
	}

	public String orderBy(EntitySelect entitySelect) {
		String orderByKey = entitySelect.orderByKey();
		if (BindingKeys.ORDER_BY.equals(orderByKey) || Strings.isBlank(orderByKey)) {
			orderByKey = orderBy;
		}
		return orderByKey;
	}

	public String includeEmptyColumns() {
		return includeEmptyColumns;
	}

	public String includeAllEmpty() {
		return includeAllEmpty;
	}

	public String includeColumns() {
		return includeColumns;
	}

	public String excludeColumns() {
		return excludeColumns;
	}

	public String sql() {
		return sql;
	}

	public String whereIncludeEmptyColumns() {
		return whereIncludeEmptyColumns;
	}

	public String whereIncludeAllEmpty() {
		return whereIncludeAllEmpty;
	}

	public String whereIncludeColumns() {
		return whereIncludeColumns;
	}

	public String whereExcludeColumns() {
		return whereExcludeColumns;
	}

	public String withLogicDeleted() {
		return withLogicDeleted;
	}

	public String existsByCount() {
		return existsByCount;
	}

	// endregion

}
