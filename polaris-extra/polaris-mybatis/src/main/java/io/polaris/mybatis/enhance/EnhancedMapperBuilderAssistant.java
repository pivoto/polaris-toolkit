package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import io.polaris.core.err.UncheckedPassException;
import io.polaris.core.reflect.Methods;
import io.polaris.mybatis.interceptor.StatementBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@Slf4j
public class EnhancedMapperBuilderAssistant extends MapperBuilderAssistant {

	// region private static

	private static final MethodHandle getStatementParameterMap = find$getStatementParameterMap();
	private static final MethodHandle getStatementResultMaps = find$getStatementResultMaps();
	private static final MethodHandle currentCache = find$currentCache();
	private static final MethodHandle unresolvedCacheRef = find$unresolvedCacheRef();

	private static MethodHandle find$unresolvedCacheRef() {
		try {
			return Methods.findPrivateGetter(MapperBuilderAssistant.class, "unresolvedCacheRef", boolean.class);
		} catch (Throwable e) {
			log.error("fail to find MapperBuilderAssistant.unresolvedCacheRef", e);
			return null;
		}
	}
	private static MethodHandle find$currentCache() {
		try {
			return Methods.findPrivateGetter(MapperBuilderAssistant.class, "currentCache", Cache.class);
		} catch (Throwable e) {
			log.error("fail to find MapperBuilderAssistant.currentCache", e);
			return null;
		}
	}

