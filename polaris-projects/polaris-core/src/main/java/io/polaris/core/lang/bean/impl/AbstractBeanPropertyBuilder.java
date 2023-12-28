package io.polaris.core.lang.bean.impl;

import io.polaris.core.lang.bean.BeanMetadata;
import io.polaris.core.lang.bean.BeanMetadatas;
import io.polaris.core.lang.bean.BeanPropertyBuilder;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author Qt
 * @since 1.8,  Dec 28, 2023
 */
public abstract class AbstractBeanPropertyBuilder<T> implements BeanPropertyBuilder<T> {
	protected Deque<Seriation> seriations = new ArrayDeque<>();
	protected T dest;
	protected Object lastOrig;
	protected boolean ignoredNull = true;

	private Seriation newSeriation(String origProperty, String destProperty, Object value) {
		Seriation seriation = new Seriation();
		if (origProperty != null) {// 需要从orig对象取值
			seriation.orig = lastOrig;
			seriation.origProperty = origProperty;
		}
		seriation.destProperty = destProperty;
		seriation.propertyValue = value;
		seriation.ignoredNull = ignoredNull;
		return seriation;
	}

	public void to(T dest) {
		this.dest = dest;
	}

	@Override
	public BeanPropertyBuilder<T> from(Object orig) {
		lastOrig = orig;
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> ignoreNull(boolean ignored) {
		ignoredNull = ignored;
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> set(String destProperty, Object value) {
		Seriation seriation = newSeriation(null, destProperty, value);
		seriations.add(seriation);
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> mapAll() {
		if (lastOrig != null) {
			mapAll(lastOrig.getClass());
		}
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> mapAll(Class<?> clazz) {
		BeanMetadata metadata = BeanMetadatas.getMetadata(clazz);
		for (String name : metadata.getters().keySet()) {
			map(name, name);
		}
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> map(String origProperty, String destProperty) {
		if (lastOrig != null) {
			Seriation seriation = newSeriation(origProperty, destProperty, null);
			seriations.add(seriation);
		}
		return this;
	}

	@Override
	public BeanPropertyBuilder<T> exec() {
		Seriation seriation = null;
		while ((seriation = seriations.poll()) != null) {
			exec(seriation);
		}
		return this;
	}

	protected abstract void exec(Seriation seriation);

	@Override
	public T done() {
		exec();
		return dest;
	}

}
