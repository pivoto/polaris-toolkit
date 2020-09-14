package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntitySelectListMapper<E> extends EntityMapper<E> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityList(@Param(EntityMapperKeys.WHERE) E entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByMap(@Param(EntityMapperKeys.WHERE) Map<String, Object> entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<E> selectEntityListByCriteria(@Param(EntityMapperKeys.WHERE) Criteria criteria
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapList(@Param(EntityMapperKeys.WHERE) E entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByMap(@Param(EntityMapperKeys.WHERE) Map<String, Object> entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	List<Map<String, Object>> selectMapListByCriteria(@Param(EntityMapperKeys.WHERE) Criteria criteria
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);


	default List<E> selectEntityList(E entity) {
		return selectEntityList(entity, null, false);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity) {
		return selectEntityListByMap(entity, null, false);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria) {
		return selectEntityListByCriteria(criteria, null, false);
	}

	default List<Map<String, Object>> selectMapList(E entity) {
		return selectMapList(entity, null, false);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity) {
		return selectMapListByMap(entity, null, false);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria) {
		return selectMapListByCriteria(criteria, null, false);
	}

	default List<E> selectEntityList(E entity, OrderBy orderBy) {
		return selectEntityList(entity, orderBy, false);
	}

	default List<E> selectEntityListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityListByMap(entity, orderBy, false);
	}

	default List<E> selectEntityListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityListByCriteria(criteria, orderBy, false);
	}

	default List<Map<String, Object>> selectMapList(E entity, OrderBy orderBy) {
		return selectMapList(entity, orderBy, false);
	}

	default List<Map<String, Object>> selectMapListByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapListByMap(entity, orderBy, false);
	}

	default List<Map<String, Object>> selectMapListByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapListByCriteria(criteria, orderBy, false);
	}

}