	private static MethodHandle find$getStatementParameterMap() {
		try {
			return Methods.findPrivate(MapperBuilderAssistant.class, "getStatementParameterMap", ParameterMap.class,
				new Class[]{String.class, Class.class, String.class});
		} catch (Throwable e) {
			log.error("fail to find MapperBuilderAssistant.getStatementParameterMap", e);
			return null;
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static MethodHandle find$getStatementResultMaps() {
		try {
			Class<List<ResultMap>> returnType = (Class) List.class;
			return Methods.findPrivate(MapperBuilderAssistant.class, "getStatementResultMaps", returnType,
				new Class[]{String.class, Class.class, String.class});
		} catch (Throwable e) {
			log.error("fail to find MapperBuilderAssistant.getStatementResultMaps", e);
			return null;
		}
	}

	// endregion private static

	private final String resource;
	private final Configuration configuration;
	private final MapperBuilderAssistant raw;
	private final ConfigurationHelper configurationHelper;

	private final transient AtomicReference<EnhancedAutoMapperMeta> autoMapperMetaRef = new AtomicReference<>();

	public EnhancedMapperBuilderAssistant(Configuration configuration, String resource, MapperBuilderAssistant assistant) {
		super(configuration, resource);
		this.configuration = configuration;
		this.resource = resource;
		this.raw = assistant;
		if (configuration instanceof EnhancedConfiguration) {
			configurationHelper = ConfigurationHelper.of(configuration);
		} else {
			configurationHelper = null;
		}
	}

	// region private assistant

	boolean unresolvedCacheRef(){
		try {
			if (unresolvedCacheRef == null) {
				return false;
			}
			return (boolean) unresolvedCacheRef.invokeExact((MapperBuilderAssistant) this.raw);
		} catch (Throwable e) {
			throw new UncheckedPassException(e, "fail to get MapperBuilderAssistant.unresolvedCacheRef");
		}
	}

	Cache currentCache() {
		try {
			if (currentCache == null) {
				return null;
			}
			return (Cache) currentCache.invokeExact((MapperBuilderAssistant) this.raw);
		} catch (Throwable e) {
			throw new UncheckedPassException(e, "fail to get MapperBuilderAssistant.currentCache");
		}
	}

	ParameterMap getStatementParameterMap(String parameterMapName, Class<?> parameterTypeClass, String statementId) {
		try {
			if (getStatementParameterMap == null) {
				return null;
			}
			return (ParameterMap) getStatementParameterMap.invokeExact((MapperBuilderAssistant) this.raw, parameterMapName, parameterTypeClass, statementId);
		} catch (Throwable e) {
			log.error("fail to invoke MapperBuilderAssistant.getStatementParameterMap", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	List<ResultMap> getStatementResultMaps(String resultMap, Class<?> resultType, String statementId) {
		try {
			if (getStatementResultMaps == null) {
				return new ArrayList<>();
			}
			return (List<ResultMap>) getStatementResultMaps.invokeExact((MapperBuilderAssistant) this.raw, resultMap, resultType, statementId);
		} catch (Throwable e) {
			log.error("fail to invoke MapperBuilderAssistant.getStatementParameterMap", e);
			return new ArrayList<>();
		}
	}

	private <T> T valueOrDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

	// endregion private assistant


	public EnhancedAutoMapperMeta pushAutoMapperMeta(EnhancedAutoMapperMeta autoMapperMeta) {
		EnhancedAutoMapperMeta meta = this.autoMapperMetaRef.get();
		this.autoMapperMetaRef.set(autoMapperMeta);
		return meta;
	}

	@Nullable
	public EnhancedAutoMapperMeta popAutoMapperMeta() {
		EnhancedAutoMapperMeta meta = this.autoMapperMetaRef.get();
		this.autoMapperMetaRef.set(null);
		return meta;
	}


	public StatementBuilder buildMappedStatement(
		String id,
		SqlSource sqlSource,
		StatementType statementType,
		SqlCommandType sqlCommandType,
		Integer fetchSize,
		Integer timeout,
		String parameterMap,
		Class<?> parameterType,
		String resultMap,
		Class<?> resultType,
		ResultSetType resultSetType,
		boolean flushCache,
		boolean useCache,
		boolean resultOrdered,
		KeyGenerator keyGenerator,
		String keyProperty,
		String keyColumn,
		String databaseId,
		LanguageDriver lang,
		String resultSets) {

		id = raw.applyCurrentNamespace(id, false);
		boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

		StatementBuilder statementBuilder = new StatementBuilder(configuration, id, sqlSource, sqlCommandType);
		statementBuilder.resource(resource)
			.fetchSize(fetchSize)
			.timeout(timeout)
			.statementType(statementType)
			.keyGenerator(keyGenerator)
			.keyProperty(keyProperty)
			.keyColumn(keyColumn)
			.databaseId(databaseId)
			.lang(lang)
			.resultOrdered(resultOrdered)
			.resultSets(resultSets)
			.resultMaps(getStatementResultMaps(resultMap, resultType, id))
			.resultSetType(resultSetType)
			.flushCacheRequired(valueOrDefault(flushCache, !isSelect))
			.useCache(valueOrDefault(useCache, isSelect))
			.cache(currentCache());

		ParameterMap statementParameterMap = getStatementParameterMap(parameterMap, parameterType, id);
		if (statementParameterMap != null) {
			statementBuilder.parameterMap(statementParameterMap);
		}

		// 使用预置的自动映射元数据进一步扩展
		EnhancedAutoMapperMeta meta = popAutoMapperMeta();
		if (meta != null) {
			meta.applyTo(statementBuilder, sqlCommandType);
		}
		return statementBuilder;
	}


	public StatementBuilder buildMappedStatement(String id, SqlSource sqlSource, StatementType statementType,
		SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, Class<?> parameterType,
		String resultMap, Class<?> resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache,
		boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty, String keyColumn, String databaseId,
		LanguageDriver lang) {
		return buildMappedStatement(
			id, sqlSource, statementType, sqlCommandType, fetchSize, timeout,
			parameterMap, parameterType, resultMap, resultType, resultSetType,
			flushCache, useCache, resultOrdered, keyGenerator, keyProperty,
			keyColumn, databaseId, lang, null);
	}


	@Override
	public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType, SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty, String keyColumn, String databaseId, LanguageDriver lang, String resultSets) {
		//return assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator, keyProperty, keyColumn, databaseId, lang, resultSets);

		if (unresolvedCacheRef()) {
			throw new IncompleteElementException("Cache-ref not yet resolved");
		}

		StatementBuilder statementBuilder = this.buildMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator, keyProperty, keyColumn, databaseId, lang, resultSets);

		MappedStatement statement = statementBuilder.build();
		if (configuration instanceof EnhancedConfiguration) {
			((EnhancedConfiguration) configuration).putMappedStatement(statement);
		} else {
			//configuration.addMappedStatement(statement);
			configurationHelper.putMappedStatement(statement);
		}
		return statement;
	}

	@Override
	public MappedStatement addMappedStatement(String id, SqlSource sqlSource, StatementType statementType, SqlCommandType sqlCommandType, Integer fetchSize, Integer timeout, String parameterMap, Class<?> parameterType, String resultMap, Class<?> resultType, ResultSetType resultSetType, boolean flushCache, boolean useCache, boolean resultOrdered, KeyGenerator keyGenerator, String keyProperty, String keyColumn, String databaseId, LanguageDriver lang) {
		//return assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterType, resultMap, resultType, resultSetType, flushCache, useCache, resultOrdered, keyGenerator, keyProperty, keyColumn, databaseId, lang);
		return addMappedStatement(
			id, sqlSource, statementType, sqlCommandType, fetchSize, timeout,
			parameterMap, parameterType, resultMap, resultType, resultSetType,
			flushCache, useCache, resultOrdered, keyGenerator, keyProperty,
			keyColumn, databaseId, lang, null);
	}

	// region delegate

	@Override
	public String getCurrentNamespace() {
		return raw.getCurrentNamespace();
	}

	@Override
	public void setCurrentNamespace(String currentNamespace) {
		raw.setCurrentNamespace(currentNamespace);
	}

	@Override
	public String applyCurrentNamespace(String base, boolean isReference) {
		return raw.applyCurrentNamespace(base, isReference);
	}

	@Override
	public Cache useCacheRef(String namespace) {
		return raw.useCacheRef(namespace);
	}

	@Override
	public Cache useNewCache(Class<? extends Cache> typeClass, Class<? extends Cache> evictionClass, Long flushInterval, Integer size, boolean readWrite, boolean blocking, Properties props) {
		return raw.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
	}

	@Override
	public ParameterMap addParameterMap(String id, Class<?> parameterClass, List<ParameterMapping> parameterMappings) {
		return raw.addParameterMap(id, parameterClass, parameterMappings);
	}

	@Override
	public ParameterMapping buildParameterMapping(Class<?> parameterType, String property, Class<?> javaType, JdbcType jdbcType, String resultMap, ParameterMode parameterMode, Class<? extends TypeHandler<?>> typeHandler, Integer numericScale) {
		return raw.buildParameterMapping(parameterType, property, javaType, jdbcType, resultMap, parameterMode, typeHandler, numericScale);
	}

	@Override
	public ResultMap addResultMap(String id, Class<?> type, String extend, Discriminator discriminator, List<ResultMapping> resultMappings, Boolean autoMapping) {
		return raw.addResultMap(id, type, extend, discriminator, resultMappings, autoMapping);
	}

	@Override
	public Discriminator buildDiscriminator(Class<?> resultType, String column, Class<?> javaType, JdbcType jdbcType, Class<? extends TypeHandler<?>> typeHandler, Map<String, String> discriminatorMap) {
		return raw.buildDiscriminator(resultType, column, javaType, jdbcType, typeHandler, discriminatorMap);
	}

	@Override
	public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, Class<?> javaType, JdbcType jdbcType, String nestedSelect, String nestedResultMap, String notNullColumn, String columnPrefix, Class<? extends TypeHandler<?>> typeHandler, List<ResultFlag> flags, String resultSet, String foreignColumn, boolean lazy) {
		return raw.buildResultMapping(resultType, property, column, javaType, jdbcType, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandler, flags, resultSet, foreignColumn, lazy);
	}

	@Override
	public ResultMapping buildResultMapping(Class<?> resultType, String property, String column, Class<?> javaType, JdbcType jdbcType, String nestedSelect, String nestedResultMap, String notNullColumn, String columnPrefix, Class<? extends TypeHandler<?>> typeHandler, List<ResultFlag> flags) {
		return raw.buildResultMapping(resultType, property, column, javaType, jdbcType, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandler, flags);
	}

	@Deprecated
	@Override
	public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
		return raw.getLanguageDriver(langClass);
	}


	// endregion delegate

}
