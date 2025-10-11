package io.polaris.mybatis.interceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import io.polaris.core.jdbc.ColumnMeta;
import io.polaris.core.jdbc.TableMeta;
import io.polaris.core.jdbc.TableMetaKit;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import io.polaris.mybatis.annotation.DynamicUseGeneratedKeys;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.mapper.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 动态启用 MyBatis 的 useGeneratedKeys 特性的拦截器。
 * <p>
 * 该拦截器用于在执行 INSERT 操作时动态判断是否需要开启 useGeneratedKeys，
 * 并根据实体类中的自增列信息构建新的 MappedStatement 来替换原始语句。
 * </p>
 *
 * @deprecated  改用 {@link DynamicResultMappingInterceptor}
 * @author Qt
 * @since Jul 04, 2024
 */
@Slf4j
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
@Deprecated
public class DynamicUseGeneratedKeysInterceptor extends DynamicResultMappingInterceptor {
}
