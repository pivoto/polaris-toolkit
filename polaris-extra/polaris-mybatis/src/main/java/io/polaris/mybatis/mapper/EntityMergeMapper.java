package io.polaris.mybatis.mapper;

import java.lang.reflect.Type;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;

/**
 * 实体合并映射器接口，提供基于SQL的实体数据合并功能。
 * 支持匹配时更新、不匹配时插入等灵活的合并策略。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityMergeMapper<E> extends MergeStatementMapper, EntityMapper<E> {

	/**
	 * 通过SQL合并实体数据，默认行为是匹配时更新，不匹配时插入
	 *
	 * @param entity 实体对象，包含要合并的数据
	 * @return 受影响的行数
	 */
	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity);
	}

	/**
	 * 通过SQL合并实体数据，可自定义合并行为
	 *
	 * @param entity               实体对象，包含要合并的数据
	 * @param includeAllEmpty      是否包含所有空值字段
	 * @param updateWhenMatched    匹配时是否更新
	 * @param insertWhenNotMatched 不匹配时是否插入
	 * @return 受影响的行数
	 */
	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity, boolean includeAllEmpty, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity, includeAllEmpty, updateWhenMatched, insertWhenNotMatched);
	}

	/**
	 * 通过SQL合并实体数据，可自定义匹配和插入行为
	 *
	 * @param entity               实体对象，包含要合并的数据
	 * @param updateWhenMatched    匹配时是否更新
	 * @param insertWhenNotMatched 不匹配时是否插入
	 * @return 受影响的行数
	 */
	@SuppressWarnings({"unchecked"})
	default int mergeBySql(E entity, boolean updateWhenMatched, boolean insertWhenNotMatched) {
		Type actualType = JavaType.of(getClass()).getActualType(EntityMapper.class, 0);
		Class<?> entityClass = Types.getClass(actualType);
		if (entityClass == Object.class || entityClass == void.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return mergeBySql((Class<E>) entityClass, entity, updateWhenMatched, insertWhenNotMatched);
	}

}
