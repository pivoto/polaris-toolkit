package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface AnySqlMapper {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	int executeAnySql(@Param(EntityMapperKeys.SQL) SqlNode sqlNode);


}
