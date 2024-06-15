package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface AnySqlSelectMapper<R> {

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	R selectObjectByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	Map<String, Object> selectMapByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<R> selectObjectListByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<Map<String, Object>> selectMapListByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);


}
