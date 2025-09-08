package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteDefaultByAnyMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param withLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(true)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(E entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByAny(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return deleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(E entity, boolean includeEmpty) {
		return deleteEntityByAny(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件删除记录
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @return 删除的记录数量
	 */
	default int deleteEntityByAny(E entity) {
		return deleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}


	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param withLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(true)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByMap(Map<String, Object> entity, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByMap(entity, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return deleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int deleteEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return deleteEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数删除记录
	 *
	 * @param entity 包含删除条件的Map对象
	 * @return 删除的记录数量
	 */
	default int deleteEntityByMap(Map<String, Object> entity) {
		return deleteEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @param withLogicDeleted 是否使用逻辑删除，null表示使用默认策略
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(true)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.deleteEntityByAny)
	int deleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);


	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByCriteria(Criteria criteria, boolean includeEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByCriteria(criteria, includeEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return deleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int deleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return deleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int deleteEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return deleteEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象删除记录
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @return 删除的记录数量
	 */
	default int deleteEntityByCriteria(Criteria criteria) {
		return deleteEntityByCriteria(criteria, false, (Set<String>) null, (Set<String>) null);
	}


}
