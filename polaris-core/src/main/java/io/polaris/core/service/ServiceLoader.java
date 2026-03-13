package io.polaris.core.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.annotation.Alternative;

/**
 * @author Qt
 * @see SpiLoader
 */
@Alternative(SpiLoader.class)
@Deprecated
public class ServiceLoader<S> implements Iterable<Service<S>> {
	private final SpiLoader<S> raw;

	public ServiceLoader(final Class<S> type, final ClassLoader loader) {
		this.raw = SpiLoader.of(type, loader);
	}

	public ServiceLoader(final Class<S> type) {
		this.raw = SpiLoader.of(type);
	}

	ServiceLoader(SpiLoader<S> raw) {
		this.raw = raw;
	}

	public static <S> ServiceLoader<S> of(Class<S> type) {
		return new ServiceLoader<>(type);
	}

	public static <S> ServiceLoader<S> of(Class<S> type, ClassLoader loader) {
		return new ServiceLoader<>(type, loader);
	}

	public static ClassLoader defaultClassLoader() {
		return SpiLoader.defaultClassLoader();
	}

	public List<Service<S>> getProviders() {
		return this.raw.getProviders();
	}

	public Map<String, Service<S>> getNamings() {
		return this.raw.getNamings();
	}

	@Override
	public Iterator<Service<S>> iterator() {
		return this.raw.iterator();
	}


	@Nullable
	public Service<S> get() {
		return this.raw.get();
	}

	@Nullable
	public S getSingleton() {
		return this.raw.getSingleton();
	}

	@Nullable
	public S getPureSingleton() {
		return this.raw.getPureSingleton();
	}

	@Nullable
	public Service<S> get(String name) {
		return this.raw.get(name);
	}

	@Nullable
	public S getSingleton(String name) {
		return this.raw.getSingleton(name);
	}

	@Nullable
	public S getPureSingleton(String name) {
		return this.raw.getPureSingleton(name);
	}

	@Nonnull
	public List<Service<S>> getAll(String propertyName, String propertyValue) {
		return this.raw.getAll(propertyName, propertyValue);
	}

	@Nonnull
	public List<S> getAllSingleton(String propertyName, String propertyValue) {
		return this.raw.getAllSingleton(propertyName, propertyValue);
	}

	@Nonnull
	public List<S> getAllPureSingleton(String propertyName, String propertyValue) {
		return this.raw.getAllPureSingleton(propertyName, propertyValue);
	}

	@Nonnull
	public List<Service<S>> getAll(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.getAll(matcher::apply);
	}

	@Nonnull
	public List<S> getAllSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.getAllSingleton(matcher::apply);
	}

	@Nonnull
	public List<S> getAllPureSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.getAllPureSingleton(matcher::apply);
	}

	@Nullable
	public Service<S> get(String propertyName, String propertyValue) {
		return this.raw.get(propertyName, propertyValue);
	}

	@Nullable
	public S getSingleton(String propertyName, String propertyValue) {
		return this.raw.getSingleton(propertyName, propertyValue);
	}

	@Nullable
	public S getPureSingleton(String propertyName, String propertyValue) {
		return this.raw.getPureSingleton(propertyName, propertyValue);
	}

	@Nullable
	public Service<S> get(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.get(matcher::apply);
	}

	@Nullable
	public S getSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.getSingleton(matcher::apply);
	}

	@Nullable
	public S getPureSingleton(@Nonnull Function<Service<S>, Boolean> matcher) {
		return this.raw.getPureSingleton(matcher::apply);
	}

	public void reload() {
		this.raw.reload();
	}
}
