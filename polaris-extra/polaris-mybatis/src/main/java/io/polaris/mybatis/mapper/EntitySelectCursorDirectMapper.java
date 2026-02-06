package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 实体查询游标直接映射器接口
 * 提供基于实体、Map和Criteria条件的游标查询功能
 * 支持动态结果映射和逻辑删除过滤
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectCursorDirectMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体对象查询游标数据
	 *
	 * @param entity             实体对象，用于构建WHERE条件
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursor(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity             实体对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity             实体对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity       实体对象
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity  实体对象
	 * @param orderBy 排序条件
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(E entity, OrderBy orderBy) {
		return directSelectEntityCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询游标数据（重载方法）
	 *
	 * @param entity 实体对象
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursor(E entity) {
		return directSelectEntityCursor(entity, null, false);
	}

	/**
	 * 根据Map参数查询游标数据
	 *
	 * @param entity             Map参数，用于构建WHERE条件
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询游标数据（重载方法）
	 *
	 * @param entity             Map参数
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询游标数据（重载方法）
	 *
	 * @param entity             Map参数
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询游标数据（重载方法）
	 *
	 * @param entity       Map参数
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityCursorByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询游标数据（重载方法）
	 *
	 * @param entity  Map参数
	 * @param orderBy 排序条件
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectEntityCursorByMap(entity, orderBy, false);
	}

	/**
	 * 根据Map参数查询游标数据（重载方法）
	 *
	 * @param entity Map参数
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByMap(Map<String, Object> entity) {
		return directSelectEntityCursorByMap(entity, null, false);
	}

	/**
	 * 根据Criteria条件查询游标数据
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<E> directSelectEntityCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询游标数据（重载方法）
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询游标数据（重载方法）
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询游标数据（重载方法）
	 *
	 * @param criteria     查询条件对象
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询游标数据（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy  排序条件
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectEntityCursorByCriteria(criteria, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询游标数据（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @return 游标结果集
	 */
	default Cursor<E> directSelectEntityCursorByCriteria(Criteria criteria) {
		return directSelectEntityCursorByCriteria(criteria, null, false);
	}

	/**
	 * 根据实体对象查询Map类型的游标数据
	 *
	 * @param entity             实体对象，用于构建WHERE条件
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集（Map类型）
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursor(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体对象查询Map类型的游标数据（重载方法）
	 *
	 * @param entity             实体对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursor(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体对象查询Map类型的游标数据（重载方法）
	 *
	 * @param entity             实体对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursor(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据实体对象查询Map类型的游标数据（重载方法）
	 *
	 * @param entity       实体对象
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapCursor(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据实体对象查询Map类型的游标数据（重载方法）
	 *
	 * @param entity  实体对象
	 * @param orderBy 排序条件
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursor(E entity, OrderBy orderBy) {
		return directSelectMapCursor(entity, orderBy, false);
	}

	/**
	 * 根据实体对象查询Map类型的游标数据（重载方法）
	 *
	 * @param entity 实体对象
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursor(E entity) {
		return directSelectMapCursor(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map类型的游标数据
	 *
	 * @param entity             Map参数，用于构建WHERE条件
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集（Map类型）
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursorByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数查询Map类型的游标数据（重载方法）
	 *
	 * @param entity             Map参数
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursorByMap(entity, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数查询Map类型的游标数据（重载方法）
	 *
	 * @param entity             Map参数
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursorByMap(entity, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map参数查询Map类型的游标数据（重载方法）
	 *
	 * @param entity       Map参数
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapCursorByMap(entity, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Map参数查询Map类型的游标数据（重载方法）
	 *
	 * @param entity Map参数
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity) {
		return directSelectMapCursorByMap(entity, null, false);
	}

	/**
	 * 根据Map参数查询Map类型的游标数据（重载方法）
	 *
	 * @param entity  Map参数
	 * @param orderBy 排序条件
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByMap(Map<String, Object> entity, OrderBy orderBy) {
		return directSelectMapCursorByMap(entity, orderBy, false);
	}

	/**
	 * 根据Criteria条件查询Map类型的游标数据
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeAllEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 游标结果集（Map类型）
	 */
	@WithLogicDeleted(false)
	@DynamicResultMapping
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityDirect)
	@Options(fetchSize = 1000)
	Cursor<Map<String, Object>> directSelectMapCursorByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.ORDER_BY) OrderBy orderBy,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Criteria条件查询Map类型的游标数据（重载方法）
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @param excludeFields      排除字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map类型的游标数据（重载方法）
	 *
	 * @param criteria           查询条件对象
	 * @param orderBy            排序条件
	 * @param includeEmptyFields 包含空值的字段集合
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, Set<String> includeEmptyFields) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false, includeEmptyFields, null);
	}

	/**
	 * 根据Criteria条件查询Map类型的游标数据（重载方法）
	 *
	 * @param criteria     查询条件对象
	 * @param orderBy      排序条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy, boolean includeAllEmpty) {
		return directSelectMapCursorByCriteria(criteria, orderBy, includeAllEmpty, null, null);
	}

	/**
	 * 根据Criteria条件查询Map类型的游标数据（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria) {
		return directSelectMapCursorByCriteria(criteria, null, false);
	}

	/**
	 * 根据Criteria条件查询Map类型的游标数据（重载方法）
	 *
	 * @param criteria 查询条件对象
	 * @param orderBy  排序条件
	 * @return 游标结果集（Map类型）
	 */
	default Cursor<Map<String, Object>> directSelectMapCursorByCriteria(Criteria criteria, OrderBy orderBy) {
		return directSelectMapCursorByCriteria(criteria, orderBy, false);
	}

}
