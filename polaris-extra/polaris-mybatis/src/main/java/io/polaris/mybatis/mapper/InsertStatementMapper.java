package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.*;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface InsertStatementMapper {

	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertBySql)
	int insertBySql(@Param(BindingKeys.INSERT) InsertStatement<?> statement);


}
