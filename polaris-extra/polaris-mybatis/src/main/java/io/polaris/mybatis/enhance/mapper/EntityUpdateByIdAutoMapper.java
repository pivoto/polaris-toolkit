package io.polaris.mybatis.enhance.mapper;

import java.util.Set;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntityUpdateByIdAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID更新实体数据。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 更新影响的行数
	 */
	@AutoMapper(type = AutoMapperType.updateEntityById)
	int updateEntityById(@Param(BindingKeys.ENTITY) Object entity,
		@Param(BindingKeys.INCLUDE_ALL_EMPTY) boolean includeAllEmpty,
		@Param(BindingKeys.INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields,
		@Param(BindingKeys.EXCLUDE_COLUMNS) Set<String> excludeFields);

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @param excludeFields 需要排除的字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return updateEntityById(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeEmptyFields 需要包含的空值字段集合
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, Set<String> includeEmptyFields) {
		return updateEntityById(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，可选择是否包含所有空值字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity, boolean includeAllEmpty) {
		return updateEntityById(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体ID更新实体数据，不包含所有空值字段，不排除任何字段。
	 *
	 * @param entity 实体对象，可以是Bean或Map等对象
	 * @return 更新影响的行数
	 */
	default int updateEntityById(Object entity) {
		return updateEntityById(entity, false, (Set<String>) null, (Set<String>) null);
	}

}
