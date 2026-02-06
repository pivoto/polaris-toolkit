package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 通用SQL执行接口，提供执行任意SQL语句的能力
 *
 * @author Qt
 * @since Aug 25, 2023
 */
public interface AnySqlMapper {


	/**
	 * 执行任意SQL语句
	 *
	 * @param sqlNode SQL节点对象，包含要执行的SQL语句信息
	 * @return 受影响的行数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	int executeAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);


}
