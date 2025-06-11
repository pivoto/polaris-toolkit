package io.polaris.core.jdbc.executor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.polaris.core.asm.reflect.AccessClassLoader;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.jdbc.base.BeanMapping;
import io.polaris.core.jdbc.base.JdbcOptions;
import io.polaris.core.jdbc.base.ResultBeanCollectionExtractor;
import io.polaris.core.jdbc.base.ResultBeanExtractor;
import io.polaris.core.jdbc.base.ResultBeanMappingCollectionExtractor;
import io.polaris.core.jdbc.base.ResultBeanMappingExtractor;
import io.polaris.core.jdbc.base.ResultExtractor;
import io.polaris.core.jdbc.base.ResultMapCollectionExtractor;
import io.polaris.core.jdbc.base.ResultMapExtractor;
import io.polaris.core.jdbc.base.ResultRowMapper;
import io.polaris.core.jdbc.base.ResultRowMappers;
import io.polaris.core.jdbc.base.ResultSingleCollectionExtractor;
import io.polaris.core.jdbc.base.ResultSingleExtractor;
import io.polaris.core.jdbc.base.ResultVisitor;
import io.polaris.core.jdbc.annotation.Key;
import io.polaris.core.jdbc.annotation.Mapping;
import io.polaris.core.jdbc.annotation.Options;
import io.polaris.core.jdbc.annotation.SqlQuery;
import io.polaris.core.jdbc.sql.EntityStatements;
import io.polaris.core.jdbc.annotation.EntitySelect;
import io.polaris.core.jdbc.annotation.SqlSelect;
import io.polaris.core.jdbc.annotation.SqlSelectSet;
import io.polaris.core.jdbc.sql.node.SqlNode;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.CaseModeOption;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since  Feb 07, 2024
 */
public class JdbcExecutorMetadata<T> {
	private static final Map<Class<?>, JdbcExecutorMetadata<?>> metabaseCache = Maps.newSoftMap(new ConcurrentHashMap<>());
	private final Map<Method, MethodMetadata> methodMetadataMap;

	protected JdbcExecutorMetadata(Class<T> interfaceClass) {
		Map<Method, MethodMetadata> methodMetadataMap = new HashMap<>();
		Method[] methods = interfaceClass.getMethods();
		for (Method method : methods) {
			if (method.isDefault()) {
				continue;
			}
			if (Modifier.isStatic(method.getModifiers())
				|| Modifier.isFinal(method.getModifiers())
				|| Modifier.isPrivate(method.getModifiers())) {
				continue;
			}
			if (Reflects.isObjectDeclaredMethod(method)) {
				continue;
			}
			methodMetadataMap.put(method, parseMethodMetadata(method));
		}
		this.methodMetadataMap = Collections.unmodifiableMap(methodMetadataMap);
	}

	@SuppressWarnings({"unchecked", "ConstantValue", "StatementWithEmptyBody"})
	public static <T> JdbcExecutorMetadata<T> of(Class<T> interfaceClass) {
		JdbcExecutorMetadata<T> rs = null;
		// 防止因对象回收后导致SoftMap结果丢失，尝试多次获取
		while ((rs = (JdbcExecutorMetadata<T>) metabaseCache.computeIfAbsent(interfaceClass, k -> new JdbcExecutorMetadata<T>(interfaceClass))) == null) {
		}
		return rs;
	}

