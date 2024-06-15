package io.polaris.core.service;

import lombok.AccessLevel;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class Service<S> {
	@SuppressWarnings("rawtypes")
	static final Comparator<Service> defaultComparator = (a, b) -> {
		if (Objects.equals(a, b)) {
			return 0;
		}
		int rs = Integer.compare(a.order, b.order);
		if (rs == 0) {
			rs = a.serviceClass.getName().compareTo(b.serviceClass.getName());
		}
		return rs;
	};

	@Getter
	private final Class<? extends S> serviceClass;
	@Getter
	private final String serviceName;
	@Getter(AccessLevel.PACKAGE)
	private final int order;
	private final Map<String, String> properties;
	private final Function<S, S> wrapperBuilder;
	private volatile S singletonPure;
	private volatile S singleton;

	Service(final Class<? extends S> serviceClass, Map<String, String> properties, final String serviceName, final int order, Function<S, S> wrapperBuilder) {
		this.serviceClass = serviceClass;
		this.serviceName = serviceName;
		this.order = order;
		this.properties = properties;
		this.wrapperBuilder = wrapperBuilder;
	}

	public String getProperty(String name) {
		return properties == null ? null : properties.get(name);
	}

	private void build() {
		singletonPure = newPureInstance();
		singleton = wrapperBuilder == null ? singletonPure : wrapperBuilder.apply(singletonPure);
	}

	public S getPureSingleton() {
		if (singletonPure == null) {
			synchronized (this) {
				if (singletonPure == null) {
					build();
				}
			}
		}
		return singletonPure;
	}

	public S getSingleton() {
		if (singleton == null) {
			synchronized (this) {
				if (singleton == null) {
					build();
				}
			}
		}
		return singleton;
	}

	public S newInstance() {
		return wrapperBuilder == null ? newPureInstance() : wrapperBuilder.apply(newPureInstance());
	}

	public S newInstance(Class<?>[] parameterTypes, Object[] args) {
		return wrapperBuilder == null ? newPureInstance(parameterTypes, args) : wrapperBuilder.apply(newPureInstance(parameterTypes, args));
	}

	public S newPureInstance() {
		try {
			return serviceClass.newInstance();
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("创建实例失败", e);
		}
	}

	public S newPureInstance(Class<?>[] parameterTypes, Object[] args) {
		try {
			return serviceClass.getConstructor(parameterTypes).newInstance(args);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException("创建实例失败", e);
		}
	}

	@Override
	public String toString() {
		return "Service{" +
			"serviceClass=" + serviceClass.getName() +
			", serviceName=" + serviceName +
			", properties=" + properties +
			'}';
	}
}
