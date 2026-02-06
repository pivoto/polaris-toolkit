package io.polaris.mybatis.mapper;

/**
 * 实体删除操作的统一接口
 * <p>
 * 该接口整合了多种实体删除方式的映射器，提供统一的删除操作入口。
 * 支持默认删除、直接删除和逻辑删除等多种删除策略。
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntityDeleteByAnyMapper<E> extends EntityMapper<E>
	, EntityDeleteDefaultByAnyMapper<E>
	, EntityDeleteDirectByAnyMapper<E>
	, EntityDeleteLogicByAnyMapper<E> {

}
