package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.polaris.core.jdbc.sql.BindingValues;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.sql.SqlStatements;
import io.polaris.core.jdbc.sql.consts.BindingKeys;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.jdbc.sql.statement.DeleteStatement;
import io.polaris.core.jdbc.sql.statement.InsertStatement;
import io.polaris.core.jdbc.sql.statement.MergeStatement;
import io.polaris.core.jdbc.sql.statement.SelectStatement;
import io.polaris.core.jdbc.sql.statement.SqlNodeBuilder;
import io.polaris.core.jdbc.sql.statement.UpdateStatement;
import io.polaris.core.tuple.Tuple2;
import io.polaris.mybatis.annotation.WithLogicDeleted;
import io.polaris.mybatis.consts.MappingKeys;
import io.polaris.mybatis.support.MapperHelperContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since Mar 30, 2026
 */
@Slf4j
public class EnhancedAutoMappers {

	public static void log(String sql, Map<String, Object> map) {
		if (log.isDebugEnabled()) {
			log.debug("SQL-TEXT: {}", sql);
			log.debug("SQL-ARGS: {}", map);
		}
	}

	public static String selectAny(Map<String, Object> map, ProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;
		function = EntityStatements.buildSqlSelectFunction(mapperMethod);
		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}


	public static String updateAny(Map<String, Object> map, ProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		Function<Map<String, Object>, SqlNode> function;
		function = EntityStatements.buildSqlUpdateFunction(mapperMethod);
		SqlNode sqlNode = function.apply(map);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String executeAnySql(Map<String, Object> map, ProviderContext ctx) {
		Object obj = map.get(BindingKeys.SQL);
		String sql = null;
		if (obj instanceof SqlNode) {
			sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNode) obj);
		} else if (obj instanceof SqlNodeBuilder) {
			sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, (SqlNodeBuilder) obj);
		} else if (obj instanceof String) {
			sql = (String) obj;
		} else {
			throw new IllegalArgumentException();
		}
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String insertBySql(Map<String, Object> map, ProviderContext ctx) {
		InsertStatement<?> st = (InsertStatement<?>) map.get(BindingKeys.INSERT);
		if (st == null) {
			st = (InsertStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String deleteBySql(Map<String, Object> map, ProviderContext ctx) {
		DeleteStatement<?> st = (DeleteStatement<?>) map.get(BindingKeys.DELETE);
		if (st == null) {
			st = (DeleteStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String updateBySql(Map<String, Object> map, ProviderContext ctx) {
		UpdateStatement<?> st = (UpdateStatement<?>) map.get(BindingKeys.UPDATE);
		if (st == null) {
			st = (UpdateStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String selectBySql(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		if (withLogicDeleted) {
			st.exceptLogicDeleted(true, true, true);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[Sql]{}", sql);
			log.debug("[Var]{}", map);
		}
		return sql;
	}

	public static String selectBySqlDirect(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[Sql]{}", sql);
			log.debug("[Var]{}", map);
		}
		return sql;
	}
	public static String selectBySqlExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		st.exceptLogicDeleted(true, true, true);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		if (log.isDebugEnabled()) {
			log.debug("[Sql]{}", sql);
			log.debug("[Var]{}", map);
		}
		return sql;
	}

	public static String countBySql(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		if (withLogicDeleted) {
			st.exceptLogicDeleted(true,true,true);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}


	public static String countBySqlDirect(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String countBySqlExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
		if (st == null) {
			st = (SelectStatement<?>) map.get(BindingKeys.SQL);
		}
		st.exceptLogicDeleted(true, true, true);
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st::toCountSqlNode);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String existsBySql(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
			if (st == null) {
				st = (SelectStatement<?>) map.get(BindingKeys.SQL);
			}
			boolean withLogicDeleted = withLogicDeleted(map, ctx);
			if (withLogicDeleted) {
				st.exceptLogicDeleted(true, true, true);
			}
			SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
			String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}
	public static String existsBySqlDirect(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
			if (st == null) {
				st = (SelectStatement<?>) map.get(BindingKeys.SQL);
			}
			SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
			String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}
	public static String existsBySqlExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			SelectStatement<?> st = (SelectStatement<?>) map.get(BindingKeys.SELECT);
			if (st == null) {
				st = (SelectStatement<?>) map.get(BindingKeys.SQL);
			}
			st.exceptLogicDeleted(true, true, true);
			SqlNode sqlNode = st.toExistsSqlNode(queryByCount);
			String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, sqlNode);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}

	public static String mergeBySql(Map<String, Object> map, ProviderContext ctx) {
		MergeStatement<?> st = (MergeStatement<?>) map.get(BindingKeys.MERGE);
		if (st == null) {
			st = (MergeStatement<?>) map.get(BindingKeys.SQL);
		}
		String sql = BindingValues.asSqlWithBindings(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, st);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String insertEntity(Map<String, Object> map, ProviderContext context) {
		String sql = SqlStatements.buildInsert(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(context));
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String deleteEntityById(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildDeleteById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String directDeleteEntityById(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildDeleteById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String logicDeleteEntityById(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildDeleteById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String deleteEntityByAny(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildDeleteByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String directDeleteEntityByAny(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildDeleteByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String logicDeleteEntityByAny(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildDeleteByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String updateEntityById(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildUpdateById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx));
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String updateEntityByAny(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildUpdateByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx));
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String existsEntity(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			boolean withLogicDeleted = withLogicDeleted(map, ctx);
			String sql = SqlStatements.buildExistsByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), queryByCount, withLogicDeleted);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}

	public static String existsEntityDirect(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			String sql = SqlStatements.buildExistsByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), queryByCount, false);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}
	public static String existsEntityExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		// 是否不通过Count(*)查询，表示使用游标查询或通过分页插件处理，接收返回值为布尔列表
		boolean queryByCount = MapperHelperContext.isQueryExistsByCount();
		try {
			String sql = SqlStatements.buildExistsByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), queryByCount, true);
			EnhancedAutoMappers.log(sql, map);
			return sql;
		} finally {
			MapperHelperContext.clearQueryExistsByCount();
		}
	}
	public static String existsEntityById(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildExistsById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String existsEntityByIdDirect(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildExistsById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String existsEntityByIdExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildExistsById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String selectEntity(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildSelectByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String selectEntityDirect(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildSelectByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}
	public static String selectEntityExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildSelectByAny(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String selectEntityById(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildSelectById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String selectEntityByIdDirect(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildSelectById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}


	public static String selectEntityByIdExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildSelectById(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String countEntity(Map<String, Object> map, ProviderContext ctx) {
		boolean withLogicDeleted = withLogicDeleted(map, ctx);
		String sql = SqlStatements.buildCount(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), withLogicDeleted);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}

	public static String countEntityDirect(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildCount(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), false);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}


	public static String countEntityExceptLogicDeleted(Map<String, Object> map, ProviderContext ctx) {
		String sql = SqlStatements.buildCount(MappingKeys.PARAMETER_MAPPING_KEYS_FILTER, map, getEntityClass(ctx), true);
		EnhancedAutoMappers.log(sql, map);
		return sql;
	}






	private static final Map<Tuple2<Method, Class<?>>, Boolean> LOGIC_DELETED_MAP = new ConcurrentHashMap<>();

	protected static Class<?> getEntityClass(ProviderContext context) {
		return EnhancedAutoMapperHelper.getEntityClassOrThrows(context);
	}

	protected static boolean withLogicDeleted(Map<String, Object> bindings, ProviderContext context) {
		try {
			// fix for MapperMethod.ParamMap.get()
			if (bindings.containsKey(BindingKeys.WITH_LOGIC_DELETED)) {
				Object sld = bindings.get(BindingKeys.WITH_LOGIC_DELETED);
				if (sld != null) {
					if (sld instanceof Boolean) {
						return (Boolean) sld;
					}
				}
			}
		} catch (Exception ignored) {
		}
		return withLogicDeleted(context);
	}
	protected static boolean withLogicDeleted(ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();
		Class<?> mapperType = context.getMapperType();

		return LOGIC_DELETED_MAP.computeIfAbsent(Tuple2.of(mapperMethod, context.getMapperType()),
			k -> innerGetWithLogicDeleted(mapperMethod, mapperType));
	}

	private static boolean innerGetWithLogicDeleted(Method mapperMethod, Class<?> mapperType) {
		// 从方法上获取
		WithLogicDeleted declared = mapperMethod.getAnnotation(WithLogicDeleted.class);
		Class<?> entityClass = null;
		if (declared != null) {
			return declared.value();
		}
		// 从Mapper接口类上获取
		declared = mapperType.getAnnotation(WithLogicDeleted.class);
		if (declared != null) {
			return declared.value();
		}
		// 从方法所在接口类上获取
		Class<?> declaringClass = mapperMethod.getDeclaringClass();
		if (declaringClass != mapperType) {
			declared = declaringClass.getAnnotation(WithLogicDeleted.class);
			if (declared != null) {
				return declared.value();
			}
		}
		return false;
	}

}
