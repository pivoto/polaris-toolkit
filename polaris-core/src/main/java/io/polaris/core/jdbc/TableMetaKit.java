package io.polaris.core.jdbc;

import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.polaris.core.asm.reflect.ClassAccess;
import io.polaris.core.jdbc.annotation.Column;
import io.polaris.core.jdbc.annotation.Expression;
import io.polaris.core.jdbc.annotation.Id;
import io.polaris.core.jdbc.annotation.Table;
import io.polaris.core.jdbc.annotation.processing.JdbcBeanInfo;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.Loggers;

/**
 * @author Qt
 * @since Aug 20, 2023
 */
public class TableMetaKit {
	private static final ILogger log = Loggers.of(TableMetaKit.class);
	private static final TableMetaKit instance = new TableMetaKit();
	private final Map<Class<?>, TableMeta> cache = new ConcurrentHashMap<>();
	private final Map<Class<?>, TableMeta> mutableCache = new ConcurrentHashMap<>();
	private final ThreadLocal<Map<Class<?>, TableMetaMutation>> mutationsLocal = new ThreadLocal<>();

	public static TableMetaKit instance() {
		return instance;
	}

	private Object readResolve() throws ObjectStreamException {
		return instance;
	}

	/**
	 * 覆盖默认的实体表元数据配置信息，优先级高于默认配置
	 * <p>
	 * 在需要动态修改表名、列名等场景下使用
	 */
	public void addMutation(TableMetaMutation mutation) {
		if (mutation == null) {
			return;
		}
		Class<?> entityClass = mutation.entityClass();
		if (!mutation.mutable()) {
			// 还原
			mutableCache.remove(entityClass);
			return;
		}
		TableMeta origin = getOrigin(entityClass);
		TableMeta tableMeta = mutation.apply(origin);
		mutableCache.put(entityClass, tableMeta);
	}

	public void removeMutation(Class<?> entityClass) {
		mutableCache.remove(entityClass);
	}

	public void clearMutationsInCurrentThread() {
		mutationsLocal.remove();
	}

	public void addMutationInCurrentThread(TableMetaMutation mutation) {
		mutationsInCurrentThread().put(mutation.entityClass(), mutation);
	}

	public void removeMutationInCurrentThread(TableMetaMutation mutation) {
		mutationsInCurrentThread().remove(mutation.entityClass());
	}

	public void addMutationsInCurrentThread(TableMetaMutation... mutations) {
		if (mutations.length == 0) {
			return;
		}
		Map<Class<?>, TableMetaMutation> map = mutationsInCurrentThread();
		for (TableMetaMutation mutation : mutations) {
			map.put(mutation.entityClass(), mutation);
		}
	}

	private Map<Class<?>, TableMetaMutation> mutationsInCurrentThread() {
		Map<Class<?>, TableMetaMutation> map = mutationsLocal.get();
		if (map == null) {
			map = new HashMap<>();
			mutationsLocal.set(map);
		}
		return map;
	}

