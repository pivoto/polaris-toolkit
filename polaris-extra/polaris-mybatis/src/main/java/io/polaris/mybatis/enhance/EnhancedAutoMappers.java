package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

import io.polaris.core.jdbc.sql.BindingKeyModel;
import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.SqlStatements;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.mybatis.consts.MappingPropKeys;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
@Slf4j
public class EnhancedAutoMappers {

	public static String selectAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;

		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		function = EntityStatements.buildSqlSelectFunction(mapperMethod, bindingKeyModel);

		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}


	public static String updateAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		function = EntityStatements.buildSqlUpdateFunction(mapperMethod, bindingKeyModel);
		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String executeAnySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		String sql = null;
		if (obj instanceof SqlNode) {
			sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNode) obj);
		} else if (obj instanceof SqlNodeBuilder) {
			sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNodeBuilder) obj);
		} else if (obj instanceof String) {
			sql = (String) obj;
		} else {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String insertBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof InsertStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		InsertStatement<?> st = (InsertStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String deleteBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof DeleteStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		DeleteStatement<?> st = (DeleteStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String updateBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof UpdateStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		UpdateStatement<?> st = (UpdateStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		if (withLogicDeleted) {
			st.exceptLogicDeleted(true, true, true);
		}
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectBySqlDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectBySqlExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		st.exceptLogicDeleted(true, true, true);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String countBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		if (withLogicDeleted) {
			st.exceptLogicDeleted(true, true, true);
		}
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}


	public static String countBySqlDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String countBySqlExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		st.exceptLogicDeleted(true, true, true);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		if (withLogicDeleted) {
			st.exceptLogicDeleted(true, true, true);
		}
		// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
		SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsBySqlDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof SelectStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		SelectStatement<?> st = (SelectStatement<?>) obj;
		// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
		SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsBySqlExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
			BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
			String sqlKey = bindingKeyModel.sql();
			Object obj = map.get(sqlKey);
			if (obj == null) {
				throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
			}
			if (!(obj instanceof SelectStatement)) {
				throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
			}
			SelectStatement<?> st = (SelectStatement<?>) obj;
			// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
			boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
			st.exceptLogicDeleted(true, true, true);
			SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
			String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
			if (log.isDebugEnabled()) {
				log.debug("[SQL-TEXT]{}", sql);
				log.debug("[SQL-ARGS]{}", map);
			}
			return sql;
	}

	public static String mergeBySql(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sqlKey = bindingKeyModel.sql();
		Object obj = map.get(sqlKey);
		if (obj == null) {
			throw new IllegalArgumentException("缺少SQL参数键值：`" + sqlKey + "`");
		}
		if (!(obj instanceof MergeStatement)) {
			throw new IllegalArgumentException("SQL参数键值：`" + sqlKey + "` 的对象类型不支持");
		}
		MergeStatement<?> st = (MergeStatement<?>) obj;
		String sql = BindingValues.asSqlWithBindings(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String insertEntity(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildInsert(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String deleteEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildDeleteById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String directDeleteEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildDeleteById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String logicDeleteEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildDeleteById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String deleteEntityByAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildDeleteByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String directDeleteEntityByAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildDeleteByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String logicDeleteEntityByAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildDeleteByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String updateEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildUpdateById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String updateEntityByAny(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildUpdateByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsEntity(Map<String, Object> map, EnhancedProviderContext ctx) {
			BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
			boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
			// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
			boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
			String sql = SqlStatements.buildExistsByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), queryByCount, withLogicDeleted, bindingKeyModel);
			if (log.isDebugEnabled()) {
				log.debug("[SQL-TEXT]{}", sql);
				log.debug("[SQL-ARGS]{}", map);
			}
			return sql;
	}

	public static String existsEntityDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
			BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
			// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
			boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
			String sql = SqlStatements.buildExistsByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), queryByCount, false, bindingKeyModel);
			if (log.isDebugEnabled()) {
				log.debug("[SQL-TEXT]{}", sql);
				log.debug("[SQL-ARGS]{}", map);
			}
			return sql;
	}

	public static String existsEntityExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
			BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
			// 是否通过Count(*)查询，否则表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
			boolean queryByCount = ctx.isExistsByCount(map, bindingKeyModel);
			String sql = SqlStatements.buildExistsByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), queryByCount, true, bindingKeyModel);
			if (log.isDebugEnabled()) {
				log.debug("[SQL-TEXT]{}", sql);
				log.debug("[SQL-ARGS]{}", map);
			}
			return sql;
	}

	public static String existsEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildExistsById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsEntityByIdDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildExistsById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String existsEntityByIdExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildExistsById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectEntity(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildSelectByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectEntityDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildSelectByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectEntityExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildSelectByAny(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectEntityById(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildSelectById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String selectEntityByIdDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildSelectById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}


	public static String selectEntityByIdExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildSelectById(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String countEntity(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		boolean withLogicDeleted = ctx.isWithLogicDeleted(map, bindingKeyModel);
		String sql = SqlStatements.buildCount(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), withLogicDeleted, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}

	public static String countEntityDirect(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildCount(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), false, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}


	public static String countEntityExceptLogicDeleted(Map<String, Object> map, EnhancedProviderContext ctx) {
		BindingKeyModel bindingKeyModel = ctx.getBindingKeyDefinition().toBindingKeyModel(map);
		String sql = SqlStatements.buildCount(MappingPropKeys.PARAMETER_MAPPING_KEYS_FILTER, map, ctx.getEntityClassOrThrows(), true, bindingKeyModel);
		if (log.isDebugEnabled()) {
			log.debug("[SQL-TEXT]{}", sql);
			log.debug("[SQL-ARGS]{}", map);
		}
		return sql;
	}


}
