package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import io.polaris.core.collection.Lists;
import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Ref;
import io.polaris.core.tuple.Tuple2;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.ColumnJdbcType;
import io.polaris.mybatis.annotation.ColumnTypeHandler;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.DynamicUseGeneratedKeys;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.consts.MappingKeys;
import io.polaris.mybatis.mapper.EntityMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
public class EnhancedAutoMapperHelper {
	private static final Map<MapperKey, Ref<Class<?>>> ENTITY_CLASS_MAP = new ConcurrentHashMap<>();

	public static EnhancedAutoMapperMeta parseEnhancedAutoMapperMeta(ProviderContext context) {
		return parseEnhancedAutoMapperMeta(context, getEntityClassOrNull(context));
	}

	public static EnhancedAutoMapperMeta parseEnhancedAutoMapperMeta(ProviderContext context, Class<?> declaredEntityClass) {
		return parseEnhancedAutoMapperMeta(context.getMapperMethod(), context.getMapperType(), declaredEntityClass);
	}

	public static EnhancedAutoMapperMeta parseEnhancedAutoMapperMeta(Method method, Class<?> mapperClass) {
		return parseEnhancedAutoMapperMeta(method, mapperClass, getEntityClassOrNull(method, mapperClass));
	}

	public static EnhancedAutoMapperMeta parseEnhancedAutoMapperMeta(Method method, Class<?> mapperClass, Class<?> declaredEntityClass) {
		EnhancedAutoMapperMeta.Builder metaBuilder = EnhancedAutoMapperMeta.builder();

		DynamicUseGeneratedKeys dynamicUseGeneratedKeys = method.getAnnotation(DynamicUseGeneratedKeys.class);
		parseDynamicUseGeneratedKeys(metaBuilder, dynamicUseGeneratedKeys, method, mapperClass, declaredEntityClass);

		DynamicResultMapping dynamicResultMapping = method.getAnnotation(DynamicResultMapping.class);
		parseDynamicResultMapping(metaBuilder, dynamicResultMapping, method, mapperClass, declaredEntityClass);

		return metaBuilder.build();
	}


	public static void parseDynamicUseGeneratedKeys(EnhancedAutoMapperMeta.Builder metaBuilder, DynamicUseGeneratedKeys dynamicUseGeneratedKeys, Method method, Class<?> mapperClass, Class<?> declaredEntityClass) {
		if (dynamicUseGeneratedKeys == null) {
			return;
		}
		String autoColumnPrefix = dynamicUseGeneratedKeys.value();
		List<ColumnMeta> autoColumns = null;

		Class<?> entityClass = dynamicUseGeneratedKeys.entity();
		if (entityClass == Object.class || entityClass == void.class) {
			if (declaredEntityClass != null && declaredEntityClass != Object.class && declaredEntityClass != void.class) {
				entityClass = declaredEntityClass;
			} else {
				entityClass = getEntityClassOrThrows(method, mapperClass);
			}
		}
		TableMeta tableMeta = TableMetaKit.instance().get(entityClass);
		autoColumns = tableMeta.getColumns().values().stream()
			.filter(columnMeta ->
				columnMeta.isPrimaryKey() &&
					(
						columnMeta.isAutoIncrement() || Strings.isNotBlank(columnMeta.getSeqName()) || Strings.isNotBlank(columnMeta.getIdSql())
					)
			)
			.collect(Collectors.toList());
		if (autoColumns.isEmpty()) {
			autoColumns = null;
		}

		metaBuilder.autoColumnPrefix(autoColumnPrefix)
			.autoColumns(autoColumns);
	}

