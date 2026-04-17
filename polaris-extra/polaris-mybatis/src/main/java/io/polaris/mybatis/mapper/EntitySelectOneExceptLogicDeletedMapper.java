package io.polaris.mybatis.mapper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.ExistsByCount;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 实体选择器接口，用于排除逻辑删除的数据进行查询操作。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectOneExceptLogicDeletedMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID查询单个实体，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，包含ID信息
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityByIdExceptLogicDeleted)
	E selectEntityByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(E entity, boolean includeAllEmpty) {
		return selectEntityExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeleted(E entity) {
		return selectEntityExceptLogicDeleted(entity, false);
	}

	/**
	 * 根据Map条件查询单个实体，排除逻辑删除的数据。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return selectEntityExceptLogicDeletedByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityExceptLogicDeletedByMap(entity, false);
	}

	/**
	 * 根据Criteria条件查询单个实体，排除逻辑删除的数据。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	E selectEntityExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询单个实体，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E selectEntityExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectEntityExceptLogicDeletedByCriteria(criteria, false);
	}

	/**
	 * 根据实体条件查询Map结果，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeleted(E entity, boolean includeAllEmpty) {
		return selectMapExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeleted(E entity) {
		return selectMapExceptLogicDeleted(entity, false);
	}

	/**
	 * 根据Map条件查询Map结果，排除逻辑删除的数据。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeletedByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return selectMapExceptLogicDeletedByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapExceptLogicDeletedByMap(entity, false);
	}

	/**
	 * 根据Criteria条件查询Map结果，排除逻辑删除的数据。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	Map<String, Object> selectMapExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return selectMapExceptLogicDeletedByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询Map结果，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> selectMapExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapExceptLogicDeletedByCriteria(criteria, false);
	}

	/**
	 * 根据实体ID判断是否存在，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，包含ID信息
	 * @return 若存在返回true，否则返回false
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityByIdExceptLogicDeleted)
	boolean existsByIdExceptLogicDeleted(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 内部方法，用于根据任意条件判断是否存在，排除逻辑删除的数据。
	 * 此方法仅供内部使用，实际开发中不应直接调用。
	 *
	 * @param entity 查询条件对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Cursor对象，包含查询结果
	 */
	@ExistsByCount(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityExceptLogicDeleted)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerByAnyExceptLogicDeleted(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(entity, includeAllEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return existsExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(E entity, boolean includeAllEmpty) {
		return existsExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeleted(E entity) {
		return existsExceptLogicDeleted(entity, false);
	}

	/**
	 * 根据Map条件判断是否存在，排除逻辑删除的数据。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(entity, includeAllEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 根据Map条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return existsExceptLogicDeletedByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return existsExceptLogicDeletedByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByMap(Map<String, Object> entity) {
		return existsExceptLogicDeletedByMap(entity, false);
	}

	/**
	 * 根据Criteria条件判断是否存在，排除逻辑删除的数据。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAnyExceptLogicDeleted(criteria, includeAllEmpty, includeEmptyFields, excludeFields);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 根据Criteria条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return existsExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return existsExceptLogicDeletedByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件判断是否存在，排除逻辑删除的数据（简化版）。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean existsExceptLogicDeletedByCriteria(Criteria criteria) {
		return existsExceptLogicDeletedByCriteria(criteria, false);
	}

}
