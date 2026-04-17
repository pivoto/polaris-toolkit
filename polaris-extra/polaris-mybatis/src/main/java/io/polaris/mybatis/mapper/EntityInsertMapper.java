package io.polaris.mybatis.mapper;

import java.util.Map;
import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.annotation.AutoUseGeneratedKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

/**
 * 实体插入映射器接口，提供多种插入实体的方法
 * 支持通过实体对象或Map参数进行数据插入
 * 可灵活控制是否包含空值字段以及指定包含/排除的字段
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityInsertMapper<E> extends EntityMapper<E> {

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@AutoUseGeneratedKeys(value = BindingKeys.ENTITY)
	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntity(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int insertEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return insertEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int insertEntity(E entity, Set<String> includeEmptyFields) {
		return insertEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 受影响的行数
	 */
	default int insertEntity(E entity, boolean includeAllEmpty) {
		return insertEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 插入实体对象到数据库
	 *
	 * @param entity 实体对象，包含要插入的数据
	 * @return 受影响的行数
	 */
	default int insertEntity(E entity) {
		return insertEntity(entity, false, (Set<String>) null, (Set<String>) null);
	}


	/**
	 * 通过Map参数插入数据到数据库
	 *
	 * @param entity 包含插入数据的Map对象
	 * @param includeAllEmpty 是否包含空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	@AutoUseGeneratedKeys(value = BindingKeys.ENTITY)
	@InsertProvider(type = MapperProviders.class, method = MapperProviderKeys.insertEntity)
	int insertEntityByMap(@Param(BindingKeys.ENTITY) Map<String, Object> entity
		, @Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);


	/**
	 * 通过Map参数插入数据到数据库
	 *
	 * @param entity 包含插入数据的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 受影响的行数
	 */
	default int insertEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return insertEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 通过Map参数插入数据到数据库
	 *
	 * @param entity 包含插入数据的Map对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 受影响的行数
	 */
	default int insertEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return insertEntityByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 通过Map参数插入数据到数据库
	 *
	 * @param entity 包含插入数据的Map对象
	 * @param includeAllEmpty 是否包含空值字段
	 * @return 受影响的行数
	 */
	default int insertEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return insertEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 通过Map参数插入数据到数据库
	 *
	 * @param entity 包含插入数据的Map对象
	 * @return 受影响的行数
	 */
	default int insertEntityByMap(Map<String, Object> entity) {
		return insertEntityByMap(entity, false, (Set<String>) null, (Set<String>) null);
	}

}
