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
 * 实体直接删除映射器接口，提供根据任意条件直接删除记录的功能，不考虑逻辑删除。
 *
 * <p>该接口定义了多种删除方法，支持根据实体对象、Map参数或条件对象进行删除操作，
 * 并允许灵活配置是否包含空值字段以及指定需要包含或排除的字段。</p>
 *
 * <p>所有删除操作均通过{@link MapperProviders}提供的SQL生成器实现，
 * 使用{@link MapperProviderKeys#directDeleteEntityByAny}作为方法标识。</p>
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteDirectByAnyMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity             实体对象，用于构建删除条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity             实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity             实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return directDeleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件直接删除记录，不考虑逻辑删除
	 *
	 * @param entity          实体对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByAny(E entity, boolean includeAllEmpty) {
		return directDeleteEntityByAny(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
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
	 * @param entity             包含删除条件的Map对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity             包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity             包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return directDeleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数直接删除记录，不考虑逻辑删除
	 *
	 * @param entity          包含删除条件的Map对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return directDeleteEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
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
	 * @param criteria           条件对象，用于构建删除条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	@WithLogicDeleted(false)
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.directDeleteEntityByAny)
	int directDeleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria           条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return directDeleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria           条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return directDeleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象直接删除记录，不考虑逻辑删除
	 *
	 * @param criteria        条件对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 删除的记录数量
	 */
	default int directDeleteEntityByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return directDeleteEntityByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
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
