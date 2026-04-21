package io.polaris.mybatis.enhance.mapper;

import java.lang.reflect.Type;

import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntityDeleteByIdAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID删除记录，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息，可以是Bean或Map等对象
	 * @param supportLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.deleteEntityById)
	int deleteEntityById(@Param(BindingKeys.ENTITY) Object entity
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);


	/**
	 * 直接根据实体ID物理删除记录，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	default int deleteEntityById(Object entity) {
		return deleteEntityById(entity, true);
	}

	/**
	 * 直接根据实体ID物理删除记录，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.directDeleteEntityById)
	int directDeleteEntityById(@Param(BindingKeys.ENTITY) Object entity);

	/**
	 * 根据实体ID进行逻辑删除操作
	 *
	 * @param entity 实体对象，包含要逻辑删除记录的ID信息，可以是Bean或Map等对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.logicDeleteEntityById)
	int logicDeleteEntityById(@Param(BindingKeys.ENTITY) Object entity);

}
