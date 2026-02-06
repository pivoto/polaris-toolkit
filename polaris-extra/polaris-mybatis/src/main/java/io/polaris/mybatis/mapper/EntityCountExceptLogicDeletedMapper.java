package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * 实体映射器接口，用于统计非逻辑删除的实体记录数量。
 * 提供多种重载方法，支持根据实体对象、Map参数或条件对象进行查询。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityCountExceptLogicDeletedMapper<E> extends EntityMapper<E> {

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityExceptLogicDeleted)
	int countEntityExceptLogicDeleted(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityExceptLogicDeleted(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(E entity, Set<String> includeEmptyFields) {
		return countEntityExceptLogicDeleted(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity          实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(E entity, boolean includeAllEmpty) {
		return countEntityExceptLogicDeleted(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 统计非逻辑删除的实体记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeleted(E entity) {
		return countEntityExceptLogicDeleted(entity, false);
	}


	/**
	 * 根据Map参数统计非逻辑删除的实体记录数量
	 *
	 * @param entity             包含查询条件的Map对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityExceptLogicDeleted)
	int countEntityExceptLogicDeletedByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据Map参数统计非逻辑删除的实体记录数量
	 *
	 * @param entity             包含查询条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数统计非逻辑删除的实体记录数量
	 *
	 * @param entity             包含查询条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return countEntityExceptLogicDeletedByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计非逻辑删除的实体记录数量
	 *
	 * @param entity          包含查询条件的Map对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return countEntityExceptLogicDeletedByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计非逻辑删除的实体记录数量
	 *
	 * @param entity 包含查询条件的Map对象
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByMap(Map<String, Object> entity) {
		return countEntityExceptLogicDeletedByMap(entity, false);
	}

	/**
	 * 根据条件对象统计非逻辑删除的实体记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeAllEmpty    是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityExceptLogicDeleted)
	int countEntityExceptLogicDeletedByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据条件对象统计非逻辑删除的实体记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象统计非逻辑删除的实体记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return countEntityExceptLogicDeletedByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计非逻辑删除的实体记录数量
	 *
	 * @param criteria        条件对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return countEntityExceptLogicDeletedByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计非逻辑删除的实体记录数量
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 符合条件的非逻辑删除记录数量
	 */
	default int countEntityExceptLogicDeletedByCriteria(Criteria criteria) {
		return countEntityExceptLogicDeletedByCriteria(criteria, false);
	}

}
