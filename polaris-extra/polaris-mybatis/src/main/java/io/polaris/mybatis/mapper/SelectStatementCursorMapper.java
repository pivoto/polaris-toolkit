package io.polaris.mybatis.mapper;

import java.util.Map;

import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.query.Criteria;
import io.polaris.core.jdbc.sql.query.OrderBy;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.mybatis.annotation.DynamicResultMapping;
import io.polaris.mybatis.consts.MapperProviderKeys;
import io.polaris.mybatis.provider.MapperProviders;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.cursor.Cursor;

/**
 * @author Qt
 * @since Aug 25, 2023
 */
public interface SelectStatementCursorMapper<R> extends
	SelectStatementCursorDefaultMapper<R>,
	SelectStatementCursorDirectMapper<R> {

}
