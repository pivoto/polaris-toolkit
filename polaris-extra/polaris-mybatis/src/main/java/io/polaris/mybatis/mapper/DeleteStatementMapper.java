package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * 删除语句映射器接口，提供基于SQL删除数据的方法。
 * <p>
 * 该接口定义了使用MyBatis注解方式执行删除操作的规范，
 * 通过{@link DeleteProvider}指定具体的SQL提供者类和方法。
 *
 * @author Qt
 * @since Aug 25, 2023
 */
public interface DeleteStatementMapper {

	/**
	 * 根据提供的删除语句执行删除操作。
	 *
	 * @param statement 包含删除条件和表信息的DeleteStatement对象
	 * @return 受影响的行数
	 */
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteBySql)
	int deleteBySql(@Param(BindingKeys.DELETE) DeleteStatement<?> statement);

}
