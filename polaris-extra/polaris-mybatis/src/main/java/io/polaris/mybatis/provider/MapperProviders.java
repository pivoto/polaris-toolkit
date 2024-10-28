package io.polaris.mybatis.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import io.polaris.core.annotation.Published;
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
		Map<String, BiFunction<Map<String, Object>, ProviderContext, String>> map = new HashMap<>();

		map.put(MapperProviderKeys.executeAnySql, MapperProviders::executeAnySql);

		map.put(MapperProviderKeys.insertBySql, MapperProviders::insertBySql);
		map.put(MapperProviderKeys.deleteBySql, MapperProviders::deleteBySql);
		map.put(MapperProviderKeys.updateBySql, MapperProviders::updateBySql);
		map.put(MapperProviderKeys.selectBySql, MapperProviders::selectBySql);
		map.put(MapperProviderKeys.countBySql, MapperProviders::countBySql);
		map.put(MapperProviderKeys.existsBySql, MapperProviders::existsBySql);
		map.put(MapperProviderKeys.mergeBySql, MapperProviders::mergeBySql);

		map.put(MapperProviderKeys.insertEntity, MapperProviders::insertEntity);

		map.put(MapperProviderKeys.deleteEntityById, MapperProviders::deleteEntityById);
		map.put(MapperProviderKeys.deleteEntityByAny, MapperProviders::deleteEntityByAny);
		map.put(MapperProviderKeys.logicDeleteEntityById, MapperProviders::logicDeleteEntityById);
		map.put(MapperProviderKeys.logicDeleteEntityByAny, MapperProviders::logicDeleteEntityByAny);

		map.put(MapperProviderKeys.updateEntityById, MapperProviders::updateEntityById);
		map.put(MapperProviderKeys.updateEntityByAny, MapperProviders::updateEntityByAny);

		map.put(MapperProviderKeys.existsEntity, MapperProviders::existsEntity);
		map.put(MapperProviderKeys.existsEntityExceptLogicDeleted, MapperProviders::existsEntityExceptLogicDeleted);
		map.put(MapperProviderKeys.existsEntityById, MapperProviders::existsEntityById);
		map.put(MapperProviderKeys.existsEntityByIdExceptLogicDeleted, MapperProviders::existsEntityByIdExceptLogicDeleted);

		map.put(MapperProviderKeys.selectEntity, MapperProviders::selectEntity);
		map.put(MapperProviderKeys.selectEntityExceptLogicDeleted, MapperProviders::selectEntityExceptLogicDeleted);
		map.put(MapperProviderKeys.selectEntityById, MapperProviders::selectEntityById);
		map.put(MapperProviderKeys.selectEntityByIdExceptLogicDeleted, MapperProviders::selectEntityByIdExceptLogicDeleted);

		map.put(MapperProviderKeys.countEntity, MapperProviders::countEntity);
		map.put(MapperProviderKeys.countEntityExceptLogicDeleted, MapperProviders::countEntityExceptLogicDeleted);


		methods = Collections.unmodifiableMap(map);
	}


	public static BiFunction<Map<String, Object>, ProviderContext, String> getProviderMethod(String name) {
		return methods.get(name);
	}

	/**
	 * @see AnyEntityProvider
	 */
	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return AnyEntityProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String executeAnySql(Object parameterObject, ProviderContext context) {
		return AnySqlProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String insertEntity(Object parameterObject, ProviderContext context) {
		return EntityInsertProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String deleteEntityById(Object parameterObject, ProviderContext context) {
		return EntityDeleteByIdProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String deleteEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityDeleteByAnyProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String logicDeleteEntityById(Object parameterObject, ProviderContext context) {
		return EntityLogicDeleteByIdProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String logicDeleteEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityLogicDeleteByAnyProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String updateEntityById(Object parameterObject, ProviderContext context) {
		return EntityUpdateByIdProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String updateEntityByAny(Object parameterObject, ProviderContext context) {
		return EntityUpdateByAnyProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String countEntity(Object parameterObject, ProviderContext context) {
		return EntityCountProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String countEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityCountExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String existsEntityById(Object parameterObject, ProviderContext context) {
		return EntityExistsByIdProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String existsEntityByIdExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityExistsByIdExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String existsEntity(Object parameterObject, ProviderContext context) {
		return EntityExistsByAnyProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String existsEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntityExistsByAnyExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String selectEntityById(Object parameterObject, ProviderContext context) {
		return EntitySelectByIdProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String selectEntityByIdExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntitySelectByIdExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String selectEntity(Object parameterObject, ProviderContext context) {
		return EntitySelectByAnyProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String selectEntityExceptLogicDeleted(Object parameterObject, ProviderContext context) {
		return EntitySelectByAnyExceptLogicDeletedProvider.provideSql(parameterObject, context);
	}


	@Published
	public static String insertBySql(Object parameterObject, ProviderContext context) {
		return SqlInsertProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String deleteBySql(Object parameterObject, ProviderContext context) {
		return SqlDeleteProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String updateBySql(Object parameterObject, ProviderContext context) {
		return SqlUpdateProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String selectBySql(Object parameterObject, ProviderContext context) {
		return SqlSelectProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String countBySql(Object parameterObject, ProviderContext context) {
		return SqlCountProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String existsBySql(Object parameterObject, ProviderContext context) {
		return SqlExistsProvider.provideSql(parameterObject, context);
	}

	@Published
	public static String mergeBySql(Object parameterObject, ProviderContext context) {
		return SqlMergeProvider.provideSql(parameterObject, context);
	}

}
