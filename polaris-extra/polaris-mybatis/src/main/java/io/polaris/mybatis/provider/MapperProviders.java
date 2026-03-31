package io.polaris.mybatis.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.annotation.Published;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.consts.MapperProviderKeys;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since Aug 24, 2023
 */
@Published
public class MapperProviders {
	static final Map<String, BiFunction<Map<String, Object>, ProviderContext, String>> methods;

	static {
		AutoMapperType[] autoMapperTypes = AutoMapperType.values();
		Map<String, BiFunction<Map<String, Object>, ProviderContext, String>> map = new HashMap<>(autoMapperTypes.length * 2);

		for (AutoMapperType autoMapperType : autoMapperTypes) {
			map.put(autoMapperType.name(), autoMapperType.sqlFunction());
		}
		map.put(MapperProviderKeys.executeAnySql, AutoMapperType.updateAnySql.sqlFunction());
		map.put(MapperProviderKeys.provideSql, AnyEntityProvider::doProvideSql);

		methods = Collections.unmodifiableMap(map);
	}


	public static BiFunction<Map<String, Object>, ProviderContext, String> getProviderMethod(String name) {
		return methods.get(name);
	}

	/**
	 * @see AnyEntityProvider
	 */
	@Published(MapperProviderKeys.provideSql)
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return AnyEntityProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectAny)
	public static String selectAny(Object parameterObject, ProviderContext context) {
		return AnyEntityProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.updateAny)
	public static String updateAny(Object parameterObject, ProviderContext context) {
		return AnyEntityProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.executeAnySql)
	public static String executeAnySql(Object parameterObject, ProviderContext context) {
		return AnySqlProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectAnySql)
	public static String selectAnySql(Object parameterObject, ProviderContext context) {
		return AnySqlProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.updateAnySql)
	public static String updateAnySql(Object parameterObject, ProviderContext context) {
		return AnySqlProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.insertEntity)
	public static String insertEntity(Object parameterObject, ProviderContext context) {
		return EntityInsertProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.deleteEntityById)
	public static String deleteEntityById(Object parameterObject, ProviderContext context) {
		return EntityDeleteByIdProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.directDeleteEntityById)
	public static String directDeleteEntityById(Object parameterObject, ProviderContext context) {
		return EntityDeleteDirectByIdProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.logicDeleteEntityById)
	public static String logicDeleteEntityById(Object parameterObject, ProviderContext context) {
		return EntityDeleteLogicByIdProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.deleteEntityByAny)
	public static String deleteEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityDeleteByAnyProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.directDeleteEntityByAny)
	public static String directDeleteEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityDeleteDirectByAnyProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.logicDeleteEntityByAny)
	public static String logicDeleteEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityDeleteLogicByAnyProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.updateEntityById)
	public static String updateEntityById(Object parameterObject, ProviderContext context) {
		return EntityUpdateByIdProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.updateEntityByAny)
	public static String updateEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityUpdateByAnyProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.countEntity)
	public static String countEntity(Object parameterObject, ProviderContext context) {
		return EntityCountProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.countEntityDirect)
	public static String countEntityDirect(Object parameterObject, ProviderContext context) {
		return EntityCountDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.countEntityExceptLogicDeleted)
	public static String countEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityCountExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntityById)
	public static String existsEntityById(Object parameterObject, ProviderContext context) {
		return EntityExistsByIdProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntityByIdDirect)
	public static String existsEntityByIdDirect(Object parameterObject, ProviderContext context) {
		return EntityExistsByIdDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntityByIdExceptLogicDeleted)
	public static String existsEntityByIdExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityExistsByIdExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntity)
	public static String existsEntity(Object parameterObject, ProviderContext context) {
		return EntityExistsByAnyProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntityDirect)
	public static String existsEntityDirect(Object parameterObject, ProviderContext context) {
		return EntityExistsByAnyDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsEntityExceptLogicDeleted)
	public static String existsEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityExistsByAnyExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.selectEntityById)
	public static String selectEntityById(Object parameterObject, ProviderContext context) {
		return EntitySelectByIdProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectEntityByIdDirect)
	public static String selectEntityByIdDirect(Object parameterObject, ProviderContext context) {
		return EntitySelectByIdDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectEntityByIdExceptLogicDeleted)
	public static String selectEntityByIdExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntitySelectByIdExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.selectEntity)
	public static String selectEntity(Object parameterObject, ProviderContext context) {
		return EntitySelectByAnyProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectEntityDirect)
	public static String selectEntityDirect(Object parameterObject, ProviderContext context) {
		return EntitySelectByAnyDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectEntityExceptLogicDeleted)
	public static String selectEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntitySelectByAnyExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published(MapperProviderKeys.insertBySql)
	public static String insertBySql(Object parameterObject, ProviderContext context) {
		return SqlInsertProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.deleteBySql)
	public static String deleteBySql(Object parameterObject, ProviderContext context) {
		return SqlDeleteProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.updateBySql)
	public static String updateBySql(Object parameterObject, ProviderContext context) {
		return SqlUpdateProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectBySql)
	public static String selectBySql(Object parameterObject, ProviderContext context) {
		return SqlSelectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.countBySql)
	public static String countBySql(Object parameterObject, ProviderContext context) {
		return SqlCountProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsBySql)
	public static String existsBySql(Object parameterObject, ProviderContext context) {
		return SqlExistsProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectBySqlDirect)
	public static String selectBySqlDirect(Object parameterObject, ProviderContext context) {
		return SqlSelectDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.countBySqlDirect)
	public static String countBySqlDirect(Object parameterObject, ProviderContext context) {
		return SqlCountDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsBySqlDirect)
	public static String existsBySqlDirect(Object parameterObject, ProviderContext context) {
		return SqlExistsDirectProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.selectBySqlExceptLogicDeleted)
	public static String selectBySqlExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return SqlSelectExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.countBySqlExceptLogicDeleted)
	public static String countBySqlExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return SqlCountExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.existsBySqlExceptLogicDeleted)
	public static String existsBySqlExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return SqlExistsExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published(MapperProviderKeys.mergeBySql)
	public static String mergeBySql(Object parameterObject, ProviderContext context) {
		return SqlMergeProvider.provideSql(parameterObject, context);
	}

}
