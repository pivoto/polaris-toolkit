package io.polaris.mybatis.mapper;

import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.AutoResultMapping;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * 游标查询映射器接口，用于执行基于 {@link SelectStatement} 的游标查询操作。
 *
 * <p>该接口继承了 {@link SelectStatementCursorDefaultMapper} 和 {@link SelectStatementCursorDirectMapper}，
 * 提供了默认和直接的游标查询功能。泛型参数 {@code R} 表示查询结果的类型。
 *
 * <p>主要特性：
 * <ul>
 *   <li>支持动态结果映射（通过 {@link AutoResultMapping} 注解）</li>
 *   <li>集成 MyBatis 的 {@link SelectProvider} 和 {@link Options} 注解</li>
 *   <li>返回 {@link Cursor} 类型的结果，适用于大数据量的流式处理</li>
 * </ul>
 *
 * @param <R> 查询结果的类型
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementCursorMapper<R> extends
	SelectStatementCursorDefaultMapper<R>,
	SelectStatementCursorDirectMapper<R> {

}
