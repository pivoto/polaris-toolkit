package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.jdbc.sql.BoundSql;
import io.polaris.core.jdbc.sql.SqlStatement;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.node.VarNameGenerator;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.query.Queries;
import io.polaris.core.jdbc.sql.statement.*;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.Types;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.EntityMapperDeclared;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.builder.annotation.ProviderContext;

import javax.annotation.Nonnull;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
 */
@Published
public class MapperProviders {
	private static final ILogger log = ILogger.of(MapperProviders.class);
	private static final String KEY_WHERE_PREFIX = "w_";
	private static final String KEY_VALUE_PREFIX = "v_";
	private static final Map<String, BiFunction<Map<String, Object>, ProviderContext, String>> methods;

	static {
		Map<String, BiFunction<Map<String, Object>, ProviderContext, String>> map = new HashMap<>();
		map.put(MapperProviderKeys.insertBySql, MapperProviders::insertBySql);
		map.put(MapperProviderKeys.deleteBySql, MapperProviders::deleteBySql);
		map.put(MapperProviderKeys.updateBySql, MapperProviders::updateBySql);
		map.put(MapperProviderKeys.selectBySql, MapperProviders::selectBySql);
		map.put(MapperProviderKeys.countBySql, MapperProviders::countBySql);
		map.put(MapperProviderKeys.mergeBySql, MapperProviders::mergeBySql);

		map.put(MapperProviderKeys.insertEntity, MapperProviders::insertEntity);

		map.put(MapperProviderKeys.deleteEntityById, MapperProviders::deleteEntityById);
		map.put(MapperProviderKeys.deleteEntityByAny, MapperProviders::deleteEntityByAny);

		map.put(MapperProviderKeys.updateEntityById, MapperProviders::updateEntityById);
		map.put(MapperProviderKeys.updateEntityByAny, MapperProviders::updateEntityByAny);

		map.put(MapperProviderKeys.selectEntity, MapperProviders::selectEntity);
		map.put(MapperProviderKeys.selectEntityById, MapperProviders::selectEntityById);

		map.put(MapperProviderKeys.countEntity, MapperProviders::countEntity);

		methods = Collections.unmodifiableMap(map);
	}

	@Published
	public static String insertEntity(Map<String, Object> map, ProviderContext context) {
		return doInsertEntity(map, context);
	}


	private static String doInsertEntity(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		Object entity = getObjectOfKey(map, EntityMapperKeys.ENTITY, Collections.emptyMap());
		Predicate<String> includeEntityNulls = getEntityIncludeNullPredicate(map);

		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);