	public TableMeta get(String entityClassName) {
		try {
			Class<?> type = Class.forName(entityClassName);
			return get(type);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public TableMeta get(Class<?> entityClass) {
		// 优先使用动态覆写的配置
		Map<Class<?>, TableMetaMutation> map = mutationsLocal.get();
		if (map != null) {
			TableMetaMutation mutation = map.get(entityClass);
			if (mutation != null) {
				return mutation.apply(getOrigin(entityClass));
			}
		}
		TableMeta meta = mutableCache.get(entityClass);
		if (meta == null) {
			meta = getOrigin(entityClass);
		}
		return meta;
	}

	private TableMeta getOrigin(Class<?> entityClass) {
		return cache.computeIfAbsent(entityClass, c -> parse(entityClass));
	}

	public Map<String, ColumnMeta> getColumns(Class<?> entityClass) {
		return get(entityClass).getColumns();
	}

	public ColumnMeta getColumn(Class<?> entityClass, String fieldName) {
		return get(entityClass).getColumns().get(fieldName);
	}

	public Map<String, ExpressionMeta> getExpressions(Class<?> entityClass) {
		return get(entityClass).getExpressions();
	}

	public ExpressionMeta getExpression(Class<?> entityClass, String fieldName) {
		return get(entityClass).getExpressions().get(fieldName);
	}

	private TableMeta parse(Class<?> entityClass) {
		Table annotation = entityClass.getAnnotation(Table.class);
		String entityClassName = entityClass.getName();
		if (annotation == null) {
			throw new IllegalArgumentException("unsupported class without @Table annotation: " + entityClassName);
		}
		try {
			String metaClassName = entityClassName + annotation.metaSuffix();
			Class<?> c = Class.forName(metaClassName);
			if (IEntityMeta.class.isAssignableFrom(c)) {
				ClassAccess<?> classAccess = ClassAccess.get(c);
				String schema = (String) classAccess.getField(null, "SCHEMA");
				String catalog = (String) classAccess.getField(null, "CATALOG");
				String table = (String) classAccess.getField(null, "TABLE");
				String alias = (String) classAccess.getField(null, "ALIAS");
				@SuppressWarnings("unchecked")
				Map<String, ColumnMeta> columns = (Map<String, ColumnMeta>) classAccess.getField(null, "COLUMNS");
				@SuppressWarnings("unchecked")
				Map<String, ExpressionMeta> expressions = (Map<String, ExpressionMeta>) classAccess.getField(null, "EXPRESSIONS");
				return TableMeta.builder().entityClass(entityClass)
					.table(table).alias(alias)
					.columns(columns)
					.expressions(expressions)
					.schema(schema).catalog(catalog).build();
			}
		} catch (Throwable e) {
			log.error("", e);
		}

		Set<String> retrieved = new HashSet<>();
		Map<String, ColumnMeta> columns = new LinkedHashMap<>();
		Map<String, ExpressionMeta> expressions = new LinkedHashMap<>();
		Class<?> targetClass = entityClass;
		do {
			Field[] fields = targetClass.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				if (retrieved.contains(fieldName)) {
					continue;
				}
				retrieved.add(fieldName);
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					if (column.ignored()) {
						continue;
					}
				}
				Expression expression = field.getAnnotation(Expression.class);
				if (expression != null) {
					if (expression.value() != null && !expression.value().trim().isEmpty()){
						JdbcBeanInfo.ExpressionInfo expressionInfo = new JdbcBeanInfo.ExpressionInfo();
						expressionInfo.readExpression(fieldName, expression);
						ExpressionMeta expressionMeta = ExpressionMeta.builder()
							.tableName(annotation.value())
							.schema(annotation.schema())
							.catalog(annotation.catalog())
							.fieldName(fieldName)
							.fieldType(field.getType())
							.expression(expressionInfo.getExpression())
							.jdbcType(expressionInfo.getJdbcTypeName())
							.jdbcTypeValue(expressionInfo.getJdbcTypeValue())
							.tableAliasPlaceholder(expressionInfo.getTableAliasPlaceholder())
							.selectable(expressionInfo.isSelectable())
							.build();
						expressions.put(fieldName, expressionMeta);
						continue;
					}
				}

				JdbcBeanInfo.FieldInfo fieldInfo = new JdbcBeanInfo.FieldInfo();
				fieldInfo.readColumn(fieldName, column, field.getAnnotation(Id.class));
				ColumnMeta columnMeta = ColumnMeta.builder()
					.tableName(annotation.value())
					.schema(annotation.schema())
					.catalog(annotation.catalog())
					.fieldName(fieldName)
					.fieldType(field.getType())
					.columnName(fieldInfo.getColumnName())
					.jdbcType(fieldInfo.getJdbcTypeName())
					.jdbcTypeValue(fieldInfo.getJdbcTypeValue())
					.updateDefault(fieldInfo.getUpdateDefault())
					.insertDefault(fieldInfo.getInsertDefault())
					.nullable(fieldInfo.isNullable())
					.insertable(fieldInfo.isInsertable())
					.updatable(fieldInfo.isUpdatable())
					.version(fieldInfo.isVersion())
					.logicDeleted(fieldInfo.isLogicDeleted())
					.createTime(fieldInfo.isCreateTime())
					.updateTime(fieldInfo.isUpdateTime())
					.primaryKey(fieldInfo.isId())
					.autoIncrement(fieldInfo.isAutoIncrement())
					.seqName(fieldInfo.getSeqName())
					.build();
				columns.put(fieldName, columnMeta);
			}
			targetClass = targetClass.getSuperclass();
		} while (targetClass != null && targetClass != Object.class);
		return TableMeta.builder().entityClass(entityClass)
			.table(annotation.value()).alias(annotation.alias())
			.columns(Collections.unmodifiableMap(columns))
			.expressions(Collections.unmodifiableMap(expressions))
			.schema(annotation.schema()).catalog(annotation.catalog()).build();
	}

}
