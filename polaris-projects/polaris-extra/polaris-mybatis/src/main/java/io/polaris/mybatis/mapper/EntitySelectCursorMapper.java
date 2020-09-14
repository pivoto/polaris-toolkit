package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.consts.EntityMapperKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since 1.8,  Aug 25, 2023
 */
public interface EntitySelectCursorMapper<E> extends EntityMapper<E> {


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursor(@Param(EntityMapperKeys.WHERE) E entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByMap(@Param(EntityMapperKeys.WHERE) Map<String, Object> entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByCriteria(@Param(EntityMapperKeys.WHERE) Criteria criteria
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursor(@Param(EntityMapperKeys.WHERE) E entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByMap(@Param(EntityMapperKeys.WHERE) Map<String, Object> entity
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByCriteria(@Param(EntityMapperKeys.WHERE) Criteria criteria
		, @Param(EntityMapperKeys.ORDER_BY) OrderBy orderBy
		, @Param(EntityMapperKeys.WHERE_NULLS_INCLUDE) boolean includeWhereNulls);


	default Cursor<E> selectEntityCursor(E entity) {
		return selectEntityCursor(entity, null, false);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity) {
		return selectEntityCursorByMap(entity, null, false);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria) {
		return selectEntityCursorByCriteria(criteria, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity) {
		return selectMapCursor(entity, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity) {
		return selectMapCursorByMap(entity, null, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria) {
		return selectMapCursorByCriteria(criteria, null, false);
	}

	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy) {
		return selectEntityCursor(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorByMap(entity, orderBy, false);
	}

	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorByCriteria(criteria, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy) {
		return selectMapCursor(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorByMap(entity, orderBy, false);
	}

	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorByCriteria(criteria, orderBy, false);
	}

}
