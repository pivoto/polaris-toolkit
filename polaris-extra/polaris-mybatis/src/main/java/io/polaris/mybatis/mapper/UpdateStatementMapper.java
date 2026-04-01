package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * UpdateStatementMapper接口定义了基于UpdateStatement的更新操作。
 * 该接口提供了通过SQL语句执行更新操作的方法。
 *
 * @author Qt
 * @since Aug 25, 2023
 */
public interface UpdateStatementMapper {

	/**
	 * 根据提供的UpdateStatement执行更新操作。
	 *
	 * @param statement 包含更新SQL语句和参数的UpdateStatement对象
	 * @return 受影响的行数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateBySql)
	int updateBySql(@Param(BindingKeys.SQL) UpdateStatement<?> statement);

}
