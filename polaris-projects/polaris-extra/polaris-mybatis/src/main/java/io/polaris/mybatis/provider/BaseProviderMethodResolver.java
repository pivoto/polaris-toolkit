package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.mybatis.annotation.MapperEntity;
import io.polaris.mybatis.mapper.EntityMapper;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderMethodResolver;

/**
 * @author Qt
 * @since 1.8,  Sep 11, 2023
 */
public abstract class BaseProviderMethodResolver implements ProviderMethodResolver {

	@Override
	public Method resolveMethod(ProviderContext context) {
		String methodName = context.getMapperMethod().getName();
		try {
			return getClass().getMethod(methodName, Map.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod("provideSql", Map.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod(methodName, Object.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		try {
			return getClass().getMethod("provideSql", Object.class, ProviderContext.class);
		} catch (NoSuchMethodException ignore) {
		}
		return ProviderMethodResolver.super.resolveMethod(context);
	}


	protected static Class<?> getEntityClass(ProviderContext context) {
		Method mapperMethod = context.getMapperMethod();
		MapperEntity declared = mapperMethod.getAnnotation(MapperEntity.class);
		if (declared != null) {
			return declared.entity();
		}
		Class<?> entityClass = null;
		Class<?> mapperType = context.getMapperType();
		if (EntityMapper.class.isAssignableFrom(mapperType)) {
			Type actualType = JavaType.of(mapperType).getActualType(EntityMapper.class, 0);
			entityClass = Types.getClass(actualType);
		}
		if (entityClass == null || entityClass == Object.class) {
			throw new IllegalStateException("未知实体类型！");
		}
		return entityClass;
	}

}
