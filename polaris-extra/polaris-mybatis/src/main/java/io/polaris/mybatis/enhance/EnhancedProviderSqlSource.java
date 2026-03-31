package io.polaris.mybatis.enhance;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
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
public class EnhancedProviderSqlSource extends ProviderSqlSource implements SqlSource {

	@Nonnull
	private final Configuration configuration;
	@Nonnull
	private final LanguageDriver languageDriver;
	@Nonnull
	private final ProviderContext providerContext;
	@Nullable
	private final BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction;

	public EnhancedProviderSqlSource(Configuration configuration, Annotation provider, Class<?> mapperType, Method mapperMethod) {
		super(configuration, provider, mapperType, mapperMethod);
		this.configuration = configuration;
		Lang lang = mapperMethod == null ? null : mapperMethod.getAnnotation(Lang.class);
		this.languageDriver = configuration.getLanguageDriver(lang == null ? null : lang.value());
		this.providerContext = newProviderContext(mapperType, mapperMethod, configuration.getDatabaseId());

		BiFunction<Map<String, Object>, ProviderContext, String> sqlFunction = null;
		if (provider instanceof SelectProvider) {
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
		} else {
			if (provider instanceof AutoMapper) {
				AutoMapperType autoMapperType = ((AutoMapper) provider).type();
				Class<?> entityClass = ((AutoMapper) provider).entity();
				EnhancedAutoMapperHelper.registerEntityClass(providerContext, entityClass);
				sqlFunction = autoMapperType.sqlFunction();
			}
		}
		this.sqlFunction = sqlFunction;
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

		BoundSql boundSql = super.getBoundSql(parameterObject);
		MapperHelperContext.enhanceBoundSql(boundSql, parameterObject);
		return boundSql;
	}


	@Nonnull
	private ProviderContext newProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId) {
		try {
			// ProviderContext(Class<?> mapperType, Method mapperMethod, String databaseId)
			MethodHandle constructor = Methods.findPrivateConstructor(ProviderContext.class, new Class<?>[]{
				Class.class, Method.class, String.class
			});
			return (ProviderContext) constructor.invokeExact(mapperType, mapperMethod, databaseId);
		} catch (Throwable e) {
			log.error("", e);
			throw new IllegalStateException("构造ProviderContext对象失败，请检查Mybatis版本兼容性");
		}
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

}
