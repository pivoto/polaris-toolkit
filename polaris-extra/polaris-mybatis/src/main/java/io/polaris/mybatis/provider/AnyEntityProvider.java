package io.polaris.mybatis.provider;

import java.lang.reflect.Method;
import java.util.Map;

import io.polaris.core.annotation.Internal;
import io.polaris.core.annotation.Published;
import io.polaris.mybatis.annotation.AutoMapper;
import io.polaris.mybatis.enhance.EnhancedAutoMappers;
import io.polaris.mybatis.enhance.EnhancedMapperAnnotations;
import io.polaris.mybatis.enhance.EnhancedProviderContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.mapping.SqlCommandType;

/**
 * @author Qt
 * @since Jan 28, 2024
 */
@Slf4j
public class AnyEntityProvider extends BaseProviderMethodResolver {

	@Published
	public static String provideSql(Object parameterObject, ProviderContext context) {
		return provideSql(parameterObject, context, AnyEntityProvider::doProvideSql);
	}

	@Internal
	public static String doProvideSql(Map<String, Object> map, EnhancedProviderContext ctx) {
		Method mapperMethod = ctx.getMapperMethod();
		boolean isSelect = mapperMethod.isAnnotationPresent(SelectProvider.class);
		if (!isSelect) {
			AutoMapper annotation = EnhancedMapperAnnotations.getAutoMapper(ctx.getDatabaseId(), mapperMethod);
			if (annotation != null) {
				isSelect = annotation.type().sqlCommandType() == SqlCommandType.SELECT;
			}
		}
		if (isSelect) {
			return EnhancedAutoMappers.selectAny(map, ctx);
		} else {
			return EnhancedAutoMappers.updateAny(map, ctx);
		}
	}

}
