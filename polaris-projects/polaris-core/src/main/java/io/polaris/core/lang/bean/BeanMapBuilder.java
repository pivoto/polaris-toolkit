package io.polaris.core.lang.bean;

import java.util.Map;

import io.polaris.core.lang.Types;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Qt
 * @since 1.8,  Aug 06, 2023
 */
@Accessors(chain = true, fluent = true)
@Setter
@Getter
public class BeanMapBuilder<T> {
	private T bean;
	private Class<?> beanType;
	private BeanMapOptions options = new BeanMapOptions();

	public BeanMapBuilder(T bean) {
		this.bean = bean;
	}

	public static <T> BeanMapBuilder<T> of(T bean) {
		return new BeanMapBuilder<T>(bean);
	}

	public BeanMap<T> build() {
		if (bean == null) {
			throw new IllegalArgumentException("bean is null");
		}
		Class<?> beanType = this.beanType;
		if (beanType == null) {
			beanType = bean.getClass();
		}
		if (Map.class.isAssignableFrom(beanType)) {
			return new BeanDelegateMap<>(bean, beanType, this.options);
		}
		if (beanType.isArray() || beanType.isPrimitive() || Types.isPrimitiveWrapper(beanType)) {
			throw new IllegalArgumentException("unsupported bean type");
		}
		BeanMapOptions options = this.options == null ? new BeanMapOptions() : this.options;
		BeanAccessMode mode = options.mode() == null ? BeanAccessMode.INDEXED : options.mode();
		switch (mode) {
			case LAMBDA:
				return new BeanLambdaMap<>(bean, beanType, options);
			case INDEXED:
			default:
				return new BeanIndexedMap<>(bean, beanType, options);
		}
	}

}
