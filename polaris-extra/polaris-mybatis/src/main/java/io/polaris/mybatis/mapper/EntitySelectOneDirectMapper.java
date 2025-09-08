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
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirectById)
	E selectEntityDirectById(@Param(BindingKeys.ENTITY) E entity);

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E selectEntityDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityDirect(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityDirect(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityDirect(E entity, Set<String> includeEmptyFields) {
		return selectEntityDirect(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityDirect(E entity, boolean includeEmpty) {
		return selectEntityDirect(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityDirect(E entity) {
		return selectEntityDirect(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E selectEntityDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityDirectByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityDirectByMap(entity, false, includeEmptyFields, null);
	}

	default E selectEntityDirectByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectEntityDirectByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityDirectByMap(Map<String, Object> entity) {
		return selectEntityDirectByMap(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E selectEntityDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default E selectEntityDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityDirectByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default E selectEntityDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityDirectByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default E selectEntityDirectByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectEntityDirectByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default E selectEntityDirectByCriteria(Criteria entity) {
		return selectEntityDirectByCriteria(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> selectMapDirect(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapDirect(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapDirect(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapDirect(E entity, Set<String> includeEmptyFields) {
		return selectMapDirect(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirect(E entity, boolean includeEmpty) {
		return selectMapDirect(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirect(E entity) {
		return selectMapDirect(entity, false);
	}

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> selectMapDirectByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapDirectByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapDirectByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirectByMap(Map<String, Object> entity, boolean includeEmpty) {
		return selectMapDirectByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirectByMap(Map<String, Object> entity) {
		return selectMapDirectByMap(entity, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> selectMapDirectByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default Map<String, Object> selectMapDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapDirectByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default Map<String, Object> selectMapDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapDirectByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirectByCriteria(Criteria criteria, boolean includeEmpty) {
		return selectMapDirectByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default Map<String, Object> selectMapDirectByCriteria(Criteria criteria) {
		return selectMapDirectByCriteria(criteria, false);
	}


	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityDirectById)
	boolean existsDirectById(@Param(BindingKeys.ENTITY) E entity);

	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityDirect)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能或分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerDirectByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	default boolean existsDirect(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerDirectByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsDirect(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsDirect(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean existsDirect(E entity, Set<String> includeEmptyFields) {
		return existsDirect(entity, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean existsDirect(E entity, boolean includeEmpty) {
		return existsDirect(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsDirect(E entity) {
		return existsDirect(entity, false);
	}

	default boolean existsDirectByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerDirectByAny(entity, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsDirectByMap(entity, false, includeEmptyFields, excludeFields);
	}

	default boolean existsDirectByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return existsDirectByMap(entity, false, includeEmptyFields, null);
	}

	default boolean existsDirectByMap(Map<String, Object> entity, boolean includeEmpty) {
		return existsDirectByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsDirectByMap(Map<String, Object> entity) {
		return existsDirectByMap(entity, false);
	}

	default boolean existsDirectByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		EntityExistsByAnyProvider.setQueryExistsByCount(false);
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerDirectByAny(criteria, includeEmpty, includeEmptyFields, excludeFields);
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

	default boolean existsDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsDirectByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	default boolean existsDirectByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return existsDirectByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	default boolean existsDirectByCriteria(Criteria criteria, boolean includeEmpty) {
		return existsDirectByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	default boolean existsDirectByCriteria(Criteria criteria) {
		return existsDirectByCriteria(criteria, false);
	}

}
