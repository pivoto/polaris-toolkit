package io.polaris.mybatis.mapper;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.annotation.ExistsByCount;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import io.polaris.mybatis.support.MapperHelperContext;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 实体单条记录查询接口，默认实现类
 *
 * @param <E> 实体类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface EntitySelectOneDefaultMapper<E> extends EntityMapper<E> {

	/**
	 * 根据实体ID查询单条记录
	 *
	 * @param entity           实体对象，包含主键信息
	 * @param withLogicDeleted 是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntityById)
	E selectEntityById(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据实体ID查询单条记录（默认不包含逻辑删除数据）
	 *
	 * @param entity 实体对象，包含主键信息
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityById(E entity) {
		return selectEntityById(entity, null);
	}

	/**
	 * 根据实体条件查询单条记录
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntity(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据实体条件查询单条记录（完整参数版本）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件查询单条记录（指定包含和排除字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntity(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询单条记录（仅指定包含字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(E entity, Set<String> includeEmptyFields) {
		return selectEntity(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单条记录（指定是否包含所有空值）
	 *
	 * @param entity          实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(E entity, boolean includeAllEmpty) {
		return selectEntity(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询单条记录（基础版本）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntity(E entity) {
		return selectEntity(entity, false);
	}

	/**
	 * 根据Map条件查询单条记录
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据Map条件查询单条记录（完整参数版本）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByMap(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map条件查询单条记录（指定包含和排除字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询单条记录（仅指定包含字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectEntityByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 根据Map条件查询单条记录（指定是否包含所有空值）
	 *
	 * @param entity          Map形式的查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return selectEntityByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询单条记录（基础版本）
	 *
	 * @param entity Map形式的查询条件
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByMap(Map<String, Object> entity) {
		return selectEntityByMap(entity, false);
	}

	/**
	 * 根据Criteria条件查询单条记录
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的实体对象，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	E selectEntityByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据Criteria条件查询单条记录（完整参数版本）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByCriteria(criteria, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Criteria条件查询单条记录（指定包含和排除字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询单条记录（仅指定包含字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectEntityByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询单条记录（指定是否包含所有空值）
	 *
	 * @param criteria        Criteria查询条件对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return selectEntityByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询单条记录（基础版本）
	 *
	 * @param criteria Criteria查询条件对象
	 * @return 查询到的实体对象，未找到返回null
	 */
	default E selectEntityByCriteria(Criteria criteria) {
		return selectEntityByCriteria(criteria, false);
	}

	/**
	 * 根据实体条件查询Map结果
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的Map结果，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMap(@Param(BindingKeys.WHERE) E entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据实体条件查询Map结果（完整参数版本）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMap(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMap(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据实体条件查询Map结果（指定包含和排除字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据实体条件查询Map结果（仅指定包含字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMap(E entity, Set<String> includeEmptyFields) {
		return selectMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询Map结果（指定是否包含所有空值）
	 *
	 * @param entity          实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMap(E entity, boolean includeAllEmpty) {
		return selectMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据实体条件查询Map结果（基础版本）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMap(E entity) {
		return selectMap(entity, false);
	}

	/**
	 * 根据Map条件查询Map结果
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的Map结果，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByMap(@Param(BindingKeys.WHERE) Map<String, Object> entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据Map条件查询Map结果（完整参数版本）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByMap(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Map条件查询Map结果（指定包含和排除字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Map条件查询Map结果（仅指定包含字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return selectMapByMap(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询Map结果（指定是否包含所有空值）
	 *
	 * @param entity          Map形式的查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return selectMapByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Map条件查询Map结果（基础版本）
	 *
	 * @param entity Map形式的查询条件
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByMap(Map<String, Object> entity) {
		return selectMapByMap(entity, false);
	}

	/**
	 * 根据Criteria条件查询Map结果
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 查询到的Map结果，未找到返回null
	 */
	@DynamicResultMapping
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.selectEntity)
	Map<String, Object> selectMapByCriteria(@Param(BindingKeys.WHERE) Criteria criteria
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 根据Criteria条件查询Map结果（完整参数版本）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByCriteria(criteria, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 根据Criteria条件查询Map结果（指定包含和排除字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 根据Criteria条件查询Map结果（仅指定包含字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return selectMapByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询Map结果（指定是否包含所有空值）
	 *
	 * @param criteria        Criteria查询条件对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return selectMapByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 根据Criteria条件查询Map结果（基础版本）
	 *
	 * @param criteria Criteria查询条件对象
	 * @return 查询到的Map结果，未找到返回null
	 */
	default Map<String, Object> selectMapByCriteria(Criteria criteria) {
		return selectMapByCriteria(criteria, false);
	}

	/**
	 * 判断实体是否存在（根据ID判断）
	 *
	 * @param entity           实体对象，包含主键信息
	 * @param withLogicDeleted 是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntityById)
	boolean existsById(@Param(BindingKeys.ENTITY) E entity
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 判断实体是否存在（根据ID判断，默认不包含逻辑删除数据）
	 *
	 * @param entity 实体对象，包含主键信息
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsById(E entity) {
		return existsById(entity, null);
	}

	/**
	 * 内部方法：根据任意条件判断实体是否存在
	 *
	 * @param entity             查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return Cursor游标对象
	 */
	@ExistsByCount(false)
	@WithLogicDeleted(true)
	@SelectProvider(type = MapperProviders.class, method = MapperProviderKeys.existsEntity)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此方法，实际开发中不直接调用")
	Cursor<Boolean> existsInnerByAny(@Param(BindingKeys.WHERE) Object entity
		, @Param(BindingKeys.WHERE_INCLUDE_ALL_EMPTY) boolean includeAllEmpty
		, @Param(BindingKeys.WHERE_INCLUDE_EMPTY_COLUMNS) Set<String> includeEmptyFields
		, @Param(BindingKeys.WHERE_EXCLUDE_COLUMNS) Set<String> excludeFields
		, @Param(BindingKeys.WITH_LOGIC_DELETED) Boolean withLogicDeleted);

	/**
	 * 判断实体是否存在（根据实体条件判断）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeAllEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，完整参数版本）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，指定包含和排除字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return exists(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，仅指定包含字段）
	 *
	 * @param entity             实体对象，作为查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity, Set<String> includeEmptyFields) {
		return exists(entity, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，指定是否包含所有空值）
	 *
	 * @param entity          实体对象，作为查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity, boolean includeAllEmpty) {
		return exists(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据实体条件判断，基础版本）
	 *
	 * @param entity 实体对象，作为查询条件
	 * @return 存在返回true，否则返回false
	 */
	default boolean exists(E entity) {
		return exists(entity, false);
	}

	/**
	 * 判断实体是否存在（根据Map条件判断）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(entity, includeAllEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 判断实体是否存在（根据Map条件判断，完整参数版本）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByMap(entity, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 判断实体是否存在（根据Map条件判断，指定包含和排除字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByMap(entity, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 判断实体是否存在（根据Map条件判断，仅指定包含字段）
	 *
	 * @param entity             Map形式的查询条件
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity, Set<String> includeEmptyFields) {
		return existsByMap(entity, false, includeEmptyFields, null);
	}

	/**
	 * 判断实体是否存在（根据Map条件判断，指定是否包含所有空值）
	 *
	 * @param entity          Map形式的查询条件
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity, boolean includeAllEmpty) {
		return existsByMap(entity, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据Map条件判断，基础版本）
	 *
	 * @param entity Map形式的查询条件
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByMap(Map<String, Object> entity) {
		return existsByMap(entity, false);
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @param withLogicDeleted   是否处理逻辑删除规则
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields, Boolean withLogicDeleted) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = existsInnerByAny(criteria, includeAllEmpty, includeEmptyFields, excludeFields, withLogicDeleted);
			Iterator<Boolean> iter = cursor.iterator();
			if (iter.hasNext()) {
				Boolean next = iter.next();
				return Boolean.TRUE.equals(next);
			}
			return false;
		} finally {
			IO.close(cursor);
		}
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断，完整参数版本）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeAllEmpty    是否包含所有空值字段
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria, boolean includeAllEmpty, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByCriteria(criteria, includeAllEmpty, includeEmptyFields, excludeFields, null);
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断，指定包含和排除字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @param excludeFields      排除的字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria, Set<String> includeEmptyFields, Set<String> excludeFields) {
		return existsByCriteria(criteria, false, includeEmptyFields, excludeFields);
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断，仅指定包含字段）
	 *
	 * @param criteria           Criteria查询条件对象
	 * @param includeEmptyFields 包含的空值字段集合
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria, Set<String> includeEmptyFields) {
		return existsByCriteria(criteria, false, includeEmptyFields, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断，指定是否包含所有空值）
	 *
	 * @param criteria        Criteria查询条件对象
	 * @param includeAllEmpty 是否包含所有空值字段
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria, boolean includeAllEmpty) {
		return existsByCriteria(criteria, includeAllEmpty, (Set<String>) null, (Set<String>) null);
	}

	/**
	 * 判断实体是否存在（根据Criteria条件判断，基础版本）
	 *
	 * @param criteria Criteria查询条件对象
	 * @return 存在返回true，否则返回false
	 */
	default boolean existsByCriteria(Criteria criteria) {
		return existsByCriteria(criteria, false);
	}

}
