package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.*;

/**
 * InsertStatementMapper 是一个 MyBatis 映射器接口，用于执行插入操作。
 * 该接口定义了通过 SQL 语句进行数据插入的方法。
 *
 * @author Qt
 * @since Aug 25, 2023
 */
public interface InsertStatementMapper {

	/**
	 * 根据提供的 InsertStatement 对象执行插入操作。
	 *
	 * @param statement 包含插入语句信息的对象
	 * @return 插入操作影响的行数
	 */
	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertBySql)
	int insertBySql(@Param(BindingKeys.INSERT) InsertStatement<?> statement);

}
