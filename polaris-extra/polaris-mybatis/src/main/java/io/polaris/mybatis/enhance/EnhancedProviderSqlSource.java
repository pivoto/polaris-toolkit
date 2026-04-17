package io.polaris.mybatis.enhance;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Methods;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.*;
import io.polaris.mybatis.support.MapperHelperContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
@Slf4j
@SuppressWarnings("SizeReplaceableByIsEmpty")
public class EnhancedProviderSqlSource implements SqlSource {

	@Nonnull
	@Getter
	private final Configuration configuration;
	@Nonnull
	@Getter
	private final LanguageDriver languageDriver;
	@Nonnull
	@Getter
	private final EnhancedProviderContext providerContext;
	@Nullable
	@Getter
	private final BiFunction<Map<String, Object>, EnhancedProviderContext, String> sqlFunction;
	@Nullable
	@Getter
	private final ProviderSqlSource providerSqlSource;


	public EnhancedProviderSqlSource(@Nonnull Configuration configuration, @Nonnull Annotation provider, @Nonnull Class<?> mapperType, @Nonnull Method mapperMethod) {
		this.configuration = configuration;
		Lang lang = mapperMethod.getAnnotation(Lang.class);
		this.languageDriver = configuration.getLanguageDriver(lang == null ? null : lang.value());

		String databaseId = Strings.trimToEmpty(configuration.getDatabaseId());
		if (provider instanceof AutoMapper) {
			Class<?> entityClass = ((AutoMapper) provider).entity();
			if (entityClass != null && entityClass != Object.class && entityClass != void.class) {
				EnhancedAutoMapperHelper.registerEntityClass(mapperType, mapperMethod, databaseId, entityClass);
			}
		}
		BiFunction<Map<String, Object>, EnhancedProviderContext, String> sqlFunction = extractSqlFunction(provider, mapperType, mapperMethod, databaseId);
		this.providerContext = EnhancedProviderContext.of(mapperType, mapperMethod, databaseId);

		// 以下逻辑尽量与 ProviderSqlSource 保持一致
		if (sqlFunction == null) {
			this.sqlFunction = null;
			this.providerSqlSource = new ProviderSqlSource(configuration, provider, mapperType, mapperMethod);
		} else {
			this.sqlFunction = sqlFunction;
			this.providerSqlSource = null;
		}
	}

	@Override
	public BoundSql getBoundSql(Object parameterObject) {
		boolean isMapParam = parameterObject instanceof Map && Maps.isModifiable((Map<?, ?>) parameterObject);
		Map<String, Object> additionalParameters;
		if (isMapParam) {
			// noinspection unchecked
			additionalParameters = (Map<String, Object>) parameterObject;
		} else {
			// 增加扩展
			additionalParameters = MapperHelperContext.bindAdditionalParameters(parameterObject);
			if (parameterObject != null) {
				BeanMap<Object> beanMap = Beans.newBeanMap(parameterObject);
				additionalParameters.putAll(beanMap);
			}
		}
		if (sqlFunction != null) {
			Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
			String sql = sqlFunction.apply(additionalParameters, providerContext);
			SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, parameterType);
			BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
			MapperHelperContext.enhanceBoundSql(boundSql, parameterObject);
			return boundSql;
		}

