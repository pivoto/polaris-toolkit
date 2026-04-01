package io.polaris.mybatis.enhance;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
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
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.provider.*;
import io.polaris.mybatis.support.MapperHelperContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.ParamNameResolver;
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
	private final Configuration configuration;
	@Nonnull
	private final LanguageDriver languageDriver;
	@Nonnull
	private final ProviderContext providerContext;
	@Nullable
	private final BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction;

	@Nonnull
	private final Method mapperMethod;
	@Nullable
	private final Class<?> providerType;

	@Nullable
	private final Method providerMethod;
	@Nullable
	private final String[] providerMethodArgumentNames;
	@Nullable
	private final Class<?>[] providerMethodParameterTypes;
	@Nullable
	private final Integer providerContextIndex;


	public EnhancedProviderSqlSource(@Nonnull Configuration configuration, @Nonnull Annotation provider, @Nonnull Class<?> mapperType, @Nonnull Method mapperMethod) {

		try {
			this.configuration = configuration;
			Lang lang = mapperMethod.getAnnotation(Lang.class);
			this.languageDriver = configuration.getLanguageDriver(lang == null ? null : lang.value());
			this.providerContext = newProviderContext(mapperType, mapperMethod, configuration.getDatabaseId());
		} catch (BuilderException e) {
			throw e;
		} catch (Exception e) {
			throw new BuilderException("Error creating SqlSource for SqlProvider.  Cause: " + e, e);
		}
		BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction = extractSqlFunction(provider);
		this.sqlFunction = sqlFunction;
		this.mapperMethod = mapperMethod;

		// 以下逻辑尽量与 ProviderSqlSource 保持一致
		if (sqlFunction == null) {
			String candidateProviderMethodName;
			Method candidateProviderMethod = null;
			try {
				this.providerType = getProviderType(configuration, provider, mapperMethod);
				candidateProviderMethodName = (String) provider.annotationType().getMethod("method").invoke(provider);

				if (candidateProviderMethodName.length() == 0 && ProviderMethodResolver.class.isAssignableFrom(this.providerType)) {
					candidateProviderMethod = ((ProviderMethodResolver) this.providerType.getDeclaredConstructor().newInstance())
						.resolveMethod(this.providerContext);
				}
				if (candidateProviderMethod == null) {
					candidateProviderMethodName = candidateProviderMethodName.length() == 0 ? "provideSql" : candidateProviderMethodName;
					for (Method m : this.providerType.getMethods()) {
						if (candidateProviderMethodName.equals(m.getName()) && CharSequence.class.isAssignableFrom(m.getReturnType())) {
							if (candidateProviderMethod != null) {
								throw new BuilderException("Error creating SqlSource for SqlProvider. Method '"
									+ candidateProviderMethodName + "' is found multiple in SqlProvider '" + this.providerType.getName()
									+ "'. Sql provider method can not overload.");
							}
							candidateProviderMethod = m;
						}
					}
				}
			} catch (BuilderException e) {
				throw e;
			} catch (Exception e) {
				throw new BuilderException("Error creating SqlSource for SqlProvider.  Cause: " + e, e);
			}
			if (candidateProviderMethod == null) {
				throw new BuilderException("Error creating SqlSource for SqlProvider. Method '"
					+ candidateProviderMethodName + "' not found in SqlProvider '" + this.providerType.getName() + "'.");
			}
			this.providerMethod = candidateProviderMethod;
			this.providerMethodArgumentNames = new ParamNameResolver(configuration, this.providerMethod).getNames();
			this.providerMethodParameterTypes = this.providerMethod.getParameterTypes();

			Integer candidateProviderContextIndex = null;
			for (int i = 0; i < this.providerMethodParameterTypes.length; i++) {
				Class<?> parameterType = this.providerMethodParameterTypes[i];
				if (parameterType == ProviderContext.class) {
					if (candidateProviderContextIndex != null) {
						throw new BuilderException("Error creating SqlSource for SqlProvider. Method '"
							+ candidateProviderMethodName + "' is found multiple in SqlProvider '" + this.providerType.getName()
							+ "'. Sql provider method can not overload.");
					}
					candidateProviderContextIndex = i;
				}
			}
			this.providerContextIndex = candidateProviderContextIndex;
		} else {
			this.providerType = null;
			this.providerMethod = null;
			this.providerMethodArgumentNames = null;
			this.providerMethodParameterTypes = null;
			this.providerContextIndex = null;
		}

	}

	@Nullable
	private BiFunction<Map<String, Object>, ProviderContext, String> extractSqlFunction(Annotation provider) {
		BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction = null;
		if (provider instanceof AutoMapper) {
			AutoMapperType autoMapperType = ((AutoMapper) provider).type();
			Class<?> entityClass = ((AutoMapper) provider).entity();
			EnhancedAutoMapperHelper.registerEntityClass(providerContext, entityClass);
			sqlFunction = autoMapperType.sqlFunction();
		} else if (provider instanceof SelectProvider) {
			Class<?> providerClass = ((SelectProvider) provider).value();
			if (providerClass == null || providerClass == void.class || providerClass == Object.class) {
				providerClass = ((SelectProvider) provider).type();
			}
			sqlFunction = findSqlFunction(providerClass, ((SelectProvider) provider).method());
		} else if (provider instanceof UpdateProvider) {
			Class<?> providerClass = ((UpdateProvider) provider).value();
			if (providerClass == null || providerClass == void.class || providerClass == Object.class) {
				providerClass = ((UpdateProvider) provider).type();
			}
			sqlFunction = findSqlFunction(providerClass, ((UpdateProvider) provider).method());
		} else if (provider instanceof InsertProvider) {
			Class<?> providerClass = ((InsertProvider) provider).value();
			if (providerClass == null || providerClass == void.class || providerClass == Object.class) {
				providerClass = ((InsertProvider) provider).type();
			}
			sqlFunction = findSqlFunction(providerClass, ((InsertProvider) provider).method());
		} else if (provider instanceof DeleteProvider) {
			Class<?> providerClass = ((DeleteProvider) provider).value();
			if (providerClass == null || providerClass == void.class || providerClass == Object.class) {
				providerClass = ((DeleteProvider) provider).type();
			}
			sqlFunction = findSqlFunction(providerClass, ((DeleteProvider) provider).method());
		}
		return sqlFunction;
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

		SqlSource sqlSource = createSqlSource(parameterObject);
		BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		MapperHelperContext.enhanceBoundSql(boundSql, parameterObject);
		return boundSql;
	}

	private BiFunction<Map<String, Object>, ProviderContext, String> findSqlFunction(Class<?> providerClass, String providerMethod) {
		if (MapperProviders.class.equals(providerClass)) {
			return MapperProviders.getProviderMethod(providerMethod);
		}
		// see MapperProviders.getProviderMethod
		if (AnyEntityProvider.class.equals(providerClass)) {
			return AnyEntityProvider::doProvideSql;
		}
		if (AnySqlProvider.class.equals(providerClass)) {
			return AutoMapperType.updateAnySql.sqlFunction();
		}
		if (SqlInsertProvider.class.equals(providerClass)) {
			return AutoMapperType.insertBySql.sqlFunction();
		}
		if (SqlDeleteProvider.class.equals(providerClass)) {
			return AutoMapperType.deleteBySql.sqlFunction();
		}
		if (SqlUpdateProvider.class.equals(providerClass)) {
			return AutoMapperType.updateBySql.sqlFunction();
		}
		if (SqlSelectProvider.class.equals(providerClass)) {
			return AutoMapperType.selectBySql.sqlFunction();
		}
		if (SqlSelectDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.selectBySqlDirect.sqlFunction();
		}
		if (SqlSelectExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.selectBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlCountProvider.class.equals(providerClass)) {
			return AutoMapperType.countBySql.sqlFunction();
		}
		if (SqlCountDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.countBySqlDirect.sqlFunction();
		}
		if (SqlCountExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.countBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlExistsProvider.class.equals(providerClass)) {
			return AutoMapperType.existsBySql.sqlFunction();
		}
		if (SqlExistsDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.existsBySqlDirect.sqlFunction();
		}
		if (SqlExistsExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.existsBySqlExceptLogicDeleted.sqlFunction();
		}

		if (SqlMergeProvider.class.equals(providerClass)) {
			return AutoMapperType.mergeBySql.sqlFunction();
		}
		if (EntityInsertProvider.class.equals(providerClass)) {
			return AutoMapperType.insertEntity.sqlFunction();
		}

		if (EntityDeleteByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.deleteEntityById.sqlFunction();
		}
		if (EntityDeleteDirectByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.directDeleteEntityById.sqlFunction();
		}
		if (EntityDeleteLogicByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.logicDeleteEntityById.sqlFunction();
		}
		if (EntityDeleteByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.deleteEntityByAny.sqlFunction();
		}
		if (EntityDeleteDirectByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.directDeleteEntityByAny.sqlFunction();
		}
		if (EntityDeleteLogicByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.logicDeleteEntityByAny.sqlFunction();
		}

		if (EntityUpdateByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.updateEntityById.sqlFunction();
		}
		if (EntityUpdateByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.updateEntityByAny.sqlFunction();
		}

		if (EntityExistsByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntity.sqlFunction();
		}
		if (EntityExistsByAnyDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntityDirect.sqlFunction();
		}
		if (EntityExistsByAnyExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntityExceptLogicDeleted.sqlFunction();
		}
		if (EntityExistsByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntityById.sqlFunction();
		}
		if (EntityExistsByIdDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntityByIdDirect.sqlFunction();
		}
		if (EntityExistsByIdExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.existsEntityByIdExceptLogicDeleted.sqlFunction();
		}

		if (EntitySelectByAnyProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntity.sqlFunction();
		}
		if (EntitySelectByAnyDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntityDirect.sqlFunction();
		}
		if (EntitySelectByAnyExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntityExceptLogicDeleted.sqlFunction();
		}
		if (EntitySelectByIdProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntityById.sqlFunction();
		}
		if (EntitySelectByIdDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntityByIdDirect.sqlFunction();
		}
		if (EntitySelectByIdExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.selectEntityByIdExceptLogicDeleted.sqlFunction();
		}

		if (EntityCountProvider.class.equals(providerClass)) {
			return AutoMapperType.countEntity.sqlFunction();
		}
		if (EntityCountDirectProvider.class.equals(providerClass)) {
			return AutoMapperType.countEntityDirect.sqlFunction();
		}
		if (EntityCountExceptLogicDeletedProvider.class.equals(providerClass)) {
			return AutoMapperType.countEntityExceptLogicDeleted.sqlFunction();
		}

		return null;
	}


	private SqlSource createSqlSource(Object parameterObject) {
		try {
			String sql;
			if (parameterObject instanceof Map) {
				int bindParameterCount = providerMethodParameterTypes.length - (providerContext == null ? 0 : 1);
				if (bindParameterCount == 1
					&& providerMethodParameterTypes[Integer.valueOf(0).equals(providerContextIndex) ? 1 : 0].isAssignableFrom(parameterObject.getClass())) {
					sql = invokeProviderMethod(extractProviderMethodArguments(parameterObject));
				} else {
					@SuppressWarnings("unchecked")
					Map<String, Object> params = (Map<String, Object>) parameterObject;
					sql = invokeProviderMethod(extractProviderMethodArguments(params, providerMethodArgumentNames));
				}
			} else if (providerMethodParameterTypes.length == 0) {
				sql = invokeProviderMethod();
			} else if (providerMethodParameterTypes.length == 1) {
				if (providerContext == null) {
					sql = invokeProviderMethod(parameterObject);
				} else {
					sql = invokeProviderMethod(providerContext);
				}
			} else if (providerMethodParameterTypes.length == 2) {
				sql = invokeProviderMethod(extractProviderMethodArguments(parameterObject));
			} else {
				throw new BuilderException("Cannot invoke SqlProvider method '" + providerMethod
					+ "' with specify parameter '" + (parameterObject == null ? null : parameterObject.getClass())
					+ "' because SqlProvider method arguments for '" + mapperMethod + "' is an invalid combination.");
			}
			Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
			return languageDriver.createSqlSource(configuration, sql, parameterType);
		} catch (BuilderException e) {
			throw e;
		} catch (Exception e) {
			throw new BuilderException("Error invoking SqlProvider method '" + providerMethod
				+ "' with specify parameter '" + (parameterObject == null ? null : parameterObject.getClass()) + "'.  Cause: " + extractRootCause(e), e);
		}
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

	private Throwable extractRootCause(Exception e) {
		Throwable cause = e;
		while (cause.getCause() != null) {
			cause = cause.getCause();
		}
		return cause;
	}

	private Object[] extractProviderMethodArguments(Object parameterObject) {
		if (providerContext != null) {
			Object[] args = new Object[2];
			args[providerContextIndex == 0 ? 1 : 0] = parameterObject;
			args[providerContextIndex] = providerContext;
			return args;
		} else {
			return new Object[]{parameterObject};
		}
	}

	private Object[] extractProviderMethodArguments(Map<String, Object> params, String[] argumentNames) {
		Object[] args = new Object[argumentNames.length];
		for (int i = 0; i < args.length; i++) {
			if (providerContextIndex != null && providerContextIndex == i) {
				args[i] = providerContext;
			} else {
				args[i] = params.get(argumentNames[i]);
			}
		}
		return args;
	}

	private String invokeProviderMethod(Object... args) throws Exception {
		Object targetObject = null;
		if (!Modifier.isStatic(providerMethod.getModifiers())) {
			targetObject = providerType.getDeclaredConstructor().newInstance();
		}
		CharSequence sql = (CharSequence) providerMethod.invoke(targetObject, args);
		return sql != null ? sql.toString() : null;
	}

	private Class<?> getProviderType(Configuration configuration, Annotation providerAnnotation, Method mapperMethod)
		throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Class<?> type = (Class<?>) providerAnnotation.annotationType().getMethod("type").invoke(providerAnnotation);
		Class<?> value = (Class<?>) providerAnnotation.annotationType().getMethod("value").invoke(providerAnnotation);
		if (value == void.class && type == void.class) {
			if (configuration.getDefaultSqlProviderType() != null) {
				return configuration.getDefaultSqlProviderType();
			}
			throw new BuilderException("Please specify either 'value' or 'type' attribute of @"
				+ providerAnnotation.annotationType().getSimpleName()
				+ " at the '" + mapperMethod.toString() + "'.");
		}
		if (value != void.class && type != void.class && value != type) {
			throw new BuilderException("Cannot specify different class on 'value' and 'type' attribute of @"
				+ providerAnnotation.annotationType().getSimpleName()
				+ " at the '" + mapperMethod.toString() + "'.");
		}
		return value == void.class ? type : value;
	}

}
