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
 * 通用SQL查询映射器接口，支持通过任意SQL语句进行数据查询操作。
 * 提供了多种返回类型的方法，包括单个对象、Map、对象列表和Map列表。
 *
 * @param <R> 泛型参数，表示查询结果的对象类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface AnySqlSelectMapper<R> {

	/**
	 * 通过任意SQL查询单个对象
	 *
	 * @param sqlNode SQL节点对象，包含要执行的查询SQL语句
	 * @return 查询结果对象
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	R selectObjectByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	/**
	 * 通过任意SQL查询单条记录并返回Map格式
	 *
	 * @param sqlNode SQL节点对象，包含要执行的查询SQL语句
	 * @return 查询结果Map，键为列名，值为对应的值
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	Map<String, Object> selectMapByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	/**
	 * 通过任意SQL查询对象列表
	 *
	 * @param sqlNode SQL节点对象，包含要执行的查询SQL语句
	 * @return 查询结果对象列表
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<R> selectObjectListByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

	/**
	 * 通过任意SQL查询多条记录并返回Map列表
	 *
	 * @param sqlNode SQL节点对象，包含要执行的查询SQL语句
	 * @return 查询结果Map列表，每个Map代表一条记录
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.executeAnySql)
	List<Map<String, Object>> selectMapListByAnySql(@Param(BindingKeys.SQL) SqlNode sqlNode);

}