		assert providerSqlSource != null;
		BoundSql boundSql = providerSqlSource.getBoundSql(parameterObject);
		MapperHelperContext.enhanceBoundSql(boundSql, parameterObject);
		return boundSql;
	}

	@Nullable
	private BiFunction<Map<String, Object>, EnhancedProviderContext, String> extractSqlFunction(Annotation provider, Class<?> mapperType, Method mapperMethod, String databaseId) {
		if (provider instanceof AutoMapper) {
			AutoMapperType autoMapperType = ((AutoMapper) provider).type();
			return autoMapperType.sqlFunction();
		}

		Class<?> providerType = null;
		String providerMethod = null;
		if (provider instanceof SelectProvider) {
			providerType = ((SelectProvider) provider).value();
			if (providerType == null || providerType == void.class || providerType == Object.class) {
				providerType = ((SelectProvider) provider).type();
			}
			providerMethod = ((SelectProvider) provider).method();
		} else if (provider instanceof UpdateProvider) {
			providerType = ((UpdateProvider) provider).value();
			if (providerType == null || providerType == void.class || providerType == Object.class) {
				providerType = ((UpdateProvider) provider).type();
			}
			providerMethod = ((UpdateProvider) provider).method();
		} else if (provider instanceof InsertProvider) {
			providerType = ((InsertProvider) provider).value();
			if (providerType == null || providerType == void.class || providerType == Object.class) {
				providerType = ((InsertProvider) provider).type();
			}
			providerMethod = ((InsertProvider) provider).method();
		} else if (provider instanceof DeleteProvider) {
			providerType = ((DeleteProvider) provider).value();
			if (providerType == null || providerType == void.class || providerType == Object.class) {
				providerType = ((DeleteProvider) provider).type();
			}
			providerMethod = ((DeleteProvider) provider).method();
		}

		BiFunction<Map<String, Object>, EnhancedProviderContext, String> sqlFunction = null;
		if (providerType != null && providerType != void.class && providerType != Object.class) {
			if (Strings.isBlank(providerMethod)) {
				providerMethod = MapperProviderKeys.provideSql;
			}
			sqlFunction = findSqlFunction(providerType, providerMethod);
			if (sqlFunction == null) {
				AutoMapper annotation = EnhancedMapperAnnotations.getAutoMapper(databaseId, mapperMethod);
				if (annotation != null) {
					return annotation.type().sqlFunction();
				}
				// 再次尝试探测并生成 SqlFunction
				sqlFunction = buildSqlFunction(providerType, providerMethod);
				if (sqlFunction == null && !MapperProviderKeys.provideSql.equals(providerMethod)) {
					sqlFunction = buildSqlFunction(providerType, MapperProviderKeys.provideSql);
				}
				if (sqlFunction == null && SqlProvider.class.isAssignableFrom(providerType)) {
					try {
						SqlProvider targetObject = (SqlProvider) providerType.getDeclaredConstructor().newInstance();
						sqlFunction = targetObject::provideSql;
					} catch (Exception ignored) {
					}
				}
			}
		}

		return sqlFunction;
	}

	private BiFunction<Map<String, Object>, EnhancedProviderContext, String> buildSqlFunction(Class<?> providerType, String providerMethod) {

		Method[] targetMethods = new Method[7];

		Method[] methods = providerType.getMethods();
		for (Method method : methods) {
			if (method.getName().equals(providerMethod)
				&& CharSequence.class.isAssignableFrom(method.getReturnType())) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length == 2) {
					if (parameterTypes[0] == Map.class && parameterTypes[1] == EnhancedProviderContext.class) {
						targetMethods[0] = method;
					} else if (parameterTypes[0] == EnhancedProviderContext.class && parameterTypes[1] == Map.class) {
						targetMethods[1] = method;
					} else if (parameterTypes[0] == Object.class && parameterTypes[1] == EnhancedProviderContext.class) {
						targetMethods[2] = method;
					} else if (parameterTypes[0] == EnhancedProviderContext.class && parameterTypes[1] == Object.class) {
						targetMethods[3] = method;
					}
				} else if (parameterTypes.length == 1) {
					if (parameterTypes[0] == Map.class) {
						targetMethods[4] = method;
					} else if (parameterTypes[0] == Object.class) {
						targetMethods[5] = method;
					} else if (parameterTypes[0] == EnhancedProviderContext.class) {
						targetMethods[6] = method;
					}
				}
			}
		}
		try {
			BiFunction<Map<String, Object>, EnhancedProviderContext, Object[]> remapArgs;
			Method targetMethod;
			// (Map, EnhancedProviderContext): CharSequence
			if (targetMethods[0] != null) {
				targetMethod = targetMethods[0];
				remapArgs = null;
			}
			// (EnhancedProviderContext, Map): CharSequence
			else if (targetMethods[1] != null) {
				targetMethod = targetMethods[1];
				remapArgs = (map, ctx) -> new Object[]{ctx, map};
			}
			// (Object, EnhancedProviderContext): CharSequence
			else if (targetMethods[2] != null) {
				targetMethod = targetMethods[2];
				remapArgs = null;
			}
			// (EnhancedProviderContext, Object): CharSequence
			else if (targetMethods[3] != null) {
				targetMethod = targetMethods[3];
				remapArgs = (map, ctx) -> new Object[]{ctx, map};
			}
			// (Map): CharSequence
			else if (targetMethods[4] != null) {
				targetMethod = targetMethods[4];
				remapArgs = (map, ctx) -> new Object[]{map};
			}
			// (Object): CharSequence
			else if (targetMethods[5] != null) {
				targetMethod = targetMethods[5];
				remapArgs = (map, ctx) -> new Object[]{map};
			}
			// (EnhancedProviderContext): CharSequence
			else if (targetMethods[6] != null) {
				targetMethod = targetMethods[6];
				remapArgs = (map, ctx) -> new Object[]{ctx};
			}
			// not found
			else {
				targetMethod = null;
				remapArgs = null;
			}
			if (targetMethod != null) {
				if (Modifier.isStatic(targetMethod.getModifiers())) {
					if (remapArgs == null) {
						return (map, ctx) -> {
							try {
								CharSequence sql = (CharSequence) targetMethod.invoke(null, map, ctx);
								return sql != null ? sql.toString() : null;
							} catch (Exception e) {
								throw new BuilderException("Error invoking SqlProvider method '" + targetMethod
									+ "'.  Cause: " + e, e);
							}
						};
					}
					return (map, ctx) -> {
						try {
							Object[] args = remapArgs.apply(map, ctx);
							CharSequence sql = (CharSequence) targetMethod.invoke(null, args);
							return sql != null ? sql.toString() : null;
						} catch (Exception e) {
							throw new BuilderException("Error invoking SqlProvider method '" + targetMethod
								+ "'.  Cause: " + e, e);
						}
					};
				} else {
					Object targetObject = providerType.getDeclaredConstructor().newInstance();
					if (remapArgs == null) {
						return (map, ctx) -> {
							try {
								CharSequence sql = (CharSequence) targetMethod.invoke(targetObject, map, ctx);
								return sql != null ? sql.toString() : null;
							} catch (Exception e) {
								throw new BuilderException("Error invoking SqlProvider method '" + targetMethod
									+ "'.  Cause: " + e, e);
							}
						};
					}
					return (map, ctx) -> {
						try {
							Object[] args = remapArgs.apply(map, ctx);
							CharSequence sql = (CharSequence) targetMethod.invoke(targetObject, args);
							return sql != null ? sql.toString() : null;
						} catch (Exception e) {
							throw new BuilderException("Error invoking SqlProvider method '" + targetMethod
								+ "'.  Cause: " + e, e);
						}
					};
				}
			}

		} catch (Exception ignored) {
		}

		return null;
	}

	private BiFunction<Map<String, Object>, EnhancedProviderContext, String> findSqlFunction(Class<?> providerType, String providerMethod) {
		if (MapperProviders.class.equals(providerType)) {
			return MapperProviders.getProviderMethod(providerMethod);
		}
		// see MapperProviders.getProviderMethod
		if (AnyEntityProvider.class.equals(providerType)) {
			return AnyEntityProvider::doProvideSql;
		}
		if (AnySqlProvider.class.equals(providerType)) {
			return AutoMapperType.updateAnySql.sqlFunction();
		}
		if (SqlInsertProvider.class.equals(providerType)) {
			return AutoMapperType.insertBySql.sqlFunction();
		}
		if (SqlDeleteProvider.class.equals(providerType)) {
			return AutoMapperType.deleteBySql.sqlFunction();
		}
		if (SqlUpdateProvider.class.equals(providerType)) {
			return AutoMapperType.updateBySql.sqlFunction();
		}
		if (SqlSelectProvider.class.equals(providerType)) {
			return AutoMapperType.selectBySql.sqlFunction();
		}
		if (SqlSelectDirectProvider.class.equals(providerType)) {
			return AutoMapperType.selectBySqlDirect.sqlFunction();
		}
		if (SqlSelectExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.selectBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlCountProvider.class.equals(providerType)) {
			return AutoMapperType.countBySql.sqlFunction();
		}
		if (SqlCountDirectProvider.class.equals(providerType)) {
			return AutoMapperType.countBySqlDirect.sqlFunction();
		}
		if (SqlCountExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.countBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlExistsProvider.class.equals(providerType)) {
			return AutoMapperType.existsBySql.sqlFunction();
		}
		if (SqlExistsDirectProvider.class.equals(providerType)) {
			return AutoMapperType.existsBySqlDirect.sqlFunction();
		}
		if (SqlExistsExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.existsBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlMergeProvider.class.equals(providerType)) {
			return AutoMapperType.mergeBySql.sqlFunction();
		}
		if (EntityInsertProvider.class.equals(providerType)) {
			return AutoMapperType.insertEntity.sqlFunction();
		}

		if (EntityDeleteByIdProvider.class.equals(providerType)) {
			return AutoMapperType.deleteEntityById.sqlFunction();
		}
		if (EntityDeleteDirectByIdProvider.class.equals(providerType)) {
			return AutoMapperType.directDeleteEntityById.sqlFunction();
		}
		if (EntityDeleteLogicByIdProvider.class.equals(providerType)) {
			return AutoMapperType.logicDeleteEntityById.sqlFunction();
		}
		if (EntityDeleteByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.deleteEntityByAny.sqlFunction();
		}
		if (EntityDeleteDirectByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.directDeleteEntityByAny.sqlFunction();
		}
		if (EntityDeleteLogicByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.logicDeleteEntityByAny.sqlFunction();
		}

		if (EntityUpdateByIdProvider.class.equals(providerType)) {
			return AutoMapperType.updateEntityById.sqlFunction();
		}
		if (EntityUpdateByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.updateEntityByAny.sqlFunction();
		}

		if (EntityExistsByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntity.sqlFunction();
		}
		if (EntityExistsByAnyDirectProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntityDirect.sqlFunction();
		}
		if (EntityExistsByAnyExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntityExceptLogicDeleted.sqlFunction();
		}
		if (EntityExistsByIdProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntityById.sqlFunction();
		}
		if (EntityExistsByIdDirectProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntityByIdDirect.sqlFunction();
		}
		if (EntityExistsByIdExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.existsEntityByIdExceptLogicDeleted.sqlFunction();
		}

		if (EntitySelectByAnyProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntity.sqlFunction();
		}
		if (EntitySelectByAnyDirectProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntityDirect.sqlFunction();
		}
		if (EntitySelectByAnyExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntityExceptLogicDeleted.sqlFunction();
		}
		if (EntitySelectByIdProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntityById.sqlFunction();
		}
		if (EntitySelectByIdDirectProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntityByIdDirect.sqlFunction();
		}
		if (EntitySelectByIdExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.selectEntityByIdExceptLogicDeleted.sqlFunction();
		}

		if (EntityCountProvider.class.equals(providerType)) {
			return AutoMapperType.countEntity.sqlFunction();
		}
		if (EntityCountDirectProvider.class.equals(providerType)) {
			return AutoMapperType.countEntityDirect.sqlFunction();
		}
		if (EntityCountExceptLogicDeletedProvider.class.equals(providerType)) {
			return AutoMapperType.countEntityExceptLogicDeleted.sqlFunction();
		}

		return null;
	}


	private static final MethodHandle newProviderContext = find$newProviderContext();

	private static MethodHandle find$newProviderContext() {
		try {
			// ProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId)
			return Methods.findPrivateConstructor(ProviderContext.class, new Class<?>[]{
				Class.class, Method.class, String.class
			});
		} catch (Throwable e) {
			log.error("fail to find ProviderContext.constructor", e);
			return null;
		}
	}

	@Nonnull
	private ProviderContext newProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId) {
		if (newProviderContext == null) {
			throw new IllegalStateException("构造ProviderContext对象失败，请检查Mybatis版本兼容性");
		}
		try {
			return (ProviderContext) newProviderContext.invokeExact(mapperType, mapperMethod, databaseId);
		} catch (Throwable e) {
			log.error("", e);
			throw new IllegalStateException("构造ProviderContext对象失败，请检查Mybatis版本兼容性");
		}
	}

}