	public static void parseDynamicResultMapping(EnhancedAutoMapperMeta.Builder metaBuilder, DynamicResultMapping dynamicResultMapping, Method method, Class<?> mapperClass, Class<?> declaredEntityClass) {
		if (dynamicResultMapping == null) {
			return;
		}
		Class<?> resultJavaType = null;
		resultJavaType = dynamicResultMapping.entity();
		if (resultJavaType == Object.class || resultJavaType == void.class) {
			if (declaredEntityClass != null && declaredEntityClass != Object.class && declaredEntityClass != void.class) {
				resultJavaType = declaredEntityClass;
			} else {
				resultJavaType = getEntityClassOrThrows(method, mapperClass);
			}
		}

		boolean useColumnName = dynamicResultMapping.useColumnName();
		String columnPrefix = dynamicResultMapping.columnPrefix();
		TableMeta tableMeta = TableMetaKit.instance().get(resultJavaType);
		List<EnhancedAutoMapperMeta.ResultMappingMeta> resultMappings = new ArrayList<>();

		List<ColumnMeta> idCols = new ArrayList<>();
		for (ColumnMeta col : tableMeta.getColumns().values()) {
			boolean match = col.hasProperties(MappingKeys.RESULT_MAPPING_KEYS_FILTER);
			// 是否存在
			if (match) {
				try {
					EnhancedAutoMapperMeta.ResultMappingMeta.Builder resultMappingMeta = EnhancedAutoMapperMeta.ResultMappingMeta.builder();

					String typeHandler = col.getProp(ColumnTypeHandler.KEY);
					if (Strings.isNotBlank(typeHandler)) {
						Class<?> clazz = Class.forName(typeHandler);
						if (TypeHandler.class.isAssignableFrom(clazz)) {
							//noinspection unchecked
							resultMappingMeta.typeHandlerClass((Class<? extends TypeHandler<?>>) clazz);
						}
					}
					String jdbcType = col.getProp(ColumnJdbcType.KEY);
					if (Strings.isNotBlank(jdbcType)) {
						resultMappingMeta.jdbcType(JdbcType.valueOf(jdbcType));
					}
					resultMappingMeta.property(col.getFieldName());
					resultMappingMeta.column(useColumnName ? col.getColumnName() : col.getFieldName());
					resultMappingMeta.javaType(col.getFieldType());
					resultMappingMeta.columnPrefix(Strings.trimToNull(columnPrefix));
					if (col.isPrimaryKey()) {
						resultMappingMeta.flags(Lists.asList(ResultFlag.ID));
					}
					resultMappings.add(resultMappingMeta.build());
				} catch (Exception ignored) {
					// 忽略异常
				}
			} else if (col.isPrimaryKey()) {
				idCols.add(col);
			}
		}
		if (resultMappings.isEmpty()) {
			resultMappings = null;
		} else {
			if (!idCols.isEmpty()) {
				for (ColumnMeta col : idCols) {
					EnhancedAutoMapperMeta.ResultMappingMeta.Builder resultMappingMeta = EnhancedAutoMapperMeta.ResultMappingMeta.builder();
					resultMappingMeta.property(col.getFieldName());
					resultMappingMeta.column(useColumnName ? col.getColumnName() : col.getFieldName());
					resultMappingMeta.javaType(col.getFieldType());
					resultMappingMeta.columnPrefix(Strings.trimToNull(columnPrefix));
					if (col.isPrimaryKey()) {
						resultMappingMeta.flags(Lists.asList(ResultFlag.ID));
					}
					resultMappings.add(resultMappingMeta.build());
				}
			}
		}

		// 添加结果映射
		metaBuilder.resultMappings(resultMappings)
			.resultJavaType(resultJavaType);
	}


	public static Class<?> registerEntityClass(ProviderContext context, Class<?> entityClass) {
		Ref<Class<?>> old = ENTITY_CLASS_MAP.put(new MapperKey(context), Ref.of(entityClass));
		return old != null ? old.get() : entityClass;
	}

	public static Class<?> registerEntityClass(Method mapperMethod, Class<?> mapperType, Class<?> entityClass) {
		Ref<Class<?>> old = ENTITY_CLASS_MAP.put(new MapperKey(mapperMethod, mapperType, ""), Ref.of(entityClass));
		return old != null ? old.get() : entityClass;
	}

	public static Class<?> registerEntityClass(Method mapperMethod, Class<?> mapperType, String databaseId, Class<?> entityClass) {
		Ref<Class<?>> old = ENTITY_CLASS_MAP.put(new MapperKey(mapperMethod, mapperType, databaseId), Ref.of(entityClass));
		return old != null ? old.get() : entityClass;
	}

