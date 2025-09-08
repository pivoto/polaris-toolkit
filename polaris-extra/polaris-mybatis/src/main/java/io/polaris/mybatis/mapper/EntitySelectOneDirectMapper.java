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
public interface EntitySelectOneDirectMapper<E> extends EntityMapper<E> {

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityByIdDirect)
	E directSelectEntityById(@Param(BindingKeys.ENTITY) E entity);

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E directSelectEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	default E directSelectEntity(E entity, Set<String> includeEmptyFields) {
		return directSelectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default E directSelectEntity(E entity, boolean includeEmpty) {
		return directSelectEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E directSelectEntity(E entity) {
		return directSelectEntity(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E directSelectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default E directSelectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directSelectEntityByMap(entity, false, includeEmptyFields, null);
	}

	default E directSelectEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return directSelectEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E directSelectEntityByMap(Map<String, Object> entity) {
		return directSelectEntityByMap(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E directSelectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default E directSelectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directSelectEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default E directSelectEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return directSelectEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E directSelectEntityByCriteria(Criteria entity) {
		return directSelectEntityByCriteria(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> directSelectMap(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> directSelectMap(E entity, Set<String> includeEmptyFields) {
		return directSelectMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> directSelectMap(E entity, boolean includeEmpty) {
		return directSelectMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> directSelectMap(E entity) {
		return directSelectMap(entity, false);
	}

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directSelectMapByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, boolean includeEmpty) {
		return directSelectMapByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity) {
		return directSelectMapByMap(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directSelectMapByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, boolean includeEmpty) {
		return directSelectMapByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> directSelectMapByCriteria(Criteria criteria) {
		return directSelectMapByCriteria(criteria, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityByIdDirect)
	boolean directExistsById(@Param(BindingKeys.ENTITY) E entity);

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityDirect)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> directExistsInnerByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default boolean directExists(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean directExists(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExists(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean directExists(E entity, Set<String> includeEmptyFields) {
		return directExists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean directExists(E entity, boolean includeEmpty) {
		return directExists(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean directExists(E entity) {
		return directExists(entity, false);
	}

	default boolean directExistsByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean directExistsByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExistsByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean directExistsByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directExistsByMap(entity, false, includeEmptyFields, null);
	}

	default boolean directExistsByMap(Map<String, Object> entity, boolean includeEmpty) {
		return directExistsByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean directExistsByMap(Map<String, Object> entity) {
		return directExistsByMap(entity, false);
	}

	default boolean directExistsByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(criteria, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean directExistsByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExistsByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default boolean directExistsByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directExistsByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean directExistsByCriteria(Criteria criteria, boolean includeEmpty) {
		return directExistsByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean directExistsByCriteria(Criteria criteria) {
		return directExistsByCriteria(criteria, false);
	}

}
