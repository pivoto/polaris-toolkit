package io.polaris.core.jdbc.sql;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import io.polaris.core.converter.Converters;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.ExpressionMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.VarNameGenerator;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.query.ValueRange;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.ConfigurableColumnPredicate;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.string.Strings;

/**
 * 用于Mybatis扩展生成Sql
 *
 * @author Qt
 * @since Jan 27, 2024
 */
@SuppressWarnings("ALL")
public class SqlStatements {
	private static Logger log = Loggers.of(SqlStatements.class);

	private static final String KEY_WHERE_PREFIX = "_w";
	private static final String KEY_VALUE_PREFIX = "_v";

	private static VarNameGenerator newWhereVarNameGenerator() {
		return VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
	}

	private static VarNameGenerator newValueVarNameGenerator() {
		return VarNameGenerator.newInstance(KEY_VALUE_PREFIX);
	}

	/**
	 * 构建根据任意条件进行逻辑删除的SQL语句。
	 *
	 * @param bindings    参数绑定映射，用于存储SQL中的参数占位符与实际值的对应关系
	 * @param entityClass 实体类类型，表示要操作的数据表对应的实体类
	 * @return 返回构建好的逻辑删除SQL字符串
	 */
	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildInsert(bindings, entityClass, entityKey, columnPredicate);
	}

	/**
	 * 构建插入语句（INSERT）的 SQL 字符串。
	 *
	 * @param bindings        参数绑定映射表，用于存放 SQL 中占位符对应的值
	 * @param entityClass     实体类类型，用于获取表结构元数据
	 * @param entityKey       实体对象在 bindings 中的键名
	 * @param columnPredicate 列过滤条件接口，决定哪些列参与插入操作
	 * @return 返回构建好的 INSERT SQL 语句字符串
	 */
	public static String buildInsert(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, ColumnPredicate columnPredicate) {
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.insert(tableMeta.getTable());

		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}
			Object val1 = entityMap.get(meta.getFieldName());
			Object val = BindingValues.getValueForInsert(meta, val1);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}

			if (meta.isVersion()) {
				val = val == null ? 1L : ((Number) val).longValue();
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				if (Strings.isNotBlank(propertiesString)) {
					sql.columnAndValue(columnName, "#{" + keyName + "," + propertiesString + "}");
				} else {
					sql.columnAndValue(columnName, "#{" + keyName + "}");
				}
				bindings.put(keyName, val);
			} else {
				if (meta.isPrimaryKey()) {
					if (Strings.isNotBlank(meta.getIdSql())) {
						// 存在自定义SQL
						sql.columnAndValue(columnName, meta.getIdSql());
						continue;
					} else if (Strings.isNotBlank(meta.getSeqName())) {
						// 存在序列，使用序列值
						sql.columnAndValue(columnName, meta.getSeqName() + ".NEXTVAL");
						continue;
					} else if (meta.isAutoIncrement()) {
						// 自增主键，不需要赋值
						continue;
					}
				}
				// 存在默认值SQL
				if (Strings.isNotBlank(meta.getInsertDefaultSql())) {
					// 存在自定义默认值SQL
					sql.columnAndValue(columnName, meta.getInsertDefaultSql());
					continue;
				}
				// 其他情况，判断是否需要包含空值字段
				if (columnPredicate.isIncludedEmptyColumn(name)) {
					sql.columnAndValue(columnName, "NULL");
				}
			}
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据ID删除实体的SQL语句
	 *
	 * @param bindings         参数绑定映射，用于存储SQL中的参数占位符和实际值的对应关系
	 * @param entityClass      实体类的Class对象，用于获取表名和字段信息
	 * @param withLogicDeleted 是否使用逻辑删除，true表示使用逻辑删除，false表示物理删除
	 * @return 返回构建好的删除SQL语句字符串
	 */
	public static String buildDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		boolean withLogicDeleted) {
		return buildDeleteById(bindings, entityClass, withLogicDeleted, BindingKeys.ENTITY, BindingKeys.WHERE);
	}

	/**
	 * 构建根据主键逻辑删除或物理删除的SQL语句。
	 *
	 * @param bindings         参数绑定映射，用于存放SQL中需要的参数值
	 * @param entityClass      实体类类型，表示要操作的表对应的实体类
	 * @param withLogicDeleted 是否启用逻辑删除，true表示使用逻辑删除，false表示直接物理删除
	 * @param entityKey        实体对象在bindings中的键名
	 * @param whereKey         查询条件在bindings中的键名（备用）
	 * @return 返回构建好的SQL字符串
	 */
	public static String buildDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		boolean withLogicDeleted, String entityKey, String whereKey) {
		if (!withLogicDeleted) {
			return buildDirectDeleteById(bindings, entityClass, entityKey, whereKey);
		}

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		if (!tableMeta.getColumns().values().stream().anyMatch(c -> c.isLogicDeleted())) {
			// 不存在逻辑删除字段
			return buildDirectDeleteById(bindings, entityClass, entityKey, whereKey);
		}

		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			boolean logicDeleted = meta.isLogicDeleted();
			String propertiesString = meta.getPropertiesString();
			if (primaryKey || version) {
				Object val = entityMap.get(name);
				if (val instanceof VarRef) {
					// 优先使用VarRef携带的参数属性
					propertiesString = ((VarRef<?>) val).getProps();
					val = ((VarRef<?>) val).getValue();
				}

				if (Objs.isEmpty(val)) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				}
			}
			Object val = null;
			// 只更新逻辑删除、更新时间、版本等字段
			if (logicDeleted) {
				val = Converters.convertQuietly(meta.getFieldType(), true);
			} else if (meta.isUpdateTime()) {
				val = entityMap.get(name);
				if (val instanceof VarRef) {
					// 优先使用VarRef携带的参数属性
					propertiesString = ((VarRef<?>) val).getProps();
					val = ((VarRef<?>) val).getValue();
				}
				if (val == null) {
					val = Converters.convertQuietly(meta.getFieldType(), new Date());
				}
			} else if (version) {
				val = entityMap.get(name);
				if (val instanceof VarRef) {
					// 优先使用VarRef携带的参数属性
					propertiesString = ((VarRef<?>) val).getProps();
					val = ((VarRef<?>) val).getValue();
				}
				val = val == null ? 1L : ((Number) val).longValue() + 1;
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				if (Strings.isNotBlank(propertiesString)) {
					sql.set(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.set(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, val);
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据ID直接删除记录的SQL语句
	 *
	 * @param bindings    绑定参数映射，用于存储SQL构建过程中的各种绑定信息
	 * @param entityClass 实体类，表示要操作的数据库表对应的Java实体类
	 * @return 返回构建好的删除SQL语句字符串
	 */
	public static String buildDirectDeleteById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		return buildDirectDeleteById(bindings, entityClass, entityKey, whereKey);
	}

	/**
	 * 构建根据ID直接删除记录的SQL语句
	 *
	 * @param bindings    绑定参数映射，包含实体数据或条件数据
	 * @param entityClass 实体类类型，用于获取表元数据信息
	 * @param entityKey   实体键名，用于从bindings中获取实体数据
	 * @param whereKey    条件键名，当entityKey对应的数据不存在时，从bindings中获取条件数据
	 * @return 构建好的删除SQL语句字符串
	 */
	public static String buildDirectDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();

			Object val = entityMap.get(name);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			// 按主键条件删除，无需判断列的包含条件
			if (primaryKey || version) {
				if (Objs.isNotEmpty(val)) {
					String key = whereKeyGen.generate();
					appendSqlWhereWithVal(bindings, sql, columnName, meta.getFieldType(), val, key, propertiesString);
				} else {
					sql.where(columnName + " IS NULL");
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据ID逻辑删除的SQL语句
	 *
	 * @param bindings    参数绑定映射，用于存储SQL参数值
	 * @param entityClass 实体类的Class对象，表示要操作的实体类型
	 * @return 返回构建好的逻辑删除SQL语句
	 */
	public static String buildLogicDeleteById(Map<String, Object> bindings, Class<?> entityClass) {
		return buildLogicDeleteById(bindings, entityClass, BindingKeys.ENTITY, BindingKeys.WHERE);
	}

	/**
	 * 构建逻辑删除SQL语句
	 *
	 * @param bindings    参数绑定映射表，用于存储SQL参数值
	 * @param entityClass 实体类的Class对象，表示要操作的数据库表对应的实体类
	 * @param entityKey   实体键名，用于在bindings中存储实体对象的键
	 * @param whereKey    条件键名，用于在bindings中存储WHERE条件参数的键
	 * @return 返回构建好的逻辑删除SQL语句
	 */
	public static String buildLogicDeleteById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {
		return buildDeleteById(bindings, entityClass, true, entityKey, whereKey);
	}

	/**
	 * 构建根据任意条件删除记录的SQL语句
	 *
	 * @param bindings         参数绑定映射，包含构建SQL所需的各种参数
	 * @param entityClass      实体类，用于获取表结构信息
	 * @param withLogicDeleted 是否使用逻辑删除，true表示使用逻辑删除，false表示物理删除
	 * @return 构建好的删除SQL语句
	 */
	public static String buildDeleteByAny(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildDeleteByAny(bindings, entityClass, withLogicDeleted, entityKey, whereKey, columnPredicate);
	}

	/**
	 * 构建根据任意条件进行（逻辑）删除的 SQL 语句。
	 * <p>
	 * 如果 {@code withLogicDeleted} 为 false 或实体类没有逻辑删除字段，则执行物理删除；
	 * 否则构建更新语句将逻辑删除字段标记为已删除，并可能更新版本号和更新时间字段。
	 *
	 * @param bindings             参数绑定映射表，用于存放 SQL 中的参数占位符与实际值的对应关系
	 * @param entityClass          实体类类型，表示要操作的数据表对应的 Java 类
	 * @param withLogicDeleted     是否启用逻辑删除模式
	 * @param entityKey            实体对象在 bindings 中的键名
	 * @param whereKey             查询条件对象在 bindings 中的键名
	 * @param whereColumnPredicate 查询条件列的谓词判断逻辑，决定哪些字段参与 where 条件构造
	 * @return 返回构建好的 SQL 字符串
	 */
	public static String buildDeleteByAny(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted,
		String entityKey, String whereKey, ColumnPredicate whereColumnPredicate) {
		if (!withLogicDeleted) {
			return buildDirectDeleteByAny(bindings, entityClass, entityKey, whereKey, whereColumnPredicate);
		}

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		if (!tableMeta.getColumns().values().stream().anyMatch(c -> c.isLogicDeleted())) {
			// 不存在逻辑删除字段
			return buildDirectDeleteByAny(bindings, entityClass, entityKey, whereKey, whereColumnPredicate);
		}

		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
		Object where = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			boolean logicDeleted = meta.isLogicDeleted();

			Object val = null;
			// 只更新逻辑删除、更新时间、版本等字段
			if (logicDeleted) {
				val = Converters.convertQuietly(meta.getFieldType(), true);
			} else if (meta.isUpdateTime()) {
				val = entityMap.get(name);
				if (val == null) {
					val = Converters.convertQuietly(meta.getFieldType(), new Date());
				}
			} else if (version) {
				val = entityMap.get(name);
				val = val == null ? 1L : ((Number) val).longValue() + 1;
			}
			if (Objs.isNotEmpty(val)) {
				String keyName = valueKeyGen.generate();
				String propertiesString = meta.getPropertiesString();
				if (Strings.isNotBlank(propertiesString)) {
					sql.set(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.set(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, val);
			}
		}
		// where 条件
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, where, whereColumnPredicate);
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建直接删除语句，根据任意条件删除实体记录
	 *
	 * @param bindings    绑定参数映射，包含实体对象和查询条件
	 * @param entityClass 实体类的Class对象，用于获取表结构信息
	 * @return 构建完成的删除SQL语句
	 */
	public static String buildDirectDeleteByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildDirectDeleteByAny(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	/**
	 * 构建直接删除SQL语句，支持通过实体对象或条件对象指定删除条件
	 *
	 * @param bindings        包含实体对象和条件对象的绑定参数映射
	 * @param entityClass     实体类类型
	 * @param entityKey       实体对象在bindings中的键名
	 * @param whereKey        条件对象在bindings中的键名
	 * @param columnPredicate 列谓词条件
	 * @return 构建完成的删除SQL语句字符串
	 */
	public static String buildDirectDeleteByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, entity);
			} else {
				appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate);
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建逻辑删除SQL语句，支持通过任意条件进行删除
	 *
	 * @param bindings    参数绑定映射，包含构建SQL所需的各种参数
	 * @param entityClass 实体类对象，用于获取表结构信息
	 * @return 构建完成的逻辑删除SQL语句
	 */
	public static String buildLogicDeleteByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};
		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildLogicDeleteByAny(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	/**
	 * 构建逻辑删除SQL语句
	 *
	 * @param bindings             参数绑定映射表，用于存储SQL参数值
	 * @param entityClass          实体类的Class对象，表示要操作的数据库表对应的实体类
	 * @param entityKey            实体键名，用于标识实体对象在bindings中的键值
	 * @param whereKey             条件键名，用于标识WHERE条件在bindings中的键值
	 * @param whereColumnPredicate WHERE条件列的谓词对象，定义了删除条件的列信息和操作符
	 * @return 返回构建好的逻辑删除SQL语句字符串
	 */
	public static String buildLogicDeleteByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate whereColumnPredicate) {
		return buildDeleteByAny(bindings, entityClass, true, entityKey, whereKey, whereColumnPredicate);
	}

	/**
	 * 构建根据ID更新实体的SQL语句
	 *
	 * @param bindings    包含构建SQL所需参数的绑定映射，如实体对象、WHERE条件等
	 * @param entityClass 实体类的Class对象，用于获取表结构和列信息
	 * @return 返回构建好的根据ID更新的SQL语句
	 */
	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildUpdateById(bindings, entityClass, entityKey, whereKey, columnPredicate);
	}

	/**
	 * 构建根据ID更新记录的SQL语句。
	 *
	 * @param bindings        参数绑定映射表，用于存放SQL中的参数占位符与实际值的对应关系
	 * @param entityClass     实体类类型，表示数据库表对应的Java实体类
	 * @param entityKey       实体对象在bindings中的键名，用于获取待更新的数据对象
	 * @param whereKey        查询条件对象在bindings中的键名，当entity为空时使用该键获取查询条件数据
	 * @param columnPredicate 列过滤谓词接口，决定哪些列可以被包含在更新操作中
	 * @return 返回构建好的UPDATE SQL语句字符串
	 */
	public static String buildUpdateById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);
			boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			if (primaryKey || version) {
				if (Objs.isEmpty(val)) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				}
			}
			if (!updatable) {
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			Object entityVal = BindingValues.getValueForUpdate(meta, val);
			if (version) {
				entityVal = entityVal == null ? 1L : ((Number) entityVal).longValue() + 1;
			} else if (primaryKey) {
				// 不更新主键值
				continue;
			}
			if (Objs.isNotEmpty(entityVal)) {
				String keyName = valueKeyGen.generate();
				if (Strings.isNotBlank(propertiesString)) {
					sql.set(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.set(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, entityVal);
			} else {
				// 默认值SQL存在则使用
				String updateDefaultSql = meta.getUpdateDefaultSql();
				if (Strings.isNotBlank(updateDefaultSql)) {
					sql.set(columnName + " = " + updateDefaultSql);
					continue;
				}

				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据任意条件更新实体的SQL语句
	 *
	 * @param bindings    包含构建参数的映射表，用于指定更新条件和列过滤规则
	 * @param entityClass 实体类的Class对象，表示要更新的实体类型
	 * @return 返回构建好的UPDATE SQL语句字符串
	 */
	public static String buildUpdateByAny(Map<String, Object> bindings, Class<?> entityClass) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String includeColumnsKey = BindingKeys.INCLUDE_COLUMNS;
		String excludeColumnsKey = BindingKeys.EXCLUDE_COLUMNS;
		String includeEmptyColumnsKey = BindingKeys.INCLUDE_EMPTY_COLUMNS;
		String includeAllEmptyKey = BindingKeys.INCLUDE_EMPTY;
		String whereIncludeColumnsKey = BindingKeys.WHERE_INCLUDE_COLUMNS;
		String whereExcludeColumnsKey = BindingKeys.WHERE_EXCLUDE_COLUMNS;
		String whereIncludeEmptyColumnsKey = BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS;
		String whereIncludeAllEmptyKey = BindingKeys.WHERE_INCLUDE_EMPTY;

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		ColumnPredicate whereColumnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, whereIncludeColumnsKey, null, whereExcludeColumnsKey,
			null, whereIncludeEmptyColumnsKey, false, whereIncludeAllEmptyKey);

		return buildUpdateByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, whereColumnPredicate);
	}

	/**
	 * 构建根据任意条件更新实体的 SQL 语句。
	 *
	 * @param bindings             参数绑定映射，用于存放 SQL 中的参数值
	 * @param entityClass          实体类类型，用于获取表结构元数据
	 * @param entityKey            实体对象在 bindings 中的键名
	 * @param whereKey             更新条件对象在 bindings 中的键名
	 * @param columnPredicate      列过滤谓词，决定哪些列可以被更新
	 * @param whereColumnPredicate WHERE 条件中使用的列过滤谓词
	 * @return 返回构建好的 UPDATE SQL 字符串
	 */
	public static String buildUpdateByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, ColumnPredicate whereColumnPredicate) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		VarNameGenerator valueKeyGen = newValueVarNameGenerator();
		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, Collections.emptyMap());
		Object where = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();

			boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
			if (!updatable) {
				continue;
			}
			if (primaryKey) {
				// 不更新主键值
				continue;
			}
			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			String propertiesString = meta.getPropertiesString();
			Object val = entityMap.get(meta.getFieldName());
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			Object entityVal = BindingValues.getValueForUpdate(meta, val);
			if (version) {
				entityVal = entityVal == null ? 1L : ((Number) entityVal).longValue() + 1;
			}
			if (Objs.isNotEmpty(entityVal)) {
				String keyName = valueKeyGen.generate();
				if (Strings.isNotBlank(propertiesString)) {
					sql.set(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.set(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, entityVal);
			} else {
				// 默认值SQL存在则使用
				String updateDefaultSql = meta.getUpdateDefaultSql();
				if (Strings.isNotBlank(updateDefaultSql)) {
					sql.set(columnName + " = " + updateDefaultSql);
					continue;
				}

				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.set(columnName + " = NULL");
				}
			}
		}
		// where 条件
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(bindings, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(bindings, entityClass, tableMeta, sql, whereKeyGen, where, whereColumnPredicate);
		}

		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建COUNT查询语句
	 *
	 * @param bindings    参数绑定映射，用于存储查询参数的键值对
	 * @param entityClass 实体类，指定要查询的实体类型
	 * @return 返回构建好的COUNT查询语句字符串
	 */
	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass) {
		return buildCount(bindings, entityClass, false);
	}

	/**
	 * 构建COUNT查询语句
	 *
	 * @param bindings         绑定参数映射，包含查询条件和配置信息
	 * @param entityClass      实体类，用于确定查询的表和字段信息
	 * @param withLogicDeleted 是否包含逻辑删除的数据
	 * @return 构建好的COUNT查询语句字符串
	 */
	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey,
			null, excludeColumnsKey,
			null, includeEmptyColumnsKey,
			false, includeAllEmptyKey);
		return buildCount(bindings, entityClass, entityKey, whereKey, columnPredicate, withLogicDeleted);
	}

	/**
	 * 构建COUNT查询语句
	 *
	 * @param bindings        参数绑定映射表，用于存储查询参数
	 * @param entityClass     实体类对象，表示要查询的实体类型
	 * @param entityKey       实体键名，用于标识实体在bindings中的键值
	 * @param whereKey        WHERE条件键名，用于标识WHERE条件在bindings中的键值
	 * @param columnPredicate 列谓词对象，用于构建查询条件
	 * @return 返回构建好的COUNT查询语句字符串
	 */
	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate) {
		return buildCount(bindings, entityClass, entityKey, whereKey, columnPredicate, false);
	}

	/**
	 * 构建用于统计记录数量的 SQL COUNT 查询语句。
	 *
	 * @param bindings         参数绑定映射，用于存放 SQL 中的参数值
	 * @param entityClass      实体类，用于获取表结构元数据
	 * @param entityKey        实体对象在 bindings 中的键名，用于构建 WHERE 条件
	 * @param whereKey         条件对象在 bindings 中的键名，用于构建额外的 WHERE 条件
	 * @param columnPredicate  列过滤条件，用于控制哪些字段参与查询条件构建
	 * @param withLogicDeleted 是否包含逻辑删除的数据，若为 true 则自动添加非逻辑删除条件
	 * @return 生成的 COUNT 查询 SQL 语句字符串
	 */
	public static String buildCount(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, boolean withLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*)");

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		Set<String> whereColumns = new HashSet<>();
		Consumer<String> whereColumnVisitor = (columnName) -> whereColumns.add(columnName);

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}

		if (withLogicDeleted) {
			// 强制添加非逻辑删除条件
			tableMeta.getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					String columnName = meta.getColumnName();
					if (whereColumns.contains(columnName)) {
						// 如已在where条件中，则忽略。本方法主要用于单表操作，不考虑存在表名别的字段的情况
						return;
					}
					String propertiesString = meta.getPropertiesString();
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				});
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据ID判断实体是否存在的查询语句
	 *
	 * @param bindings    参数绑定映射，用于存储查询参数
	 * @param entityClass 实体类对象，指定要查询的实体类型
	 * @return 返回构建好的存在性检查查询语句字符串
	 */
	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass) {
		return buildExistsById(bindings, entityClass, false);
	}

	/**
	 * 构建根据ID判断实体是否存在的SQL查询语句
	 *
	 * @param bindings         参数绑定映射表，用于存储SQL参数绑定信息
	 * @param entityClass      实体类的Class对象，表示要查询的实体类型
	 * @param withLogicDeleted 是否包含逻辑删除的数据，true表示包含已逻辑删除的数据，false表示不包含
	 * @return 返回构建好的判断实体是否存在的SQL查询语句
	 */
	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		return buildExistsById(bindings, entityClass, entityKey, whereKey, withLogicDeleted);
	}

	/**
	 * 构建根据ID判断记录是否存在的SQL查询语句
	 *
	 * @param bindings    参数绑定映射表，用于存储SQL参数值
	 * @param entityClass 实体类对象，表示要操作的数据库表对应的Java类
	 * @param entityKey   实体键名，用于指定实体在bindings中的键值
	 * @param whereKey    WHERE条件键名，用于指定WHERE子句中使用的参数键值
	 * @return 返回构建好的SQL查询语句字符串
	 */
	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey) {
		return buildExistsById(bindings, entityClass, entityKey, whereKey, false);
	}

	/**
	 * 构建根据主键判断记录是否存在的SQL语句。
	 *
	 * @param bindings         参数绑定映射，用于存放SQL中的参数值
	 * @param entityClass      实体类类型，用于获取表结构信息
	 * @param entityKey        实体对象在bindings中的键名
	 * @param whereKey         查询条件在bindings中的键名（备用）
	 * @param withLogicDeleted 是否考虑逻辑删除字段，若为true则强制加入未删除条件
	 * @return 返回构建好的查询SQL字符串，格式如：SELECT COUNT(*) EXISTED FROM table WHERE ...
	 */
	public static String buildExistsById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, boolean withLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*) EXISTED");

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			if (primaryKey) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				}
			} else if (withLogicDeleted && meta.isLogicDeleted()) {
				// 强制添加非逻辑删除条件
				String keyName = whereKeyGen.generate();
				val = Converters.convertQuietly(meta.getFieldType(), false);

				if (Strings.isNotBlank(propertiesString)) {
					sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.where(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, val);
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建基于任意条件的存在性查询语句
	 *
	 * @param bindings     参数绑定映射，用于存储查询参数
	 * @param entityClass  实体类对象，指定要查询的实体类型
	 * @param queryByCount 是否通过计数方式进行查询
	 * @return 返回构建好的存在性查询语句字符串
	 */
	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass, boolean queryByCount) {
		return buildExistsByAny(bindings, entityClass, queryByCount, false);
	}

	/**
	 * 构建存在性查询SQL语句，支持任意条件匹配
	 *
	 * @param bindings         参数绑定映射，包含查询条件和实体信息
	 * @param entityClass      实体类类型，用于获取表结构和字段信息
	 * @param queryByCount     是否按数量查询，true时返回计数SQL，false时返回是否存在SQL
	 * @param withLogicDeleted 是否包含逻辑删除数据，true时包含已逻辑删除的数据，false时排除
	 * @return 构建好的存在性查询SQL语句
	 */
	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass, boolean queryByCount, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildExistsByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, queryByCount, withLogicDeleted);
	}

	/**
	 * 构建存在性查询SQL语句，用于检查是否存在满足条件的记录
	 *
	 * @param bindings        参数绑定映射，用于存储查询参数
	 * @param entityClass     实体类类型
	 * @param entityKey       实体键名
	 * @param whereKey        WHERE条件键名
	 * @param columnPredicate 列谓词条件
	 * @param queryByCount    是否通过COUNT查询
	 * @return 构建好的存在性查询SQL语句
	 */
	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, boolean queryByCount) {
		return buildExistsByAny(bindings, entityClass, entityKey, whereKey, columnPredicate, queryByCount, false);
	}

	/**
	 * 构建一个用于判断记录是否存在的SQL语句，支持通过实体对象或条件对象（Criteria）指定查询条件。
	 * 可以选择查询 COUNT(*) 或常量 1 来表示是否存在匹配的记录，并可控制是否排除逻辑删除的数据。
	 *
	 * @param bindings         参数绑定映射表，用于存放SQL中的参数值
	 * @param entityClass      实体类类型，用于获取表结构元数据
	 * @param entityKey        实体对象在bindings中的键名
	 * @param whereKey         查询条件对象在bindings中的键名（可以是实体或Criteria）
	 * @param columnPredicate  列过滤谓词，决定哪些列参与构建查询条件
	 * @param queryByCount     是否使用 COUNT(*) 方式进行查询
	 * @param withLogicDeleted 是否强制添加非逻辑删除条件
	 * @return 构造完成的SQL字符串
	 */
	public static String buildExistsByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, ColumnPredicate columnPredicate, boolean queryByCount, boolean withLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		if (queryByCount) {
			sql.select("COUNT(*) EXISTED");
		} else {
			sql.select("1 EXISTED");
		}

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		Set<String> whereColumns = new HashSet<>();
		Consumer<String> whereColumnVisitor = (columnName) -> whereColumns.add(columnName);

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}

		if (withLogicDeleted) {
			// 强制添加非逻辑删除条件
			tableMeta.getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					String columnName = meta.getColumnName();
					if (whereColumns.contains(columnName)) {
						// 如已在where条件中，则忽略。本方法主要用于单表操作，不考虑存在表名别的字段的情况
						return;
					}
					String propertiesString = meta.getPropertiesString();
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				});
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据ID查询的SQL语句
	 *
	 * @param bindings    参数绑定映射，用于存储SQL中的参数值
	 * @param entityClass 实体类对象，用于获取表名和字段信息
	 * @return 返回构建好的根据ID查询的SQL语句
	 */
	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass) {
		return buildSelectById(bindings, entityClass, false);
	}

	/**
	 * 构建根据ID查询的SQL语句
	 *
	 * @param bindings         参数绑定映射，用于存储SQL中的参数值
	 * @param entityClass      实体类的Class对象，用于获取表名和字段信息
	 * @param withLogicDeleted 是否包含逻辑删除的数据，true表示包含已逻辑删除的数据，false表示不包含
	 * @return 返回构建好的根据ID查询的SQL语句
	 */
	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String orderByKey = BindingKeys.ORDER_BY;
		return buildSelectById(bindings, entityClass, entityKey, whereKey, orderByKey, withLogicDeleted);
	}

	/**
	 * 构建根据ID查询的SQL语句
	 *
	 * @param bindings    参数绑定映射，用于存储SQL参数值
	 * @param entityClass 实体类对象，用于获取表名和字段信息
	 * @param entityKey   实体键名，用于指定主键字段名
	 * @param whereKey    WHERE条件键名，用于指定查询条件参数
	 * @param orderByKey  排序条件键名，用于指定排序字段
	 * @return 返回构建好的SQL查询语句
	 */
	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey) {
		return buildSelectById(bindings, entityClass, entityKey, whereKey, orderByKey, false);
	}

	/**
	 * 构建根据ID查询记录的SQL语句。
	 *
	 * @param bindings         参数绑定映射，用于存放SQL中的参数值
	 * @param entityClass      实体类类型，表示要操作的数据表对应的实体类
	 * @param entityKey        实体对象在bindings中的键名
	 * @param whereKey         查询条件在bindings中的键名（备用）
	 * @param orderByKey       排序字段在bindings中的键名（当前未使用）
	 * @param withLogicDeleted 是否包含逻辑删除字段的过滤条件
	 * @return 返回构建好的SELECT SQL语句字符串
	 */
	public static String buildSelectById(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, boolean withLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity == null) {
			entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, Collections.emptyMap());
		}
		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			//boolean version = meta.isVersion();
			Object val = entityMap.get(name);

			sql.select(columnName + " " + name);

			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			if (primaryKey) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				}
			} else if (withLogicDeleted && meta.isLogicDeleted()) {
				// 强制添加非逻辑删除条件
				String keyName = whereKeyGen.generate();
				val = Converters.convertQuietly(meta.getFieldType(), false);

				if (Strings.isNotBlank(propertiesString)) {
					sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
				} else {
					sql.where(columnName + " = #{" + keyName + "}");
				}
				bindings.put(keyName, val);
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalArgumentException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	/**
	 * 构建根据任意条件查询的SQL语句
	 *
	 * @param bindings    参数绑定映射，用于存储查询条件的键值对
	 * @param entityClass 实体类的Class对象，用于获取表名和字段信息
	 * @return 返回构建好的SELECT查询语句
	 */
	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass) {
		return buildSelectByAny(bindings, entityClass, false);
	}

	/**
	 * 构建根据任意条件查询的SQL语句
	 *
	 * @param bindings         包含查询条件的绑定参数映射
	 * @param entityClass      实体类类型
	 * @param withLogicDeleted 是否包含逻辑删除的数据
	 * @return 构建好的SELECT SQL语句
	 */
	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass, boolean withLogicDeleted) {
		String entityKey = BindingKeys.ENTITY;
		String whereKey = BindingKeys.WHERE;
		String orderByKey = BindingKeys.ORDER_BY;
		// 兼容 where keys
		String[] includeColumnsKey = {BindingKeys.WHERE_INCLUDE_COLUMNS, BindingKeys.INCLUDE_COLUMNS};
		String[] excludeColumnsKey = {BindingKeys.WHERE_EXCLUDE_COLUMNS, BindingKeys.EXCLUDE_COLUMNS};
		String[] includeEmptyColumnsKey = {BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS, BindingKeys.INCLUDE_EMPTY_COLUMNS};
		String[] includeAllEmptyKey = {BindingKeys.WHERE_INCLUDE_EMPTY, BindingKeys.INCLUDE_EMPTY};

		ColumnPredicate columnPredicate = ConfigurableColumnPredicate.of(bindings,
			null, includeColumnsKey, null, excludeColumnsKey,
			null, includeEmptyColumnsKey, false, includeAllEmptyKey);
		return buildSelectByAny(bindings, entityClass, entityKey, whereKey, orderByKey, columnPredicate, withLogicDeleted);
	}

	/**
	 * 构建根据任意条件查询的SQL语句
	 *
	 * @param bindings        参数绑定映射表，用于存储查询参数
	 * @param entityClass     实体类对象，表示要查询的数据库表对应的Java类
	 * @param entityKey       实体键名，用于标识实体在bindings中的存储键
	 * @param whereKey        WHERE条件键名，用于标识WHERE条件在bindings中的存储键
	 * @param orderByKey      排序条件键名，用于标识ORDER BY条件在bindings中的存储键
	 * @param columnPredicate 列谓词接口，用于定义列的过滤条件
	 * @return 返回构建好的SELECT SQL语句字符串
	 */
	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate) {
		return buildSelectByAny(bindings, entityClass, entityKey, whereKey, orderByKey, columnPredicate, false);
	}

	/**
	 * 构建一个 SELECT 查询语句，支持根据实体对象、条件对象（Criteria）、排序等动态生成 SQL。
	 *
	 * @param bindings         参数绑定映射，用于存放 SQL 中的参数占位符与实际值的对应关系
	 * @param entityClass      实体类类型，用于获取表结构元数据
	 * @param entityKey        实体对象在 bindings 中的键名，若存在则用作 WHERE 条件的一部分
	 * @param whereKey         条件对象（如 Criteria）在 bindings 中的键名，用于构建 WHERE 子句
	 * @param orderByKey       排序信息在 bindings 中的键名，可以是字符串或 OrderBy 对象
	 * @param columnPredicate  列过滤谓词接口，决定哪些列参与查询条件判断
	 * @param withLogicDeleted 是否强制加入逻辑删除字段的查询条件（默认为未删除状态）
	 * @return 生成的 SELECT SQL 字符串
	 */
	public static String buildSelectByAny(Map<String, Object> bindings, Class<?> entityClass,
		String entityKey, String whereKey, String orderByKey, ColumnPredicate columnPredicate,
		boolean withLogicDeleted) {

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			sql.select(columnName + " " + name);
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();
			ExpressionMeta meta = entry.getValue();
			if (meta.isSelectable()) {
				// 无别名时，使用表名，防止子查询中字段来源不明确
				String columnName = meta.getExpressionWithTableName();
				sql.select(columnName + " " + name);
			}
		}

		VarNameGenerator whereKeyGen = newWhereVarNameGenerator();

		Set<String> whereColumns = new HashSet<>();
		Consumer<String> whereColumnVisitor = (columnName) -> whereColumns.add(columnName);

		Object entity = BindingValues.getBindingValueOrDefault(bindings, entityKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}
		entity = BindingValues.getBindingValueOrDefault(bindings, whereKey, null);
		if (entity != null) {
			if (entity instanceof Criteria) {
				appendSqlWhereWithCriteriaAndVisitor(bindings, tableMeta, sql, whereKeyGen, entity, whereColumnVisitor);
			} else {
				appendSqlWhereWithEntityAndVisitor(bindings, entityClass, tableMeta, sql, whereKeyGen, entity, columnPredicate, whereColumnVisitor);
			}
		}

		if (withLogicDeleted) {
			// 强制添加非逻辑删除条件
			tableMeta.getColumns().values().stream()
				.filter(c -> c.isLogicDeleted())
				.forEach(meta -> {
					String columnName = meta.getColumnName();
					if (whereColumns.contains(columnName)) {
						// 如已在where条件中，则忽略。本方法主要用于单表操作，不考虑存在表名别的字段的情况
						return;
					}
					String propertiesString = meta.getPropertiesString();
					Object val = Converters.convertQuietly(meta.getFieldType(), false);
					String keyName = whereKeyGen.generate();
					if (Strings.isNotBlank(propertiesString)) {
						sql.where(columnName + " = #{" + keyName + "," + propertiesString + "}");
					} else {
						sql.where(columnName + " = #{" + keyName + "}");
					}
					bindings.put(keyName, val);
				});
		}

		// 排序字段
		Object orderByObj = BindingValues.getBindingValueOrDefault(bindings, orderByKey, null);
		OrderBy orderBy = null;
		if (orderByObj instanceof String) {
			orderBy = Queries.newOrderBy((String) orderByObj);
		} else if (orderByObj instanceof OrderBy) {
			orderBy = (OrderBy) orderByObj;
		}
		if (orderBy != null) {
			for (OrderBy.Item item : orderBy.getItems()) {
				ColumnMeta columnMeta = tableMeta.getColumns().get(item.getField());
				if (columnMeta != null) {
					sql.orderBy(columnMeta.getColumnName() + " " + item.getDirection().getSqlText());
					continue;
				}

				ExpressionMeta expressionMeta = tableMeta.getExpressions().get(item.getField());
				if (expressionMeta != null) {
					sql.orderBy(expressionMeta.getExpressionWithTableName() + " " + item.getDirection().getSqlText());
					continue;
				}
			}
		}

		return sql.toSqlString();
	}

	private static void appendSqlWhereWithEntityAndVisitor(Map<String, Object> bindings, Class<?> entityClass
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen
		, Object entity, ColumnPredicate columnPredicate, Consumer<String> columnVisitor) {
		// 实体查询条件
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			Object val = entityMap.get(name);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}

			if (Objs.isNotEmpty(val)) {
				String key = whereKeyGen.generate();
				appendSqlWhereWithVal(bindings, sql, columnName, meta.getFieldType(), val, key, propertiesString);
				columnVisitor.accept(name);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
					columnVisitor.accept(name);
				}
			}
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ExpressionMeta meta = entry.getValue();
			// 无别名时，使用表名，防止子查询中字段来源不明确
			String columnName = meta.getExpressionWithTableName();
			Object val = entityMap.get(name);

			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}
			if (Objs.isNotEmpty(val)) {
				String key = whereKeyGen.generate();
				appendSqlWhereWithVal(bindings, sql, columnName, meta.getFieldType(), val, key, propertiesString);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
				}
			}
		}
	}

	private static void appendSqlWhereWithCriteriaAndVisitor(Map<String, Object> bindings
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen, Object criteria, Consumer<String> columnVisitor) {
		// 追加查询条件
		if (criteria instanceof Criteria) {
			Function<String, String> columnDiscovery = Queries.newColumnDiscovery(tableMeta);
			SqlNode sqlNode = Queries.parse((Criteria) criteria, false, columnDiscovery, columnVisitor);
			if (!sqlNode.isSkipped()) {
				BoundSql boundSql = sqlNode.asBoundSql(whereKeyGen);
				sql.where(boundSql.getText());
				bindings.putAll(boundSql.getBindings());
			}
		}
	}

	private static void appendSqlWhereWithEntity(Map<String, Object> bindings, Class<?> entityClass
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen
		, Object entity, ColumnPredicate columnPredicate) {
		// 实体查询条件
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity :
			(tableMeta.getEntityClass().isAssignableFrom(entity.getClass())
				? Beans.newBeanMap(entity, tableMeta.getEntityClass())
				: Beans.newBeanMap(entity));
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			Object val = entityMap.get(name);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}

			if (Objs.isNotEmpty(val)) {
				String key = whereKeyGen.generate();
				appendSqlWhereWithVal(bindings, sql, columnName, meta.getFieldType(), val, key, propertiesString);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
				}
			}
		}
		for (Map.Entry<String, ExpressionMeta> entry : tableMeta.getExpressions().entrySet()) {
			String name = entry.getKey();

			// 不在包含列表
			if (!columnPredicate.isIncludedColumn(name)) {
				continue;
			}

			ExpressionMeta meta = entry.getValue();
			// 无别名时，使用表名，防止子查询中字段来源不明确
			String columnName = meta.getExpressionWithTableName();
			Object val = entityMap.get(name);
			String propertiesString = meta.getPropertiesString();
			if (val instanceof VarRef) {
				// 优先使用VarRef携带的参数属性
				propertiesString = ((VarRef<?>) val).getProps();
				val = ((VarRef<?>) val).getValue();
			}

			if (Objs.isNotEmpty(val)) {
				String key = whereKeyGen.generate();
				appendSqlWhereWithVal(bindings, sql, columnName, meta.getFieldType(), val, key, propertiesString);
			} else {
				// 需要包含空值字段
				boolean include = columnPredicate.isIncludedEmptyColumn(name);
				if (include) {
					sql.where(columnName + " IS NULL");
				}
			}
		}
	}

	private static void appendSqlWhereWithCriteria(Map<String, Object> bindings
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen, Object criteria) {
		// 追加查询条件
		if (criteria instanceof Criteria) {
			Function<String, String> columnDiscovery = Queries.newColumnDiscovery(tableMeta);
			SqlNode sqlNode = Queries.parse((Criteria) criteria, false, columnDiscovery);
			if (!sqlNode.isSkipped()) {
				BoundSql boundSql = sqlNode.asBoundSql(whereKeyGen);
				sql.where(boundSql.getText());
				bindings.putAll(boundSql.getBindings());
			}
		}
	}

	/**
	 * 添加查询条件， 支持处理集合与数组类型
	 */
	private static void appendSqlWhereWithVal(@Nonnull Map<String, Object> bindings, @Nonnull SqlStatement sql, @Nonnull String columnName, @Nonnull Class<?> fieldType, @Nonnull Object val, @Nonnull String key, String varProps) {
		// 注意： val 变量类型不可能为VarRef， 已在外部处理
		String varSuffix = Strings.isNotBlank(varProps) ? "," + varProps : "";
		// 日期字段
		if (Date.class.isAssignableFrom(fieldType)) {
			// 两个元素的日期类字段特殊处理，认为是日期范围条件
			Date[] range = BindingValues.getDateRangeOrNull(val);
			if (range != null) {
				Date start = range[0];
				Date end = range[1];
				if (start != null) {
					sql.where(columnName + " >= #{" + key + "0" + varSuffix + "} ");
					bindings.put(key + "0", start);
				}
				if (end != null) {
					sql.where(columnName + " <= #{" + key + "1" + varSuffix + "} ");
					bindings.put(key + "1", end);
				}
				// 日期条件完毕
				return;
			}
		}

		// 文本字段
		if (String.class.isAssignableFrom(fieldType)) {
			if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
				bindings.put(key, val);
				sql.where(columnName + " like #{" + key + varSuffix + "} ");
				// 完成条件绑定
				return;
			}
		}

		if (val instanceof ValueRange) {
			ValueRange<?> range = (ValueRange<?>) val;
			Object start = range.getStart();
			Object end = range.getEnd();
			if (Objs.isNotEmpty(start)) {
				sql.where(columnName + " >= #{" + key + "0" + varSuffix + "} ");
				bindings.put(key + "0", start);
			}
			if (Objs.isNotEmpty(end)) {
				sql.where(columnName + " <= #{" + key + "1" + varSuffix + "} ");
				bindings.put(key + "1", end);
			}
		} else if (val instanceof Iterable) {
			StringBuilder where = new StringBuilder();
			where.append(columnName).append(" IN ( ");
			int i = 0;
			boolean first = true;
			Iterator<?> iter = ((Iterable<?>) val).iterator();
			while (iter.hasNext()) {
				Object next = iter.next();
				if (i > 0 && i % 1000 == 0) {
					where.append(" ) AND ").append(columnName).append(" IN ( ");
					first = true;
				}
				if (first) {
					first = false;
				} else {
					where.append(", ");
				}
				where.append("#{").append(key).append(i).append(varSuffix).append("}");
				bindings.put(key + i, Converters.convertQuietly(fieldType, next));
				i++;
			}
			where.append(" ) ");
			sql.where(where.toString());
		} else if (val.getClass().isArray()) {
			int len = Array.getLength(val);
			StringBuilder where = new StringBuilder();
			where.append(columnName).append(" IN ( ");
			boolean first = true;
			for (int i = 0; i < len; i++) {
				Object next = Array.get(val, i);
				if (i > 0 && i % 1000 == 0) {
					where.append(" ) AND ").append(columnName).append(" IN ( ");
					first = true;
				}
				if (first) {
					first = false;
				} else {
					where.append(", ");
				}
				where.append("#{").append(key).append(i).append(varSuffix).append("}");
				bindings.put(key + i, Converters.convertQuietly(fieldType, next));
			}
			where.append(" ) ");
			sql.where(where.toString());
		} else {
			sql.where(columnName + " = #{" + key + varSuffix + "} ");
			bindings.put(key, Converters.convertQuietly(fieldType, val));
		}
	}


}
