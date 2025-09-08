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
public interface EntityCountDefaultMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @param withLogicDeleted   是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, Set<String> includeEmptyFields) {
		return countEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity       实体对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, boolean includeEmpty) {
		return countEntity(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity) {
		return countEntity(entity, false);
	}


	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @param withLogicDeleted   是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);


	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity             实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return countEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity       实体对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return countEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity) {
		return countEntityByMap(entity, false);
	}

	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @param withLogicDeleted   是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);


	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmpty       是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByCriteria(criteria, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria           条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria     条件对象，用于构建查询条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return countEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria) {
		return countEntityByCriteria(criteria, false);
	}

}