	public Map<Method, MethodMetadata> getMethodMetadataMap() {
		return methodMetadataMap;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private MethodMetadata parseMethodMetadata(Method method) {
		JavaType<?> returnType = JavaType.of(method.getGenericReturnType());
		MappingModel mappingModel = MappingModel.of(method.getAnnotation(Mapping.class));
		BeanMapping<?> beanMapping = mappingModel.getBeanMapping();

		Function<Object[], MethodArgs> argsBuilder = parseArgsBuilder(method, beanMapping);
		Tuple2<Boolean, Function<Map<String, Object>, SqlNode>> sqlBuilderTuple = parseSqlBuilder(method);
		Function<Map<String, Object>, SqlNode> sqlBuilder = sqlBuilderTuple.getSecond();
		if (!sqlBuilderTuple.getFirst()) {
			return new MethodMetadata(returnType, false, argsBuilder, sqlBuilder, null);
		}

		final ResultExtractor<?> extractor;
		CaseModeOption caseMode = mappingModel.getCaseMode();
		if (void.class.equals(returnType.getRawClass())) {
			extractor = null;
		} else if (Collection.class.isAssignableFrom(returnType.getRawClass())) {
			Type actualType = returnType.getActualType(Collection.class, 0);
			Class<Object> elementType = JavaType.of(actualType).getRawClass();
			Supplier<Collection> collBuilder = () -> (Collection) Reflects.newInstanceIfPossible(returnType.getRawClass());
			// mapping
			if (beanMapping != null &&
				elementType.isAssignableFrom(beanMapping.getMetaObject().getBeanType().getRawClass())) {
				extractor = new ResultBeanMappingCollectionExtractor<>(collBuilder, beanMapping);
			}
			// bean
			else if (Beans.isBeanClass(elementType)) {
				extractor = new ResultBeanCollectionExtractor<>(
					collBuilder, elementType, mappingModel.getCaseMode());
			}
			// map
			else if (Map.class.isAssignableFrom(elementType)) {
				extractor = new ResultMapCollectionExtractor(collBuilder, elementType);
			}
			// single
			else {
				extractor = new ResultSingleCollectionExtractor<>(collBuilder, elementType);
			}
		} else {
			Class<?> elementType = returnType.getRawClass();
			// mapping
			if (beanMapping != null &&
				elementType.isAssignableFrom(beanMapping.getMetaObject().getBeanType().getRawClass())) {
				extractor = new ResultBeanMappingExtractor<>(beanMapping);
			}
			// bean
			else if (Beans.isBeanClass(elementType)) {
				extractor = new ResultBeanExtractor<>(elementType);
			}
			// map
			else if (Map.class.isAssignableFrom(elementType)) {
				extractor = new ResultMapExtractor(elementType);
			}
			// single
			else {
				extractor = new ResultSingleExtractor<>(elementType);
			}
		}
		return new MethodMetadata(returnType, true, argsBuilder, sqlBuilder, extractor);
	}


	@SuppressWarnings("unchecked")
	private Function<Object[], MethodArgs> parseArgsBuilder(Method method, BeanMapping<?> beanMapping) {
		final Type[] parameterTypes = method.getGenericParameterTypes();
		final Parameter[] parameters = method.getParameters();
		final int parameterCount = parameterTypes.length;
		final int[] specIndex = new int[]{-1, -1, -1, -1};
		final String[] keys = new String[parameterCount];
		for (int i = 0; i < parameterCount; i++) {
			Parameter parameter = parameters[i];
			JavaType<?> parameterType = JavaType.of(parameterTypes[i]);
			if (Connection.class.isAssignableFrom(parameterType.getRawClass())) {
				specIndex[0] = i;
			} else if (ResultExtractor.class.isAssignableFrom(parameterType.getRawClass())) {
				specIndex[1] = i;
			} else if (ResultVisitor.class.isAssignableFrom(parameterType.getRawClass())) {
				specIndex[2] = i;
			} else {
				String key = getParameterName(parameter);
				if (key == null) {
					if (specIndex[3] >= 0) {
						throw new IllegalArgumentException("不能存在多个未指定参数名的参数");
					}
					specIndex[3] = i;
				} else {
					keys[i] = key;
				}
			}
		}

		final ResultRowMapper<?> resultRowMapper;
		if (specIndex[2] >= 0) {
			JavaType<?> parameterType = JavaType.of(parameterTypes[specIndex[2]]);
			JavaType<?> actualType = JavaType.of(parameterType.getActualType(ResultVisitor.class, 0));
			Class<?> rawClass = actualType.getRawClass();
			// mapping
			if (beanMapping != null &&
				rawClass.isAssignableFrom(beanMapping.getMetaObject().getBeanType().getRawClass())) {
				resultRowMapper = ResultRowMappers.ofMapping(beanMapping);
			}
			// object
			else if (Object.class.equals(rawClass)) {
				resultRowMapper = ResultRowMappers.ofMap();
			}
			// map
			else if (Map.class.isAssignableFrom(rawClass)) {
				resultRowMapper = ResultRowMappers.ofMap((Class) rawClass);
			}
			// bean
			else if (Beans.isBeanClass(rawClass)) {
				resultRowMapper = ResultRowMappers.ofBean(actualType);
			}
			// single
			else {
				resultRowMapper = ResultRowMappers.ofSingle(actualType);
			}
		} else {
			resultRowMapper = null;
		}

		final JdbcOptions options = JdbcOptions.of(method.getAnnotation(Options.class));
		return args -> {
			Connection conn = null;
			if (specIndex[0] >= 0) {
				conn = (Connection) args[specIndex[0]];
			}
			Map<String, Object> bindings = new HashMap<>();
			ResultExtractor<?> extractor = null;
			ResultVisitor<?> visitor = null;
			Object noKeyArg = null;
			if (specIndex[1] >= 0) {
				extractor = (ResultExtractor<?>) args[specIndex[1]];
			}
			if (specIndex[2] >= 0) {
				visitor = (ResultVisitor<?>) args[specIndex[2]];
			}
			if (specIndex[3] >= 0) {
				noKeyArg = args[specIndex[3]];
				if (noKeyArg instanceof Map) {
					((Map<String, Object>) noKeyArg).forEach(bindings::putIfAbsent);
				} else {
					Beans.newBeanMap(noKeyArg).forEach(bindings::putIfAbsent);
				}
			}
			for (int i = 0; i < parameterCount; i++) {
				if (PrimitiveArrays.contains(specIndex, i)) {
					continue;
				}
				bindings.put(keys[i], args[i]);
			}
			return new MethodArgs(options, conn, bindings, noKeyArg, extractor, visitor, resultRowMapper);
		};
	}

	private static Tuple2<Boolean, Function<Map<String, Object>, SqlNode>> parseSqlBuilder(Method method) {
		boolean isSelect = method.isAnnotationPresent(SqlQuery.class)
			|| method.isAnnotationPresent(EntitySelect.class)
			|| method.isAnnotationPresent(SqlSelect.class)
			|| method.isAnnotationPresent(SqlSelectSet.class);
		if (isSelect) {
			return Tuple2.of(true, EntityStatements.buildSqlSelectFunction(method));
		}
		return Tuple2.of(false, EntityStatements.buildSqlUpdateFunction(method));
	}


	private static String getParameterName(Parameter parameter) {
		Key key = parameter.getAnnotation(Key.class);
		if (key != null) {
			return Strings.trimToNull(key.value());
		}
		for (Annotation annotation : parameter.getAnnotations()) {
			// 兼容 mybatis-param
			if ("org.apache.ibatis.annotations.Param".equals(annotation.annotationType().getName())) {
				return Strings.trimToNull(AnnotationAttributes.of(annotation).getString("value"));
			}
		}
		return null;
	}
}
