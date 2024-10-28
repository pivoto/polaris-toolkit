package io.polaris.mybatis.mapper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.EntityExistsByAnyExceptLogicDeletedProvider;
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
public interface EntitySelectMapper<E> extends EntityMapper<E> {

	// region normal

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) E entity);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

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


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityById)
	boolean existsById(@Param(BindingKeys.ENTITY) E entity);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntity)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能或分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default boolean exists(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(criteria, includeEmpty, includeEmptyFields, excludeFields);
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

	// endregion normal

	// region except logic deleted

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityByIdExceptLogicDeleted)
	E selectEntityByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) E entity);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityExceptLogicDeleted(E entity, boolean includeEmpty) {
		return selectEntityExceptLogicDeleted(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityExceptLogicDeleted(E entity) {
		return selectEntityExceptLogicDeleted(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, null);
	}

	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectEntityExceptLogicDeletedByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityExceptLogicDeletedByMap(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityExceptLogicDeletedByCriteria(Criteria entity) {
		return selectEntityExceptLogicDeletedByCriteria(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeleted(E entity, boolean includeEmpty) {
		return selectMapExceptLogicDeleted(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeleted(E entity) {
		return selectMapExceptLogicDeleted(entity, false);
	}

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeletedByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectMapExceptLogicDeletedByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapExceptLogicDeletedByMap(entity, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectMapExceptLogicDeletedByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false);
	}


	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityByIdExceptLogicDeleted)
	boolean existsByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) E entity);

	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityExceptLogicDeleted)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能或分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerByAnyExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default boolean existsExceptLogicDeleted(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean existsExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean existsExceptLogicDeleted(E entity, boolean includeEmpty) {
		return existsExceptLogicDeleted(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsExceptLogicDeleted(E entity) {
		return existsExceptLogicDeleted(entity, false);
	}

	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyExceptLogicDeletedProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(entity, includeEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyExceptLogicDeletedProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return existsExceptLogicDeletedByMap(entity, false, includeEmptyFields, null);
	}

	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeEmpty) {
		return existsExceptLogicDeletedByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity) {
		return existsExceptLogicDeletedByMap(entity, false);
	}

	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyExceptLogicDeletedProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(criteria, includeEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			EntityExistsByAnyExceptLogicDeletedProvider.clearQueryExistsByCount();
			IO.close(cursor);
		}
	}

	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return existsExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, boolean includeEmpty) {
		return existsExceptLogicDeletedByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria) {
		return existsExceptLogicDeletedByCriteria(criteria, false);
	}

	// endregion except logic deleted

}
