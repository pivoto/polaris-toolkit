package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.constants.ToolkitConstants;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManagerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Qt
 * @version Aug 14, 2018
 * @since 1.8
 */
public class EntityManagerFactoryProxyBuilder {

	private EntityManagerFactory defaultEntityManagerFactory;
	private Map<Object, EntityManagerFactory> targetEntityManagerFactory = new ConcurrentHashMap<>();

	public EntityManagerFactoryProxyBuilder withDefault(EntityManagerFactory entityManagerFactory) {
		this.defaultEntityManagerFactory = entityManagerFactory;
		targetEntityManagerFactory.put(ToolkitConstants.DYNAMIC_DATASOURCE_DEFAULT_KEY, entityManagerFactory);
		return this;
	}

	public EntityManagerFactoryProxyBuilder withTarget(String id, EntityManagerFactory factory) {
		targetEntityManagerFactory.put(id, factory);
		return this;
	}

	public EntityManagerFactoryProxyBuilder withTargets(Map<Object, EntityManagerFactory> targets) {
		targetEntityManagerFactory.putAll(targets);
		return this;
	}

	public EntityManagerFactory build() {
		ClassLoader classLoader = defaultEntityManagerFactory.getClass().getClassLoader();
		Class<?>[] interfaces = defaultEntityManagerFactory.getClass().getInterfaces();
		return (EntityManagerFactory) Proxy.newProxyInstance(
				classLoader, interfaces,
				new ProxyInvocationHandler(defaultEntityManagerFactory, targetEntityManagerFactory));
	}

	@RequiredArgsConstructor
	private static class ProxyInvocationHandler implements InvocationHandler {
		private final EntityManagerFactory defaultTarget;
		private final Map<Object, EntityManagerFactory> targetTable;

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("equals")) {
				return (proxy == args[0]);
			}
			/*
			// 解决 TransactionSynchronizationManager.getResource(.)
			else if (method.getName().equals("hashCode")) {
				return System.identityHashCode(proxy);
			}
			*/
			else if (method.getName().equals("toString")) {
				StringBuilder sb = new StringBuilder("EntityManagerFactory-Proxy for ");
				if (this.defaultTarget != null) {
					sb.append(" defaultTarget: [").append(this.defaultTarget.toString()).append("]");
				}
				if (this.targetTable != null) {
					sb.append(" targetTable: [").append(this.targetTable).append("]");
				}
				return sb.toString();
			}
			EntityManagerFactory actualTarget = null;
			String key = DynamicDataSourceKeys.get();
			if (key != null) {
				actualTarget = this.targetTable.get(key);
			}
			if (actualTarget == null) {
				actualTarget = this.defaultTarget;
			}
			if (actualTarget == null) {
				throw new IllegalStateException("找不到 EntityManagerFactory 实例");
			}
			Object rs = method.invoke(actualTarget, args);
			return rs;
		}
	}

}
