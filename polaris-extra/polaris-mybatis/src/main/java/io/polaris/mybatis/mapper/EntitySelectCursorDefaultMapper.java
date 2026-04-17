package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 实体选择游标默认映射器接口，提供基于不同条件查询游标数据的方法。
 * 支持实体对象、Map对象和Criteria对象作为查询条件，并可配置排序、空值字段处理和逻辑删除标记。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorDefaultMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体条件查询游标数据，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity, OrderBy orderBy) {
		return selectEntityCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体条件查询游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursor(E entity) {
		return selectEntityCursor(entity, null, false);
	}


	/**
	 * 根据Map条件查询游标数据，考虑逻辑删除标记
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Map条件查询游标数据，考虑逻辑删除标记
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByMap(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map条件查询游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件查询游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map条件查询游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectEntityCursorByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map条件查询游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByMap(Map<String, Object> entity) {
		return selectEntityCursorByMap(entity, null, false);
	}

	/**
	 * 根据条件对象查询实体游标数据，考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectEntityCursorByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectEntityCursorByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据条件对象查询实体游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 查询结果游标
	 */
	default Cursor<E> selectEntityCursorByCriteria(Criteria criteria) {
		return selectEntityCursorByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map游标数据，考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursor(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursor(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity, OrderBy orderBy) {
		return selectMapCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map游标数据
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursor(E entity) {
		return selectMapCursor(entity, null, false);
	}

	/**
	 * 根据Map条件查询Map游标数据，考虑逻辑删除标记
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByMap(entity, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursorByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity) {
		return selectMapCursorByMap(entity, null, false);
	}

	/**
	 * 根据Map条件查询Map游标数据
	 *
	 * @param entity Map对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return selectMapCursorByMap(entity, orderBy, false);
	}

	/**
	 * 根据条件对象查询Map游标数据，考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否处理逻辑删除规则
	 * @return 查询结果Map游标
	 */
	@SupportLogicDeleted(true)
	@AutoResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> selectMapCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.ORDER_BY) OrderBy orderBy
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByCriteria(criteria, orderBy, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return selectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return selectMapCursorByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria) {
		return selectMapCursorByCriteria(criteria, null, false);
	}

	/**
	 * 根据条件对象查询Map游标数据
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param orderBy 排序条件
	 * @return 查询结果Map游标
	 */
	default Cursor<Map<String, Object>> selectMapCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return selectMapCursorByCriteria(criteria, orderBy, false);
	}

}