		SqlStatement sql = SqlStatement.of();
		sql.insert(tableMeta.getTable());

		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean insertable = meta.isInsertable() || meta.isCreateTime() || meta.isUpdateTime();
			if (!insertable) {
				continue;
			}
			Object val = Statements.getValForInsert(entityMap, meta);
			if (isNotEmpty(val)) {
				sql.columnAndValue(columnName, "#{" + KEY_VALUE_PREFIX + name + "}");
				map.put(KEY_VALUE_PREFIX + name, val);
			} else if (includeEntityNulls.test(name)) {
				sql.columnAndValue(columnName, "NULL");
			}
		}
		return sql.toSqlString();
	}


	@Published
	public static String deleteEntityById(Map<String, Object> map, ProviderContext context) {
		return doDeleteEntity(map, context, true);
	}

	@Published
	public static String deleteEntityByAny(Map<String, Object> map, ProviderContext context) {
		return doDeleteEntity(map, context, false);
	}

	private static String doDeleteEntity(Map<String, Object> map, ProviderContext context, boolean byId) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.delete(tableMeta.getTable());

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		Predicate<String> whereIncludeNullPredicate = getWhereIncludeNullPredicate(map);

		if (byId) {
			Object entity = getObjectOfKey(map, EntityMapperKeys.ENTITY, Collections.emptyMap());
			Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
			for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
				String name = entry.getKey();
				ColumnMeta meta = entry.getValue();
				String columnName = meta.getColumnName();
				boolean primaryKey = meta.isPrimaryKey();
				boolean version = meta.isVersion();
				Object val = entityMap.get(name);
				if (primaryKey || version) {
					if (isNotEmpty(val)) {
						appendSqlWhereWithVal(map, sql, meta, val, whereKeyGen.generate());
					} else {
						sql.where(columnName + " IS NULL");
					}
				}
			}
		} else {
			Object where = getObjectOfKey(map, EntityMapperKeys.WHERE, Collections.emptyMap());
			if (where instanceof Criteria) {
				appendSqlWhereWithCriteria(map, tableMeta, sql, whereKeyGen, where);
			} else if (where != null) {
				appendSqlWhereWithEntity(map, entityClass, tableMeta, sql, whereKeyGen, whereIncludeNullPredicate, where);
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}


	@Published
	public static String updateEntityById(Map<String, Object> map, ProviderContext context) {
		return doUpdateEntityById(map, context);
	}


	private static String doUpdateEntityById(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		Object entity = getObjectOfKey(map, EntityMapperKeys.ENTITY, Collections.emptyMap());
		Predicate<String> predicate = getEntityIncludeNullPredicate(map);
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);
			boolean updatable = meta.isUpdatable() || meta.isVersion() || meta.isUpdateTime();
			if (primaryKey || version) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					sql.where(columnName + " = #{" + KEY_WHERE_PREFIX + name + "}");
					map.put(KEY_WHERE_PREFIX + name, val);
				}
			}
			if (!updatable) {
				continue;
			}
			if (val == null) {
				if (meta.isUpdateTime()) {
					val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), new Date());
				}
			}
			if (val == null) {
				String updateDefault = meta.getUpdateDefault();
				if (Strings.isNotBlank(updateDefault)) {
					val = ConverterRegistry.INSTANCE.convertQuietly(meta.getFieldType(), updateDefault);
				}
			}
			if (version) {
				val = val == null ? 1L : ((Number) val).longValue() + 1;
			} else if (primaryKey) {
				// skip set
				continue;
			}
			if (val != null) {
				sql.set(columnName + " = #{" + KEY_VALUE_PREFIX + name + "}");
				map.put(KEY_VALUE_PREFIX + name, val);
			} else if (predicate.test(name)) {
				sql.set(columnName + " = NULL");
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	@Published
	public static String updateEntityByAny(Map<String, Object> map, ProviderContext context) {
		return doUpdateEntityByAny(map, context);
	}


	private static String doUpdateEntityByAny(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.update(tableMeta.getTable());

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		Predicate<String> includeWhereNulls = getWhereIncludeNullPredicate(map);
		Predicate<String> includeEntityNulls = getEntityIncludeNullPredicate(map);

		Object entity = getObjectOfKey(map, EntityMapperKeys.ENTITY, Collections.emptyMap());
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
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
			Object entityVal = Statements.getValForUpdate(entityMap, meta);
			if (version) {
				entityVal = entityVal == null ? 1L : ((Number) entityVal).longValue() + 1;
			}
			if (entityVal != null) {
				sql.set(columnName + " = #{" + KEY_VALUE_PREFIX + name + "}");
				map.put(KEY_VALUE_PREFIX + name, entityVal);
			} else if (includeEntityNulls.test(name)) {
				sql.set(columnName + " = NULL");
			}
		}

		Object where = getObjectOfKey(map, EntityMapperKeys.WHERE, Collections.emptyMap());
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(map, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(map, entityClass, tableMeta, sql, whereKeyGen, includeWhereNulls, where);
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}


	@Published
	public static String countEntity(Map<String, Object> map, ProviderContext context) {
		return doCountEntity(map, context);
	}


	private static String doCountEntity(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());
		sql.select("COUNT(*)");

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		Predicate<String> whereIncludeNullPredicate = getWhereIncludeNullPredicate(map);

		Object where = getObjectOfKey(map, EntityMapperKeys.WHERE);
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(map, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(map, entityClass, tableMeta, sql, whereKeyGen, whereIncludeNullPredicate, where);
		}

		return sql.toSqlString();
	}

	@Published
	public static String selectEntityById(Map<String, Object> map, ProviderContext context) {
		return doSelectEntityById(map, context);
	}


	private static String doSelectEntityById(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		Object entity = getObjectOfKey(map, EntityMapperKeys.ENTITY);
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			boolean primaryKey = meta.isPrimaryKey();
			boolean version = meta.isVersion();
			Object val = entityMap.get(name);

			sql.select(columnName + " " + name);

			if (primaryKey || version) {
				if (val == null) {
					sql.where(columnName + " IS NULL");
				} else {
					sql.where(columnName + " = #{" + KEY_WHERE_PREFIX + name + "}");
					map.put(KEY_WHERE_PREFIX + name, val);
				}
			}
		}
		if (!sql.where().hasConditions()) {
			throw new IllegalStateException("缺少条件子句");
		}
		return sql.toSqlString();
	}

	@Published
	public static String selectEntity(Map<String, Object> map, ProviderContext context) {
		return doSelectEntity(map, context);
	}


	private static String doSelectEntity(Map<String, Object> map, ProviderContext context) {
		Class<?> entityClass = getEntityClass(context);
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		SqlStatement sql = SqlStatement.of();
		sql.from(tableMeta.getTable());

		VarNameGenerator whereKeyGen = VarNameGenerator.newInstance(KEY_WHERE_PREFIX);
		Predicate<String> whereIncludeNullPredicate = getWhereIncludeNullPredicate(map);

		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			sql.select(columnName + " " + name);
		}
		Object where = getObjectOfKey(map, EntityMapperKeys.WHERE);
		if (where instanceof Criteria) {
			appendSqlWhereWithCriteria(map, tableMeta, sql, whereKeyGen, where);
		} else if (where != null) {
			appendSqlWhereWithEntity(map, entityClass, tableMeta, sql, whereKeyGen, whereIncludeNullPredicate, where);
		}
		// 排序字段
		Object orderByObj = getObjectOfKey(map, EntityMapperKeys.ORDER_BY);
		OrderBy orderBy = null;
		if (orderByObj instanceof String) {
			orderBy = Queries.newOrderBy((String) orderByObj);
		} else if (orderByObj instanceof OrderBy) {
			orderBy = (OrderBy) orderByObj;
		}
		if (orderBy != null) {
			for (OrderBy.Item item : orderBy.getItems()) {
				ColumnMeta columnMeta = tableMeta.getColumns().get(item.getField());
				if (columnMeta == null) {
					continue;
				}
				sql.orderBy(columnMeta.getColumnName() + " " + item.getDirection().getSqlText());
			}
		}
		return sql.toSqlString();
	}

	private static void appendSqlWhereWithEntity(Map<String, Object> map, Class<?> entityClass
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen
		, Predicate<String> includeWhereNulls, Object entity) {
		// 实体查询条件
		Map<String, Object> entityMap = (entity instanceof Map) ? (Map<String, Object>) entity : Beans.newBeanMap(entity, entityClass);
		for (Map.Entry<String, ColumnMeta> entry : tableMeta.getColumns().entrySet()) {
			String name = entry.getKey();
			ColumnMeta meta = entry.getValue();
			String columnName = meta.getColumnName();
			Object val = entityMap.get(name);
			if (isNotEmpty(val)) {
				appendSqlWhereWithVal(map, sql, meta, val, whereKeyGen.generate());
			} else if (includeWhereNulls.test(name)) {
				sql.where(columnName + " IS NULL");
			}
		}
	}

	private static void appendSqlWhereWithCriteria(Map<String, Object> map
		, TableMeta tableMeta, SqlStatement sql, VarNameGenerator whereKeyGen, Object criteria) {
		// 追加查询条件
		if (criteria instanceof Criteria) {
			Function<String, String> columnDiscovery = field -> {
				ColumnMeta columnMeta = tableMeta.getColumns().get(field);
				if (columnMeta != null) {
					return columnMeta.getColumnName();
				}
				return null;
			};
			SqlNode sqlNode = Queries.parse((Criteria) criteria, false, columnDiscovery);
			if (!sqlNode.isSkipped()) {
				BoundSql boundSql = sqlNode.asBoundSql(whereKeyGen);
				sql.where(boundSql.getText());
				map.putAll(boundSql.getBindings());
			}
		}
	}


	/**
	 * 添加查询条件， 支持处理集合与数组类型
	 */
	private static void appendSqlWhereWithVal(@Nonnull Map<String, Object> map
		, @Nonnull SqlStatement sql, @Nonnull ColumnMeta meta, @Nonnull Object val, String key) {
		String columnName = meta.getColumnName();
		StringBuilder where = new StringBuilder();
		// 日期字段
		if (Date.class.isAssignableFrom(meta.getFieldType())) {
			// 两个元素的日期类字段特殊处理，认为是日期范围条件
			Date[] range = Statements.extractDateRange(val);
			if (range != null) {
				Date start = range[0];
				Date end = range[1];
				if (start != null) {
					where.append(columnName).append(" >= #{").append(key).append("0}");
					map.put(key + "0", start);
				}
				if (end != null) {
					where.append(columnName).append(" <= #{").append(key).append("1}");
					map.put(key + "1", end);
				}
				// 日期条件完毕
				sql.where(where.toString());
				return;
			}
		}

		// 文本字段
		if (String.class.isAssignableFrom(meta.getFieldType())) {
			if (val instanceof String && (((String) val).startsWith("%") || ((String) val).endsWith("%"))) {
				where.append(columnName).append(" like #{").append(key).append("}");
				map.put(key, val);
				sql.where(where.toString());
				return;
			}
		}

		if (val instanceof Iterable) {
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
				where.append("#{").append(key).append(i).append("}");
				map.put(key + "i", next);
				i++;
			}
			where.append(" )");
		} else if (val.getClass().isArray()) {
			int len = Array.getLength(val);
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
				where.append("#{").append(key).append(i).append("}");
				map.put(key + "i", next);
			}
			where.append(" )");
		} else {
			where.append(columnName).append(" = #{").append(key).append("}");
			map.put(key, val);
		}
		sql.where(where.toString());
	}

	public static Object getObjectOfKey(Map<String, Object> map, String key, Object defVal) {
		if (map.containsKey(key)) {
			return map.getOrDefault(key, defVal);
		}
		return defVal;
	}

	public static Object getObjectOfKey(Map<String, Object> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return null;
	}

	private static Predicate<String> getWhereIncludeNullPredicate(Map<String, Object> map) {
		Object o = getObjectOfKey(map, EntityMapperKeys.WHERE_NULLS_KEYS);
		if (o instanceof Collection) {
			Collection<?> nullKeys = (Collection<?>) o;
			if (!nullKeys.isEmpty()) {
				return nullKeys::contains;
			}
		}
		boolean includeNulls = Boolean.TRUE.equals(getObjectOfKey(map, EntityMapperKeys.WHERE_NULLS_INCLUDE));
		if (includeNulls) {
			return field -> true;
		}
		return field -> false;
	}

	private static Predicate<String> getEntityIncludeNullPredicate(Map<String, Object> map) {
		Object o = getObjectOfKey(map, EntityMapperKeys.ENTITY_NULLS_KEYS);
		if (o instanceof Collection) {
			Collection<?> nullKeys = (Collection<?>) o;
			if (!nullKeys.isEmpty()) {
				return nullKeys::contains;
			}
		}
		boolean includeNulls = Boolean.TRUE.equals(getObjectOfKey(map, EntityMapperKeys.ENTITY_NULLS_INCLUDE));
		if (includeNulls) {
			return field -> true;
		}
		return field -> false;
	}

	private static Object createTimeVal(Class<?> fieldType) {
		if (fieldType.isAssignableFrom(Date.class)) {
			return new Date();
		}
		if (fieldType.isAssignableFrom(Timestamp.class)) {
			return new Timestamp(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(java.sql.Date.class)) {
			return new Timestamp(System.currentTimeMillis());
		}
		if (fieldType.isAssignableFrom(LocalDateTime.class)) {
			return LocalDateTime.now();
		}
		if (fieldType.isAssignableFrom(LocalDate.class)) {
			return LocalDate.now();
		}
		return new Date();
	}

	private static Class<?> getEntityClass(ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();
		EntityMapperDeclared declared = mapperMethod.getAnnotation(EntityMapperDeclared.class);
		if (declared != null) {
			return declared.entity();
		}
		Class<?> entityClass = null;
		Class<?> mapperType = context.getMapperType();
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
		}
		if (entityClass == null || entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return entityClass;
	}

	@Published
	public static String insertBySql(Map<String, Object> map, ProviderContext context) {
		return getSqlWithBindings(map, (InsertStatement<?>) map.get(EntityMapperKeys.INSERT));
	}

	@Published
	public static String deleteBySql(Map<String, Object> map, ProviderContext context) {
		return getSqlWithBindings(map, (DeleteStatement<?>) map.get(EntityMapperKeys.DELETE));
	}

	@Published
	public static String updateBySql(Map<String, Object> map, ProviderContext context) {
		return getSqlWithBindings(map, (UpdateStatement<?>) map.get(EntityMapperKeys.UPDATE));
	}

	@Published
	public static String selectBySql(Map<String, Object> map, ProviderContext context) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(EntityMapperKeys.SELECT);
		return getSqlWithBindings(map, st);
	}

	@Published
	public static String countBySql(Map<String, Object> map, ProviderContext context) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(EntityMapperKeys.SELECT);
		return getSqlWithBindings(map, st::toCountSqlNode);
	}

	@Published
	public static String mergeBySql(Map<String, Object> map, ProviderContext context) {
		MergeStatement<?> st = (MergeStatement<?>) map.get(EntityMapperKeys.MERGE);
		return getSqlWithBindings(map, st);
	}

	private static boolean isNotEmpty(Object val) {
		return Objs.isNotEmpty(val);
	}

	private static String getSqlWithBindings(Map<String, Object> map, SqlNodeBuilder sqlNodeBuilder) {
		BoundSql boundSql = sqlNodeBuilder.toSqlNode().asBoundSql();
		Map<String, Object> bindings = boundSql.getBindings();
		if (bindings != null && !bindings.isEmpty()) {
			map.putAll(bindings);
		}
		return boundSql.getText();
	}
}
