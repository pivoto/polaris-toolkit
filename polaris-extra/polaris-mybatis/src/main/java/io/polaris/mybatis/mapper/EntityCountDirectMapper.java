package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityCountDirectMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityDirect)
	int directCountEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directCountEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(E entity, Set<String> includeEmptyFields) {
		return directCountEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(E entity, boolean includeEmpty) {
		return directCountEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int directCountEntity(E entity) {
		return directCountEntity(entity, false);
	}


	/**
	 * 根据Map参数统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityDirect)
	int directCountEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据Map参数统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directCountEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directCountEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return directCountEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量，不考虑逻辑删除标记
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByMap(Map<String, Object> entity) {
		return directCountEntityByMap(entity, false);
	}

	/**
	 * 根据条件对象统计记录数量，不考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(false)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntityDirect)
	int directCountEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据条件对象统计记录数量，不考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directCountEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象统计记录数量，不考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directCountEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量，不考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return directCountEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量，不考虑逻辑删除标记
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int directCountEntityByCriteria(Criteria criteria) {
		return directCountEntityByCriteria(criteria, false);
	}

}
