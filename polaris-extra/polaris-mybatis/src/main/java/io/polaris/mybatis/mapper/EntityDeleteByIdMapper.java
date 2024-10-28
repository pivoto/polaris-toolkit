package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteByIdMapper<E> extends EntityMapper<E> {

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityById)
	int deleteEntityById(@Param(BindingKeys.ENTITY) E entity);

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityById)
	int logicDeleteEntityById(@Param(BindingKeys.ENTITY) E entity);

}
