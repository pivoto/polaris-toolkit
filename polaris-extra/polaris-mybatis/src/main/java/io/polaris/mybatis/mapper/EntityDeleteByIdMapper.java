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

	@WithLogicDeleted(true)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityById)
	int deleteEntityById(@Param(BindingKeys.ENTITY) E entity);

	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityById)
	int directDeleteEntityById(@Param(BindingKeys.ENTITY) E entity);

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityById)
	int logicDeleteEntityById(@Param(BindingKeys.ENTITY) E entity);

}
