package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface AnySqlMapper {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	int executeAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);


}
