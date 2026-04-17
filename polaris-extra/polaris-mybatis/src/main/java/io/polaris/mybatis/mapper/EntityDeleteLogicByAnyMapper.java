package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;

/**
 * 实体逻辑删除映射器接口，提供基于任意条件的逻辑删除功能
 * <p>
 * 该接口定义了多种逻辑删除方法，支持根据实体对象、Map参数或条件对象进行删除操作，
 * 并允许灵活控制空值字段的处理方式和字段排除策略。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteLogicByAnyMapper<E> extends EntityMapper<E> {


	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityByAny)
	int logicDeleteEntityByAny(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByAny(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByAny(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByAny(E entity, Set<String> includeEmptyFields) {
		return logicDeleteEntityByAny(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByAny(E entity, boolean includeAllEmpty) {
		return logicDeleteEntityByAny(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件进行逻辑删除操作
	 *
	 * @param entity 实体对象，用于构建删除条件
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByAny(E entity) {
		return logicDeleteEntityByAny(entity, false, (Set<String>) null, (Set<String>) null);
	}


	/**
	 * 根据Map参数进行逻辑删除操作
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityByAny)
	int logicDeleteEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据Map参数进行逻辑删除操作
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map参数进行逻辑删除操作
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return logicDeleteEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map参数进行逻辑删除操作
	 *
	 * @param entity 包含删除条件的Map对象
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return logicDeleteEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map参数进行逻辑删除操作
	 *
	 * @param entity 包含删除条件的Map对象
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByMap(Map<String, Object> entity) {
		return logicDeleteEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象进行逻辑删除操作
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@DeleteProvider(type = MapperProviders.class, method = MapperProviderKeys.logicDeleteEntityByAny)
	int logicDeleteEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 根据条件对象进行逻辑删除操作
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return logicDeleteEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据条件对象进行逻辑删除操作
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return logicDeleteEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据条件对象进行逻辑删除操作
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return logicDeleteEntityByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据条件对象进行逻辑删除操作
	 *
	 * @param criteria 条件对象，用于构建删除条件
	 * @return 受影响的行数
	 */
	default int logicDeleteEntityByCriteria(Criteria criteria) {
		return logicDeleteEntityByCriteria(criteria, false, (Set<String>) null, (Set<String>) null);
	}

}
