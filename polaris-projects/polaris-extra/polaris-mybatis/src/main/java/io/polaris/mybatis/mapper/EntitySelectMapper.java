package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.Map;

/**
 * @author Qt
 * @since  Aug 25, 2023
 */
public interface EntitySelectMapper<E> extends EntityMapper<E> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) E entity);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeWhereNulls);


	default E selectEntity(E entity) {
		return selectEntity(entity, false);
	}

	default E selectEntityByMap(Map<String, Object> entity) {
		return selectEntityByMap(entity, false);
	}


	default E selectEntityByCriteria(Criteria entity) {
		return selectEntityByCriteria(entity, false);
	}


	default Map<String, Object> selectMap(E entity) {
		return selectMap(entity, false);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity) {
		return selectMapByMap(entity, false);
	}

	default Map<String, Object> selectMapByCriteria(Criteria entity) {
		return selectMapByCriteria(entity, false);
	}
}
