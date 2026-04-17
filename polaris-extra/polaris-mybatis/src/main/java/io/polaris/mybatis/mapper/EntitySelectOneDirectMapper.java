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
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;


/**
 * 实体选择器接口，提供直接查询单个实体的相关方法。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectOneDirectMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID直接查询单个实体。
	 *
	 * @param entity 实体对象，包含ID信息
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityByIdDirect)
	E directSelectEntityById(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 根据实体条件直接查询单个实体。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(E entity, Set<String> includeEmptyFields) {
		return directSelectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，可指定是否包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(E entity, boolean includeAllEmpty) {
		return directSelectEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntity(E entity) {
		return directSelectEntity(entity, false);
	}

	/**
	 * 根据Map条件直接查询单个实体。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map条件直接查询单个实体，默认不包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directSelectEntityByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件直接查询单个实体，可指定是否包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return directSelectEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByMap(Map<String, Object> entity) {
		return directSelectEntityByMap(entity, false);
	}

	/**
	 * 根据Criteria条件直接查询单个实体。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	E directSelectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件直接查询单个实体，默认不包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directSelectEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，可指定是否包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return directSelectEntityByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 查询到的实体对象，若未找到则返回null
	 */
	default E directSelectEntityByCriteria(Criteria criteria) {
		return directSelectEntityByCriteria(criteria, false);
	}

	/**
	 * 根据实体条件直接查询单个实体，返回Map结果。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接查询单个实体，返回Map结果，默认不包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMap(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMap(E entity, Set<String> includeEmptyFields) {
		return directSelectMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，返回Map结果，可指定是否包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMap(E entity, boolean includeAllEmpty) {
		return directSelectMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMap(E entity) {
		return directSelectMap(entity, false);
	}

	/**
	 * 根据Map条件直接查询单个实体，返回Map结果。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map条件直接查询单个实体，返回Map结果，默认不包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directSelectMapByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map条件直接查询单个实体，返回Map结果，可指定是否包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return directSelectMapByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByMap(Map<String, Object> entity) {
		return directSelectMapByMap(entity, false);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，返回Map结果。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	@AutoResultMapping
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	Map<String, Object> directSelectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件直接查询单个实体，返回Map结果，默认不包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directSelectMapByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，返回Map结果，可指定是否包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return directSelectMapByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件直接查询单个实体，返回Map结果，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 查询到的Map结果，若未找到则返回null
	 */
	default Map<String, Object> directSelectMapByCriteria(Criteria criteria) {
		return directSelectMapByCriteria(criteria, false);
	}

	/**
	 * 根据实体ID判断实体是否存在。
	 *
	 * @param entity 实体对象，包含ID信息
	 * @return 若存在返回true，否则返回false
	 */
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityByIdDirect)
	boolean directExistsById(@Param(BindingKeys.ENTITY) E entity);

	/**
	 * 内部方法，根据任意条件判断实体是否存在，返回Cursor结果。
	 *
	 * @param entity 查询条件对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return Cursor结果，包含布尔值
	 */
	@ExistsByCount(false)
	@SupportLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityDirect)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> directExistsInnerByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件判断实体是否存在。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(entity, includeAllEmpty, includeEmptyFields, excludeFields);
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
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExists(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(E entity, Set<String> includeEmptyFields) {
		return directExists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断实体是否存在，可指定是否包含所有空值字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(E entity, boolean includeAllEmpty) {
		return directExists(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExists(E entity) {
		return directExists(entity, false);
	}

	/**
	 * 根据Map条件判断实体是否存在。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(entity, includeAllEmpty, includeEmptyFields, excludeFields);
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
	 * 根据Map条件判断实体是否存在，默认不包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExistsByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directExistsByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件判断实体是否存在，可指定是否包含所有空值字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return directExistsByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param entity Map对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByMap(Map<String, Object> entity) {
		return directExistsByMap(entity, false);
	}

	/**
	 * 根据Criteria条件判断实体是否存在。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = directExistsInnerByAny(criteria, includeAllEmpty, includeEmptyFields, excludeFields);
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
	 * 根据Criteria条件判断实体是否存在，默认不包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directExistsByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directExistsByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件判断实体是否存在，可指定是否包含所有空值字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return directExistsByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件判断实体是否存在，默认不包含所有空值字段，且不排除任何字段。
	 *
	 * @param criteria Criteria对象，作为查询条件
	 * @return 若存在返回true，否则返回false
	 */
	default boolean directExistsByCriteria(Criteria criteria) {
		return directExistsByCriteria(criteria, false);
	}

}
