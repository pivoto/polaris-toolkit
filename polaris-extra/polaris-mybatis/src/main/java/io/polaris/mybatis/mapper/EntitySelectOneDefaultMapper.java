package io.polaris.mybatis.mapper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.EntityExistsByAnyProvider;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectOneDefaultMapper<E> extends EntityMapper<E> {

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default E selectEntityById(E entity) {
		return selectEntityById(entity, null);
	}

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default E selectEntity(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default E selectEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntity(E entity, Set<String> includeEmptyFields) {
		return selectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntity(E entity, boolean includeEmpty) {
		return selectEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntity(E entity) {
		return selectEntity(entity, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default E selectEntityByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, null);
	}

	default E selectEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityByMap(Map<String, Object> entity) {
		return selectEntityByMap(entity, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default E selectEntityByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByCriteria(criteria, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityByCriteria(Criteria entity) {
		return selectEntityByCriteria(entity, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Map<String, Object> selectMap(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields) {
		return selectMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMap(E entity, boolean includeEmpty) {
		return selectMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMap(E entity) {
		return selectMap(entity, false);
	}

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectMapByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapByMap(Map<String, Object> entity) {
		return selectMapByMap(entity, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default Map<String, Object> selectMapByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByCriteria(criteria, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectMapByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapByCriteria(Criteria criteria) {
		return selectMapByCriteria(criteria, false);
	}


	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityById)
	boolean existsById(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default boolean existsById(E entity) {
		return existsById(entity, null);
	}

	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntity)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能或分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	default boolean exists(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean exists(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default boolean exists(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean exists(E entity, Set<String> includeEmptyFields) {
		return exists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean exists(E entity, boolean includeEmpty) {
		return exists(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean exists(E entity) {
		return exists(entity, false);
	}

	default boolean existsByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean existsByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default boolean existsByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean existsByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return existsByMap(entity, false, includeEmptyFields, null);
	}

	default boolean existsByMap(Map<String, Object> entity, boolean includeEmpty) {
		return existsByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsByMap(Map<String, Object> entity) {
		return existsByMap(entity, false);
	}

	default boolean existsByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(criteria, includeEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean existsByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByCriteria(criteria, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	default boolean existsByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default boolean existsByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return existsByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean existsByCriteria(Criteria criteria, boolean includeEmpty) {
		return existsByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsByCriteria(Criteria criteria) {
		return existsByCriteria(criteria, false);
	}

}
