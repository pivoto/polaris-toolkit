package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface MergeStatementMapper {


	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.mergeBySql)
	int mergeBySql(@Param(EntityMapperKeys.MERGE) MergeStatement<?> statement);


}
