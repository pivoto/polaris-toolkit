package io.polaris.mybatis.mapper;

import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * 实体更新映射器接口，提供根据ID更新实体的功能。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityUpdateByIdMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID更新实体数据。
	 *
	 * @param entity             实体对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 更新影响的行数
	 */
	@UpdateProvider(type = MapperProviders.class, method = MapperProviderKeys.updateEntityById)
	int updateEntityById(@Param(BindingKeys.ENTITY) E entity,
		@Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段。
	 *
	 * @param entity             实体对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields      需要排除的字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return updateEntityById(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity             实体对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(E entity, Set<String> includeEmptyFields) {
		return updateEntityById(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，可选择是否包含所有空值字段。
	 *
	 * @param entity          实体对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 更新影响的行数
	 */
	default int updateEntityById(E entity, boolean includeAllEmpty) {
		return updateEntityById(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity 实体对象
	 * @return 更新影响的行数
	 */
	default int updateEntityById(E entity) {
		return updateEntityById(entity, false, (Set<String>) null, (Set<String>) null);
	}

}