	public static Class<?> getEntityClassOrThrows(ProviderContext context) {
		Ref<Class<?>> ref = ENTITY_CLASS_MAP.computeIfAbsent(new MapperKey(context),
			k -> Ref.of(innerGetEntityClass(context)));
		Class<?> entityClass = ref.get();
		if (entityClass != null) {
			return entityClass;
		}
		throw new IllegalStateException("未知实体类型！");
	}

	@Nullable
	public static Class<?> getEntityClassOrNull(ProviderContext context) {
		Ref<Class<?>> ref = ENTITY_CLASS_MAP.computeIfAbsent(new MapperKey(context),
			k -> Ref.of(innerGetEntityClass(context)));
		return ref.get();
	}

	/**
	 * 获取 Mapper 接口或方法上声明的实体类。
	 *
	 * @param mapperMethod 当前调用的方法对象
	 * @param mapperType   Mapper 接口类型
	 * @return 实体类 Class 对象
	 * @throws IllegalStateException 如果无法确定实体类则抛出异常
	 */
	public static Class<?> getEntityClassOrThrows(Method mapperMethod, Class<?> mapperType) {
		return getEntityClassOrThrows(mapperMethod, mapperType, "");
	}

	public static Class<?> getEntityClassOrThrows(Method mapperMethod, Class<?> mapperType, String databaseId) {
		Ref<Class<?>> ref = ENTITY_CLASS_MAP.computeIfAbsent(new MapperKey(mapperMethod, mapperType, databaseId),
			k -> Ref.of(innerGetEntityClass(mapperMethod, mapperType, databaseId)));
		Class<?> entityClass = ref.get();
		if (entityClass != null) {
			return entityClass;
		}
		throw new IllegalStateException("未知实体类型！");
	}

	@Nullable
	public static Class<?> getEntityClassOrNull(Method mapperMethod, Class<?> mapperType) {
		return getEntityClassOrNull(mapperMethod, mapperType, "");
	}

	@Nullable
	public static Class<?> getEntityClassOrNull(Method mapperMethod, Class<?> mapperType, String databaseId) {
		Ref<Class<?>> ref = ENTITY_CLASS_MAP.computeIfAbsent(new MapperKey(mapperMethod, mapperType, databaseId),
			k -> Ref.of(innerGetEntityClass(mapperMethod, mapperType, databaseId)));
		return ref.get();
	}


	@Nullable
	private static Class<?> innerGetEntityClass(ProviderContext context) {
		return innerGetEntityClass(context.getMapperMethod(), context.getMapperType(), context.getDatabaseId());
	}


	@Nullable
	private static Class<?> innerGetEntityClass(Method mapperMethod, Class<?> mapperType, String databaseId) {
		Class<?> entityClass = null;

		AutoMapper annotation = EnhancedMapperAnnotations.getAutoMapper(databaseId, mapperMethod);
		if (annotation != null) {
			entityClass = annotation.entity();
			if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
				return entityClass;
			}
		}

		{  // 从方法上获取
			MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
			if (declared != null) {
				entityClass = declared.entity();
				if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
					return entityClass;
				}
			}
			// 从Mapper接口类上获取
			declared = mapperType.getAnnotation(MapperEntity.class);
			if (declared != null) {
				entityClass = declared.entity();
				if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
					return entityClass;
				}
			}
			// 从方法所在接口类上获取
			Class<?> declaringClass = mapperMethod.getDeclaringClass();
			if (declaringClass != mapperType) {
				declared = declaringClass.getAnnotation(MapperEntity.class);
				if (declared != null) {
					entityClass = declared.entity();
					if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
						return entityClass;
					}
				}
			}
		}
		// 从基类泛型中获取
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
			if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
				return entityClass;
			}
		}
		return null;
	}


	@Getter
	@EqualsAndHashCode
	@ToString
	public static class MapperKey {
		private final Method mapperMethod;
		private final Class<?> mapperType;
		private final String databaseId;

		public MapperKey(Method mapperMethod, Class<?> mapperType, String databaseId) {
			this.mapperMethod = mapperMethod;
			this.mapperType = mapperType;
			this.databaseId = Strings.trimToEmpty(databaseId);
		}

		public MapperKey(ProviderContext context) {
			this(context.getMapperMethod(), context.getMapperType(), context.getDatabaseId());
		}
	}
}
