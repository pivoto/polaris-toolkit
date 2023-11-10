package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface AnySqlSelectMapper<R> {

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	R selectObjectByAnySql(@Param(EntityMapperKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	Map<String, Object> selectMapByAnySql(@Param(EntityMapperKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<R> selectObjectListByAnySql(@Param(EntityMapperKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<Map<String, Object>> selectMapListByAnySql(@Param(EntityMapperKeys.SQL) SqlNode sqlNode);


}
