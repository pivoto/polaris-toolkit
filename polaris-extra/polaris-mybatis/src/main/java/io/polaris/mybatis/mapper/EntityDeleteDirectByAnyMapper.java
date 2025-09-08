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
public interface EntityDeleteDirectByAnyMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return directDeleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, boolean includeEmpty) {
		return directDeleteEntityByAny(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity) {
		return directDeleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}


	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directDeleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, boolean includeEmpty) {
		return directDeleteEntityByMap(entity, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity 包含删除条件的Map对象
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity) {
		return directDeleteEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY) boolean includeEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directDeleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, boolean includeEmpty) {
		return directDeleteEntityByCriteria(criteria, includeEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria) {
		return directDeleteEntityByCriteria(criteria, false, (Set<String>) null, (Set<String>) null);
	}


}
