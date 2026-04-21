package io.polaris.mybatis.enhance.mapper;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.annotation.Internal;
import io.polaris.core.io.IO;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.ColumnPredicate;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.annotation.AutoResultMapping;
import io.polaris.mybatis.annotation.AutoUseGeneratedKeys;
import io.polaris.mybatis.annotation.SupportLogicDeleted;
import io.polaris.mybatis.consts.AutoMapperType;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Apr 17, 2026
 */
public interface EntitySqlAutoMapper<E> extends EntityMapper<E> {

	/**
	 * 根据提供的 InsertStatement 对象执行插入操作。
	 *
	 * @param statement 包含插入语句信息的对象
	 * @return 插入操作影响的行数
	 */
	@AutoMapper(type = AutoMapperType.insertBySql)
	int insertBySql(@Param(BindingKeys.SQL) InsertStatement<?> statement);

	/**
	 * 根据提供的UpdateStatement执行更新操作。
	 *
	 * @param statement 包含更新SQL语句和参数的UpdateStatement对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.updateBySql)
	int updateBySql(@Param(BindingKeys.SQL) UpdateStatement<?> statement);

	/**
	 * 根据提供的删除语句执行删除操作。
	 *
	 * @param statement 包含删除条件和表信息的DeleteStatement对象
	 * @return 受影响的行数
	 */
	@AutoMapper(type = AutoMapperType.deleteBySql)
	int deleteBySql(@Param(BindingKeys.SQL) DeleteStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录（内部方法，不建议直接调用）
	 *
	 * @param statement SQL查询语句对象
	 * @return 游标结果，包含布尔值表示是否存在匹配记录
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoMapper(type = AutoMapperType.select1BySql)
	@Options(fetchSize = 1)
	@Internal("考虑到查询性能与分页实现的兼容性，声明此此方法，实际开发中不直接调用")
	Cursor<Boolean> _cursorExistsBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 检查是否存在满足条件的记录
	 *
	 * @param statement SQL查询语句对象
	 * @return 是否存在匹配记录
	 */
	default boolean existsBySql(SelectStatement<?> statement) {
		Cursor<Boolean> cursor = null;
		try {
			cursor = _cursorExistsBySql(statement);
			if (cursor == null) {
				return false;
			}
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
	 * 统计满足条件的记录数量
	 *
	 * @param statement SQL查询语句对象
	 * @return 记录总数
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoMapper(type = AutoMapperType.countBySql)
	int countBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询实体列表
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象列表
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	List<E> selectEntityListBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 查询单个实体对象
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	E selectEntityBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

	/**
	 * 根据SQL语句查询实体对象的游标结果。
	 *
	 * @param statement SQL查询语句对象
	 * @return 实体对象的游标结果
	 */
	@SupportLogicDeleted(true) // 是否处理逻辑删除
	@AutoResultMapping
	@AutoMapper(type = AutoMapperType.selectBySql)
	@Options(fetchSize = 1000)
	Cursor<E> selectEntityCursorBySql(@Param(BindingKeys.SQL) SelectStatement<?> statement);

}
