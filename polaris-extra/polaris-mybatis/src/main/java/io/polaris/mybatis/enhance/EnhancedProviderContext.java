package io.polaris.mybatis.enhance;

import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.jdbc.sql.BindingKeyModel;
import io.polaris.core.string.Strings;
import io.polaris.core.tuple.LazyRef;
import io.polaris.core.tuple.Ref;
import org.apache.ibatis.builder.annotation.ProviderContext;

/**
 * @author Qt
 * @since Apr 02, 2026
 */
public class EnhancedProviderContext {

	private final Class<?> mapperType;
	private final Method mapperMethod;
	private final String databaseId;
	private final Ref<BindingKeyDefinition> bindingKeyDefinition;
	private final Ref<Class<?>> entityClass;
	private final Ref<Boolean> withLogicDeleted;
	private final Ref<Boolean> existsByCount;


	private EnhancedProviderContext(boolean lazy, Class<?> mapperType, Method mapperMethod, String databaseId) {
		this.mapperType = mapperType;
		this.mapperMethod = mapperMethod;
		this.databaseId = Strings.trimToEmpty(databaseId);
		if (lazy) {
			this.bindingKeyDefinition = LazyRef.of(() -> EnhancedAutoMapperHelper.getBindingKeyDefinition(this.mapperMethod));
			this.entityClass = LazyRef.of(() -> EnhancedAutoMapperHelper.getEntityClassOrNull(this.mapperType, this.mapperMethod, this.databaseId));
			this.withLogicDeleted = LazyRef.of(() -> EnhancedAutoMapperHelper.withLogicDeleted(this.mapperType, this.mapperMethod));
			this.existsByCount = LazyRef.of(() -> EnhancedAutoMapperHelper.existsByCount(this.mapperType, this.mapperMethod));
		} else {
			this.bindingKeyDefinition = Ref.of(EnhancedAutoMapperHelper.getBindingKeyDefinition(this.mapperMethod));
			this.entityClass = Ref.of(EnhancedAutoMapperHelper.getEntityClassOrNull(this.mapperType, this.mapperMethod, this.databaseId));
			this.withLogicDeleted = Ref.of(EnhancedAutoMapperHelper.withLogicDeleted(this.mapperType, this.mapperMethod));
			this.existsByCount = Ref.of(EnhancedAutoMapperHelper.existsByCount(this.mapperType, this.mapperMethod));
		}
	}

	public static EnhancedProviderContext of(ProviderContext context) {
		return new EnhancedProviderContext(false, context.getMapperType(), context.getMapperMethod(), context.getDatabaseId());
	}

	public static EnhancedProviderContext ofLazy(ProviderContext context) {
		return new EnhancedProviderContext(true, context.getMapperType(), context.getMapperMethod(), context.getDatabaseId());
	}

	public static EnhancedProviderContext of(Class<?> mapperType, Method mapperMethod, String databaseId) {
		return new EnhancedProviderContext(false, mapperType, mapperMethod, databaseId);
	}

	public static EnhancedProviderContext ofLazy(Class<?> mapperType, Method mapperMethod, String databaseId) {
		return new EnhancedProviderContext(true, mapperType, mapperMethod, databaseId);
	}


	@Nonnull
	public Class<?> getMapperType() {
		return mapperType;
	}

	@Nonnull
	public Method getMapperMethod() {
		return mapperMethod;
	}

	@Nonnull
	public String getDatabaseId() {
		return databaseId;
	}

	@Nonnull
	public BindingKeyDefinition getBindingKeyDefinition() {
		return bindingKeyDefinition.get();
	}


	@Nullable
	public Class<?> getEntityClassOrNull() {
		return entityClass.get();
	}


	@Nonnull
	public Class<?> getEntityClassOrThrows() {
		Class<?> c = entityClass.get();
		if (c == null) {
			throw new IllegalStateException("未知实体类型！");
		}
		return c;
	}


	public boolean isWithLogicDeleted() {
		Boolean defaultWithLogicDeleted = withLogicDeleted.get();
		return Boolean.TRUE.equals(defaultWithLogicDeleted);
	}

	public boolean isWithLogicDeleted(Map<String, Object> bindings) {
		return isWithLogicDeleted(bindings, getBindingKeyDefinition().toBindingKeyModel(bindings));
	}

	public boolean isWithLogicDeleted(Map<String, Object> bindings, BindingKeyModel bindingKeyModel) {
		try {
			// fix for MapperMethod.ParamMap.get()
			String key = bindingKeyModel.withLogicDeleted();
			if (bindings.containsKey(key)) {
				Object sld = bindings.get(key);
				if (sld != null) {
					if (sld instanceof Boolean) {
						return (Boolean) sld;
					}
				}
			}
		} catch (Exception ignored) {
		}
		Boolean defaultWithLogicDeleted = withLogicDeleted.get();
		return Boolean.TRUE.equals(defaultWithLogicDeleted);
	}


	public boolean isExistsByCount() {
		Boolean defaultExistsByCount = existsByCount.get();
		return !Boolean.FALSE.equals(defaultExistsByCount);
	}

	public boolean isExistsByCount(Map<String, Object> bindings) {
		return isExistsByCount(bindings, getBindingKeyDefinition().toBindingKeyModel(bindings));
	}

	public boolean isExistsByCount(Map<String, Object> bindings, BindingKeyModel bindingKeyModel) {
		try {
			// fix for MapperMethod.ParamMap.get()
			String key = bindingKeyModel.existsByCount();
			if (bindings.containsKey(key)) {
				Object sld = bindings.get(key);
				if (sld != null) {
					if (sld instanceof Boolean) {
						return (Boolean) sld;
					}
				}
			}
		} catch (Exception ignored) {
		}
		Boolean defaultExistsByCount = existsByCount.get();
		return !Boolean.FALSE.equals(defaultExistsByCount);
	}
}
