package io.polaris.mybatis.enhance;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import io.polaris.core.reflect.Methods;
import io.polaris.mybatis.enhance.log.DebuggableLog;
import io.polaris.mybatis.enhance.log.OriginalLogFactory;
import io.polaris.mybatis.type.TypeHandlers;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.builder.annotation.MethodResolver;
import org.apache.ibatis.builder.xml.XMLStatementBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
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
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.apache.ibatis.type.UnknownTypeHandler;

/**
 * @author Qt
 * @since Mar 28, 2026
 */
@Slf4j
public class EnhancedConfiguration extends Configuration {

	/** 覆盖原 Configuration 的 MapperRegistry，重写相关的addMapper等方法，增加扩展功能 */
	protected final EnhancedMapperRegistry mapperRegistry = new EnhancedMapperRegistry(this);
	/** 是否强制使用DebuggableLog，用于实现在SQL拦截器中获取绑定语句与参数 */
	protected boolean useDebuggableLog = true;

	public EnhancedConfiguration(Environment environment) {
		super(environment);
		init();
	}

	public EnhancedConfiguration() {
		super();
		init();
	}

	private void init() {
		TypeHandlers.registerAll(this);
		this.typeHandlerRegistry.setDefaultEnumTypeHandler(EnhancedEnumTypeHandler.class);
		this.languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
	}

	// region copy Configuration

	public EnhancedConfiguration copyConfiguration(Configuration origin) throws Throwable {
		// environment
		this.environment = origin.getEnvironment();
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
		// 重写 setLogImpl
		this.setLogImpl(origin.getLogImpl());
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
			this.addInterceptor(interceptor);
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
		return this;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private void copyTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistryOrigin) throws Throwable {
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "jdbcTypeHandlerMap", (Class<Map<JdbcType, TypeHandler<?>>>) ((Class) Map.class)
			);
			Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMapOrigin = (Map<JdbcType, TypeHandler<?>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<JdbcType, TypeHandler<?>> jdbcTypeHandlerMap = (Map<JdbcType, TypeHandler<?>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			jdbcTypeHandlerMapOrigin.forEach((jdbcType, typeHandler) -> {
				typeHandler = updateTypeHandlerConfiguration(typeHandler);
				jdbcTypeHandlerMap.put(jdbcType, typeHandler);
			});
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "typeHandlerMap", (Class<Map<Type, Map<JdbcType, TypeHandler<?>>>>) ((Class) Map.class));
			Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMapOrigin = (Map<Type, Map<JdbcType, TypeHandler<?>>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<Type, Map<JdbcType, TypeHandler<?>>> typeHandlerMap = (Map<Type, Map<JdbcType, TypeHandler<?>>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			typeHandlerMapOrigin.forEach((type, jdbcTypeHandlerMap) -> {
				for (Map.Entry<JdbcType, TypeHandler<?>> entry : jdbcTypeHandlerMap.entrySet()) {
					TypeHandler<?> typeHandler = entry.getValue();
					TypeHandler<?> updated = updateTypeHandlerConfiguration(typeHandler);
					if (typeHandler != updated) {
						entry.setValue(updated);
					}
				}
				typeHandlerMap.put(type, jdbcTypeHandlerMap);
			});
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "allTypeHandlersMap", (Class<Map<Class<?>, TypeHandler<?>>>) ((Class) Map.class));
			Map<Class<?>, TypeHandler<?>> allTypeHandlersMapOrigin = (Map<Class<?>, TypeHandler<?>>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			Map<Class<?>, TypeHandler<?>> allTypeHandlersMap = (Map<Class<?>, TypeHandler<?>>) methodHandle.invokeExact(this.typeHandlerRegistry);
			allTypeHandlersMapOrigin.forEach((type, typeHandler) -> {
				typeHandler = updateTypeHandlerConfiguration(typeHandler);
				allTypeHandlersMap.put(type, typeHandler);
			});
		}
		{
			MethodHandle methodHandle = Methods.findPrivateGetter(TypeHandlerRegistry.class, "defaultEnumTypeHandler", (Class<Class<? extends TypeHandler>>) ((Class) Class.class));
			Class<? extends TypeHandler> defaultEnumTypeHandlerOrigin = (Class<? extends TypeHandler>) methodHandle.invokeExact(typeHandlerRegistryOrigin);
			if (defaultEnumTypeHandlerOrigin != null) {
				this.setDefaultEnumTypeHandler(defaultEnumTypeHandlerOrigin);
			}
		}
	}

	private TypeHandler<?> updateTypeHandlerConfiguration(TypeHandler<?> typeHandler) {
		if (typeHandler instanceof UnknownTypeHandler) {
			// 更新UnknownTypeHandler，否则其关联的Configuration还是旧的
			typeHandler = this.typeHandlerRegistry.getUnknownTypeHandler();
		} else if (typeHandler instanceof BaseTypeHandler) {
			// noinspection deprecation
			((BaseTypeHandler<?>) typeHandler).setConfiguration(this);
		}
		return typeHandler;
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


	public boolean isUseDebuggableLog() {
		return useDebuggableLog;
	}

	public void setUseDebuggableLog(boolean useDebuggableLog) {
		this.useDebuggableLog = useDebuggableLog;
		if (useDebuggableLog) {
			Class<? extends Log> logImplClass = this.logImpl;
			if (logImplClass != null && !DebuggableLog.class.isAssignableFrom(logImplClass)) {
				this.logImpl = DebuggableLog.class;
				LogFactory.useCustomLogging(DebuggableLog.class);
				OriginalLogFactory.useOriginalLogging(logImplClass);
			}
		} else {
			Class<? extends Log> logImplClass = OriginalLogFactory.getLogImplClass();
			if (logImplClass != null) {
				super.setLogImpl(logImplClass);
			}
		}
	}

	@Override
	public void setLogImpl(Class<? extends Log> logImpl) {
		if (logImpl != null) {
			if (this.useDebuggableLog) {
				this.logImpl = DebuggableLog.class;
				LogFactory.useCustomLogging(DebuggableLog.class);
				OriginalLogFactory.useOriginalLogging(logImpl);
			} else {
				super.setLogImpl(logImpl);
			}
		}
	}

	@Override
	public void addInterceptor(Interceptor interceptor) {
		if (interceptor instanceof EnhancedInterceptor) {
			// 增强拦截器的功能已在生成 EnhancedProviderSqlSource 时处理，不需要再添加此拦截器
			return;
		}
		boolean contains = interceptorChain.getInterceptors().stream().anyMatch(p -> interceptor.getClass().isAssignableFrom(p.getClass()));
		if (contains) {
			return;
		}
		super.addInterceptor(interceptor);
	}

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
	public EnhancedMapperRegistry getMapperRegistry() {
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
