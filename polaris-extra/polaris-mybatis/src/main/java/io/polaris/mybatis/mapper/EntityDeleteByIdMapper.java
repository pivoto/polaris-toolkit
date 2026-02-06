package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteByIdMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID删除记录，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息
	 * @return 受影响的行数
	 */
	@WithLogicDeleted(true)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityById)
	int deleteEntityById(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 直接根据实体ID物理删除记录，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，包含要删除记录的ID信息
	 * @return 受影响的行数
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityById)
	int directDeleteEntityById(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 根据实体ID进行逻辑删除操作
	 *
	 * @param entity 实体对象，包含要逻辑删除记录的ID信息
	 * @return 受影响的行数
	 */
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityById)
	int logicDeleteEntityById(@Param(BindingKeys.ENTITY) E entity);

}
