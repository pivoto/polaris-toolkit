package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.polaris.core.reflect.Methods;
import io.polaris.mybatis.type.TypeHandlers;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.LanguageDriverRegistry;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@Slf4j
public class EnhancedConfiguration extends Configuration {

	/** 覆盖原 Configuration 的 MapperRegistry，重写相关的addMapper等方法，增加扩展功能 */
	protected final EnhancedMapperRegistry mapperRegistry = new EnhancedMapperRegistry(this);

	public EnhancedConfiguration(Environment environment) {
		super(environment);
		init();
	}

	public EnhancedConfiguration() {
		super();
		init();
	}

	public EnhancedConfiguration(Configuration origin) throws Throwable {
		super(origin.getEnvironment());
		init();
		copyConfiguration(origin);
	}

	private void init() {
		TypeHandlers.registerAll(this);
		this.typeHandlerRegistry.setDefaultEnumTypeHandler(EnhancedEnumTypeHandler.class);
		this.languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
	}

	// region copy Configuration

	private void copyConfiguration(Configuration origin) throws Throwable {
		this.safeRowBoundsEnabled = origin.isSafeRowBoundsEnabled();
		this.safeResultHandlerEnabled = origin.isSafeResultHandlerEnabled();
		this.mapUnderscoreToCamelCase = origin.isMapUnderscoreToCamelCase();
		this.aggressiveLazyLoading = origin.isAggressiveLazyLoading();
		this.multipleResultSetsEnabled = origin.isMultipleResultSetsEnabled();
		this.useGeneratedKeys = origin.isUseGeneratedKeys();
		this.useColumnLabel = origin.isUseColumnLabel();
		this.cacheEnabled = origin.isCacheEnabled();
		this.callSettersOnNulls = origin.isCallSettersOnNulls();
		this.useActualParamName = origin.isUseActualParamName();
		this.returnInstanceForEmptyRow = origin.isReturnInstanceForEmptyRow();
		this.shrinkWhitespacesInSql = origin.isShrinkWhitespacesInSql();
		this.logPrefix = origin.getLogPrefix();
		this.logImpl = origin.getLogImpl();
		this.vfsImpl = origin.getVfsImpl();
		this.defaultSqlProviderType = origin.getDefaultSqlProviderType();
		this.localCacheScope = origin.getLocalCacheScope();
		this.jdbcTypeForNull = origin.getJdbcTypeForNull();
		this.lazyLoadTriggerMethods = origin.getLazyLoadTriggerMethods();
		this.defaultStatementTimeout = origin.getDefaultStatementTimeout();
		this.defaultFetchSize = origin.getDefaultFetchSize();
		this.defaultResultSetType = origin.getDefaultResultSetType();
		this.defaultExecutorType = origin.getDefaultExecutorType();
		this.autoMappingBehavior = origin.getAutoMappingBehavior();
		this.autoMappingUnknownColumnBehavior = origin.getAutoMappingUnknownColumnBehavior();
		this.variables = origin.getVariables();
		this.reflectorFactory = origin.getReflectorFactory();
		this.objectFactory = origin.getObjectFactory();
		this.objectWrapperFactory = origin.getObjectWrapperFactory();
		this.lazyLoadingEnabled = origin.isLazyLoadingEnabled();
		this.proxyFactory = origin.getProxyFactory();
		this.databaseId = origin.getDatabaseId();
		this.configurationFactory = origin.getConfigurationFactory();

		this.mapperRegistry.copyKnownMappers(origin.getMapperRegistry());
		for (Interceptor interceptor : origin.getInterceptors()) {
			this.interceptorChain.addInterceptor(interceptor);
		}
		copyTypeHandlerRegistry(origin.getTypeHandlerRegistry());
		origin.getTypeAliasRegistry().getTypeAliases().forEach(this.typeAliasRegistry::registerAlias);
		copyLanguageRegistry(origin.getLanguageRegistry());
		copyMappedStatements(origin);
		copyCaches(origin);
		copyResultMaps(origin);
		copyParameterMaps(origin);
		copyKeyGenerators(origin);
		copyLoadedResources(origin);
		copySqlFragments(origin);
		copyIncompleteStatements(origin);
		copyIncompleteCacheRefs(origin);
		copyIncompleteResultMaps(origin);
		copyIncompleteMethods(origin);
		copyCacheRefMap(origin);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistryOrigin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "jdbcTypeHandlerMap", (Class<Map<JdbcType, TypeHandler<?>>>) ((Class) Map.class)
			);
			Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMapOrigin = (Map<JdbcType, TypeHandler<?>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = (Map<JdbcType, TypeHandler<?>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			jdbcTypeHandlerMap.putAll(jdbcTypeHandlerMapOrigin);
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "typeHandlerMap", (Class<Map<Type, Map<JdbcType, TypeHandler<?>>>>) ((Class) Map.class));
			Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMapOrigin = (Map<Type, Map<JdbcType, TypeHandler<?>>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = (Map<Type, Map<JdbcType, TypeHandler<?>>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			typeHandlerMap.putAll(typeHandlerMapOrigin);
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "allTypeHandlersMap", (Class<Map<Class<?>, TypeHandler<?>>>) ((Class) Map.class));
			Map<Class<?>, TypeHandler<?>> allTypeHandlersMapOrigin = (Map<Class<?>, TypeHandler<?>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = (Map<Class<?>, TypeHandler<?>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			allTypeHandlersMap.putAll(allTypeHandlersMapOrigin);
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "defaultEnumTypeHandler", (Class<Class<? extends TypeHandler>>) ((Class) Class.class));
			Class<? extends TypeHandler> defaultEnumTypeHandlerOrigin = (Class<? extends TypeHandler>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			if (defaultEnumTypeHandlerOrigin != null) {
				this.setDefaultEnumTypeHandler(defaultEnumTypeHandlerOrigin);
			}
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyLanguageRegistry(LanguageDriverRegistry languageRegistryOrigin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(LanguageDriverRegistry.class, "LANGUAGE_DRIVER_MAP", (Class<Map<Class<? extends LanguageDriver>, LanguageDriver>>) ((Class) Map.class)
			);
			Map<Class<? extends LanguageDriver>, LanguageDriver> jdbcTypeHandlerMapOrigin = (Map<Class<? extends LanguageDriver>, LanguageDriver>) methodHandle.invokeExact(languageRegistryOrigin);
			Map<Class<? extends LanguageDriver>, LanguageDriver> jdbcTypeHandlerMap = (Map<Class<? extends LanguageDriver>, LanguageDriver>) methodHandle.invokeExact(this.languageRegistry);
			jdbcTypeHandlerMap.putAll(jdbcTypeHandlerMapOrigin);
		}
		this.languageRegistry.setDefaultDriverClass(languageRegistryOrigin.getDefaultDriverClass());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyMappedStatements(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "mappedStatements", (Class<Map<String, MappedStatement>>) ((Class) Map.class)
			);
			Map<String, MappedStatement> mappedStatementsOrigin = (Map<String, MappedStatement>) methodHandle.invokeExact(origin);
			this.mappedStatements.putAll(mappedStatementsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyCaches(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "caches", (Class<Map<String, Cache>>) ((Class) Map.class)
			);
			Map<String, Cache> cachesOrigin = (Map<String, Cache>) methodHandle.invokeExact(origin);
			this.caches.putAll(cachesOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyResultMaps(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "resultMaps", (Class<Map<String, ResultMap>>) ((Class) Map.class)
			);
			Map<String, ResultMap> resultMapOrigin = (Map<String, ResultMap>) methodHandle.invokeExact(origin);
			this.resultMaps.putAll(resultMapOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyParameterMaps(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "parameterMaps", (Class<Map<String, ParameterMap>>) ((Class) Map.class)
			);
			Map<String, ParameterMap> parameterMapsOrigin = (Map<String, ParameterMap>) methodHandle.invokeExact(origin);
			this.parameterMaps.putAll(parameterMapsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyKeyGenerators(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "keyGenerators", (Class<Map<String, KeyGenerator>>) ((Class) Map.class)
			);
			Map<String, KeyGenerator> keyGeneratorsOrigin = (Map<String, KeyGenerator>) methodHandle.invokeExact(origin);
			this.keyGenerators.putAll(keyGeneratorsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyLoadedResources(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "loadedResources", (Class<Set<String>>) ((Class) Set.class)
			);
			Set<String> loadedResourcesOrigin = (Set<String>) methodHandle.invokeExact(origin);
			this.loadedResources.addAll(loadedResourcesOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copySqlFragments(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "sqlFragments", (Class<Map<String, XNode>>) ((Class) Map.class)
			);
			Map<String, XNode> sqlFragmentsOrigin = (Map<String, XNode>) methodHandle.invokeExact(origin);
			this.sqlFragments.putAll(sqlFragmentsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyIncompleteStatements(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "incompleteStatements", (Class<Collection<XMLStatementBuilder>>) ((Class) Collection.class)
			);
			Collection<XMLStatementBuilder> incompleteStatementsOrigin = (Collection<XMLStatementBuilder>) methodHandle.invokeExact(origin);
			this.incompleteStatements.addAll(incompleteStatementsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyIncompleteCacheRefs(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "incompleteCacheRefs", (Class<Collection<CacheRefResolver>>) ((Class) Collection.class)
			);
			Collection<CacheRefResolver> incompleteCacheRefsOrigin = (Collection<CacheRefResolver>) methodHandle.invokeExact(origin);
			this.incompleteCacheRefs.addAll(incompleteCacheRefsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyIncompleteResultMaps(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "incompleteResultMaps", (Class<Collection<ResultMapResolver>>) ((Class) Collection.class)
			);
			Collection<ResultMapResolver> incompleteResultMapsOrigin = (Collection<ResultMapResolver>) methodHandle.invokeExact(origin);
			this.incompleteResultMaps.addAll(incompleteResultMapsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyIncompleteMethods(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "incompleteMethods", (Class<Collection<MethodResolver>>) ((Class) Collection.class)
			);
			Collection<MethodResolver> incompleteMethodsOrigin = (Collection<MethodResolver>) methodHandle.invokeExact(origin);
			this.incompleteMethods.addAll(incompleteMethodsOrigin);
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyCacheRefMap(Configuration origin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(Configuration.class, "cacheRefMap", (Class<Map<String, String>>) ((Class) Map.class)
			);
			Map<String, String> cacheRefMapOrigin = (Map<String, String>) methodHandle.invokeExact(origin);
			this.cacheRefMap.putAll(cacheRefMapOrigin);
		}
	}

	// endregion copy Configuration

	@Override
	public void addMappedStatement(MappedStatement ms) {
		if (ms != null) {
			this.mappedStatements.put(ms.getId(), ms);
		}
	}

	public void putMappedStatement(MappedStatement ms) {
		String statementId = ms.getId();
		if (this.hasStatement(statementId, false)) {
			this.mappedStatements.remove(statementId);
		}
		this.mappedStatements.put(ms.getId(), ms);
	}

	public MappedStatement removeMappedStatement(String statementId) {
		return this.mappedStatements.remove(statementId);
	}

	public void replaceMappedStatement(String statementId, MappedStatement newer) {
		if (this.hasStatement(statementId, false)) {
			MappedStatement existed = this.getMappedStatement(statementId, false);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		}
	}

	public void replaceMappedStatement(String statementId, Function<MappedStatement, MappedStatement> replaced) {
		if (this.hasStatement(statementId, false)) {
			MappedStatement existed = this.getMappedStatement(statementId, false);
			MappedStatement newer = replaced.apply(existed);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		}
	}

	public void addOrReplaceMappedStatement(String statementId, Function<MappedStatement, MappedStatement> replaced) {
		if (this.hasStatement(statementId, false)) {
			MappedStatement existed = this.getMappedStatement(statementId, false);
			MappedStatement newer = replaced.apply(existed);
			this.mappedStatements.remove(statementId);
			this.addMappedStatement(newer);
		} else {
			this.addMappedStatement(replaced.apply(null));
		}
	}


	@Override
	public void setDefaultScriptingLanguage(Class<? extends LanguageDriver> driver) {
		// 覆盖原方法，增加扩展功能
		if (driver == null) {
			driver = XMLLanguageDriver.class;
		}
		getLanguageRegistry().setDefaultDriverClass(driver);
	}

	@Override
	public void setDefaultEnumTypeHandler(Class<? extends TypeHandler> typeHandler) {
		// 覆盖原方法，增加扩展功能
		EnhancedEnumTypeHandler.setDefaultEnumTypeHandler(typeHandler);
	}

	@Override
	public MapperRegistry getMapperRegistry() {
		return mapperRegistry;
	}

	@Override
	public void addMappers(String packageName, Class<?> superType) {
		mapperRegistry.addMappers(packageName, superType);
	}

	@Override
	public void addMappers(String packageName) {
		mapperRegistry.addMappers(packageName);
	}

	@Override
	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	@Override
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mapperRegistry.getMapper(type, sqlSession);
	}

	@Override
	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}
}
