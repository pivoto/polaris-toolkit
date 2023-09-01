package io.polaris.mybatis.mapper;

import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntityDeleteByIdMapper<E> extends EntityMapper<E> {

	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityById)
	int deleteEntityById(@Param(EntityMapperKeys.ENTITY) E entity);

}
