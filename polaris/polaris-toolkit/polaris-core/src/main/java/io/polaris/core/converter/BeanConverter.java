package io.polaris.core.converter;

import io.polaris.core.io.Serializations;
import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.lang.Types;
import io.polaris.core.object.Beans;
import io.polaris.core.object.Copiers;
import io.polaris.core.object.copier.CopyOptions;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

/**
 * @author Qt
 * @since 1.8
 */
public class BeanConverter<T> extends AbstractConverter<T> {
	private final Type beanType;
	private final Class<T> beanClass;
	private final CopyOptions copyOptions;

	public BeanConverter(Class<T> beanClass) {
		this(beanClass, CopyOptions.create().ignoreError(true));
	}

	public BeanConverter(Type beanType) {
		this(beanType, CopyOptions.create().ignoreError(true));
	}

	public BeanConverter(Type beanType, CopyOptions copyOptions) {
		this.beanType = beanType;
		this.beanClass = (Class<T>) Types.getClass(beanType);
		this.copyOptions = copyOptions;
	}

	@Override
	public Class<T> getTargetType() {
		return this.beanClass;
	}

	@Override
	protected T convertInternal(Object value, Class<? extends T> targetType) {
		if (value instanceof Map || Beans.isBeanClass(value.getClass())) {
			T target = Reflects.newInstanceIfPossible(beanClass);
			return Copiers.copy(value, target, beanType, copyOptions);
		}
		if (value instanceof byte[]) {
			return (T) Serializations.deserialize((byte[]) value);
		}

		if (value instanceof CharSequence) {
			// 扩展json实现，
			Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
			if (optional.isPresent()) {
				String json = value.toString();
				return optional.get().deserialize(json, targetType);
			}
		}

		throw new UnsupportedOperationException();
	}
}
