package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.*;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface InsertStatementMapper {

	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertBySql)
	int insertBySql(@Param(EntityMapperKeys.INSERT) InsertStatement<?> statement);


}
