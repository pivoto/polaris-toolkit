package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface DeleteStatementMapper {

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteBySql)
	int deleteBySql(@Param(BindingKeys.DELETE) DeleteStatement<?> statement);


}
