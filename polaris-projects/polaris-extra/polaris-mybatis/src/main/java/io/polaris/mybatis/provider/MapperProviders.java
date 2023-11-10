package io.polaris.mybatis.provider;

import io.polaris.core.annotation.Published;
import io.polaris.mybatis.consts.MapperProviderKeys;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Qt
 * @since 1.8,  Aug 24, 2023
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


	public static BiFunction<Map<String, Object>, ProviderContext, String> getProviderMethod(String name) {
		return methods.get(name);
	}


	@Published
	public static String executeAnySql(Map<String, Object> map, ProviderContext context) {
		return AnySqlProvider.provideSql(map, context);
	}

	@Published
	public static String insertEntity(Map<String, Object> map, ProviderContext context) {
		return EntityInsertProvider.provideSql(map, context);
	}


	@Published
	public static String deleteEntityById(Map<String, Object> map, ProviderContext context) {
		return EntityDeleteByIdProvider.provideSql(map, context);
	}


	@Published
	public static String deleteEntityByAny(Map<String, Object> map, ProviderContext context) {
		return EntityDeleteByAnyProvider.provideSql(map, context);
	}


	@Published
	public static String updateEntityById(Map<String, Object> map, ProviderContext context) {
		return EntityUpdateByIdProvider.provideSql(map, context);
	}


	@Published
	public static String updateEntityByAny(Map<String, Object> map, ProviderContext context) {
		return EntityUpdateByAnyProvider.provideSql(map, context);
	}


	@Published
	public static String countEntity(Map<String, Object> map, ProviderContext context) {
		return EntityCountProvider.provideSql(map, context);
	}


	@Published
	public static String selectEntityById(Map<String, Object> map, ProviderContext context) {
		return EntitySelectByIdProvider.provideSql(map, context);
	}


	@Published
	public static String selectEntity(Map<String, Object> map, ProviderContext context) {
		return EntitySelectByAnyProvider.provideSql(map, context);
	}


	@Published
	public static String insertBySql(Map<String, Object> map, ProviderContext context) {
		return SqlInsertProvider.provideSql(map, context);
	}

	@Published
	public static String deleteBySql(Map<String, Object> map, ProviderContext context) {
		return SqlDeleteProvider.provideSql(map, context);
	}

	@Published
	public static String updateBySql(Map<String, Object> map, ProviderContext context) {
		return SqlUpdateProvider.provideSql(map, context);
	}

	@Published
	public static String selectBySql(Map<String, Object> map, ProviderContext context) {
		return SqlSelectProvider.provideSql(map, context);
	}

	@Published
	public static String countBySql(Map<String, Object> map, ProviderContext context) {
		return SqlCountProvider.provideSql(map, context);
	}

	@Published
	public static String mergeBySql(Map<String, Object> map, ProviderContext context) {
		return SqlMergeProvider.provideSql(map, context);
	}

}
