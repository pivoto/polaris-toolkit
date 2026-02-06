package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 实体查询游标接口，排除逻辑删除数据
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorExceptLogicDeletedMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询游标数据，排除逻辑删除记录
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询游标数据（指定包含空值字段和排除字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询游标数据（指定包含空值字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询游标数据（指定是否包含空值）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询游标数据（仅指定排序条件）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询游标数据（仅实体条件）
	 *
	 * @param entity 实体对象
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeleted(E entity) {
		return selectEntityCursorExceptLogicDeleted(entity, null, false);
	}


	/**
	 * 根据Map参数查询游标数据，排除逻辑删除记录
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询游标数据（指定包含空值字段和排除字段）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询游标数据（指定包含空值字段）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询游标数据（指定是否包含空值）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询游标数据（仅指定排序条件）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map参数查询游标数据（仅Map条件）
	 *
	 * @param entity Map参数
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectEntityCursorExceptLogicDeletedByMap(entity, null, false);
	}


	/**
	 * 根据Criteria条件查询游标数据，排除逻辑删除记录
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询游标数据（指定包含空值字段和排除字段）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询游标数据（指定包含空值字段）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询游标数据（指定是否包含空值）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询游标数据（仅指定排序条件）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询游标数据（仅Criteria条件）
	 *
	 * @param criteria 查询条件
	 * @return 游标结果
	 */
	default Cursor<E> selectEntityCursorExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectEntityCursorExceptLogicDeletedByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map游标数据，排除逻辑删除记录
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询Map游标数据（指定包含空值字段和排除字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map游标数据（指定包含空值字段）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map游标数据（指定是否包含空值）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map游标数据（仅指定排序条件）
	 *
	 * @param entity 实体对象
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeleted(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map游标数据（仅实体条件）
	 *
	 * @param entity 实体对象
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeleted(E entity) {
		return selectMapCursorExceptLogicDeleted(entity, null, false);
	}


	/**
	 * 根据Map参数查询Map游标数据，排除逻辑删除记录
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询Map游标数据（指定包含空值字段和排除字段）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询Map游标数据（指定包含空值字段）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询Map游标数据（指定是否包含空值）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询Map游标数据（仅Map条件）
	 *
	 * @param entity Map参数
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity) {
		return selectMapCursorExceptLogicDeletedByMap(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map游标数据（仅指定排序条件）
	 *
	 * @param entity Map参数
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeletedByMap(entity, orderBy, false);
	}


	/**
	 * 根据Criteria条件查询Map游标数据，排除逻辑删除记录
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityExceptLogicDeleted)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询Map游标数据（指定包含空值字段和排除字段）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields 排除的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map游标数据（指定包含空值字段）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询Map游标数据（指定是否包含空值）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询Map游标数据（仅Criteria条件）
	 *
	 * @param criteria 查询条件
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, null, false);
	}

	/**
	 * 根据Criteria条件查询Map游标数据（仅指定排序条件）
	 *
	 * @param criteria 查询条件
	 * @param orderBy 排序条件
	 * @return 游标结果
	 */
	default Cursor<Map<String, Object>> selectMapCursorExceptLogicDeletedByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorExceptLogicDeletedByCriteria(criteria, orderBy, false);
	}

}
