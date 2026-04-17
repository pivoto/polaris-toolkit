package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * EntityCountDefaultMapper 接口定义了基于不同参数类型统计记录数量的方法。
 * 支持通过实体对象、Map 参数和 Criteria 对象进行条件查询，并可控制是否包含空值字段、
 * 指定包含或排除的字段，以及是否考虑逻辑删除字段。
 *
 * @param <E> 实体类型泛型参数
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityCountDefaultMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件统计记录数量
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntity(@Param(BindingKeys.WHERE) E entity,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, Set<String> includeEmptyFields) {
		return countEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntity(E entity, boolean includeAllEmpty) {
		return countEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件统计记录数量（重载方法）
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
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据Map参数统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByMap(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map参数统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return countEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量（重载方法）
	 *
	 * @param entity 实体对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return countEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数统计记录数量（重载方法）
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
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param supportLogicDeleted 是否支持逻辑删除字段，默认为true
	 * @return 符合条件的记录数量
	 */
	@SupportLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.countEntity)
	int countEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria,
		@Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields,
		@Param(BindingKeys.SUPPORT_LOGIC_DELETED) Boolean supportLogicDeleted);

	/**
	 * 根据条件对象统计记录数量（重载方法）
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByCriteria(criteria, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据条件对象统计记录数量（重载方法）
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象统计记录数量（重载方法）
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return countEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量（重载方法）
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return countEntityByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象统计记录数量（重载方法）
	 *
	 * @param criteria 条件对象，用于构建查询条件
	 * @return 符合条件的记录数量
	 */
	default int countEntityByCriteria(Criteria criteria) {
		return countEntityByCriteria(criteria, false);
	}

}
