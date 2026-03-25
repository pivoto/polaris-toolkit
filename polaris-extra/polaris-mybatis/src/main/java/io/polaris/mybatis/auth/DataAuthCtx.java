package io.polaris.mybatis.auth;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import io.polaris.core.function.Executable;
import io.polaris.core.function.ThrowableExecution;
import io.polaris.core.function.ThrowableSupplier;
import io.polaris.mybatis.auth.annotation.DataScope;
import io.polaris.mybatis.auth.consts.DataScopeRelation;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 数据权限控制上下文
 *
 * @author Qt
 * @since Jan 25, 2026
 */
public class DataAuthCtx {

	private final static ThreadLocal<DataScopeRelation> RELATION = new TransmittableThreadLocal<>();
	private final static ThreadLocal<Set<String>> INCLUDED_SCOPE = new TransmittableThreadLocal<>();
	private final static ThreadLocal<Set<String>> EXCLUDED_SCOPE = new TransmittableThreadLocal<>();

	public static DataAuthEnv capture() {
		return new DataAuthEnv(getRelation(), getIncludedScopes(), getExcludedScopes());
	}

	public static Runnable continued(Runnable runnable) {
		return continued(capture(), runnable);
	}

	public static <T> Supplier<T> continued(Supplier<T> supplier) {
		return continued(capture(), supplier);
	}

	public static Runnable continued(DataAuthEnv env, Runnable runnable) {
		return () -> doContinued(env, runnable);
	}

	public static <T> Supplier<T> continued(DataAuthEnv env, Supplier<T> supplier) {
		return () -> doContinued(env, supplier);
	}

	public static void doContinued(DataAuthEnv env, Runnable runnable) {
		if (env != null) {
			execute(runnable, env.relation, env.includedScopes, env.excludedScopes);
		} else {
			runnable.run();
		}
	}

	public static <T> T doContinued(DataAuthEnv env, Supplier<T> supplier) {
		if (env != null) {
			return execute(supplier, env.relation, env.includedScopes, env.excludedScopes);
		} else {
			return supplier.get();
		}
	}

	public static DataScopeRelation getComputedRelation() {
		DataScopeRelation relation = RELATION.get();
		if (relation != null && relation != DataScopeRelation.UNSET) {
			return relation;
		}

		DataAuthEnvProvider dataAuthEnvProvider = DataAuthKit.instance().getDataAuthEnvProvider();
		if (dataAuthEnvProvider != null) {
			DataAuthEnv dataAuthEnv = dataAuthEnvProvider.getDataAuthEnv();
			if (dataAuthEnv != null) {
				relation = dataAuthEnv.relation;
				if (relation != null && relation != DataScopeRelation.UNSET) {
					return relation;
				}
			}
		}

		DataAuthConfigurator configurator = DataAuthKit.instance().getConfigurator();
		if (configurator != null) {
			relation = configurator.getGlobalRelation();
			if (relation != null && relation != DataScopeRelation.UNSET) {
				return relation;
			}
		}
		return DataScopeRelation.OR;
	}

	public static Set<String> getComputedScopes() {
		Set<String> set = new LinkedHashSet<>();

		DataAuthConfigurator configurator = DataAuthKit.instance().getConfigurator();
		if (configurator != null) {
			Set<String> globalScopes = configurator.getGlobalScopes();
			if (globalScopes != null && !globalScopes.isEmpty()) {
				set.addAll(globalScopes);
			}
		}

		DataAuthEnvProvider dataAuthEnvProvider = DataAuthKit.instance().getDataAuthEnvProvider();
		if (dataAuthEnvProvider != null) {
			DataAuthEnv dataAuthEnv = dataAuthEnvProvider.getDataAuthEnv();
			if (dataAuthEnv != null) {
				Set<String> includedScopes = dataAuthEnv.includedScopes;
				if (includedScopes != null && !includedScopes.isEmpty()) {
					set.addAll(includedScopes);
				}
				Set<String> excludedScopes = dataAuthEnv.excludedScopes;
				if (excludedScopes != null && !excludedScopes.isEmpty()) {
					set.removeAll(excludedScopes);
				}
			}
		}

		Set<String> includedScopes = INCLUDED_SCOPE.get();
		if (includedScopes != null && !includedScopes.isEmpty()) {
			set.addAll(includedScopes);
		}
		Set<String> excludedScopes = EXCLUDED_SCOPE.get();
		if (excludedScopes != null && !excludedScopes.isEmpty()) {
			set.removeAll(excludedScopes);
		}

		return Collections.unmodifiableSet(set);
	}

	@Nonnull
	public static DataScopeRelation getRelation() {
		DataScopeRelation relation = RELATION.get();
		if (relation == null) {
			return DataScopeRelation.OR;
		}
		return relation;
	}

	@Nonnull
	public static Set<String> getIncludedScopes() {
		Set<String> set = INCLUDED_SCOPE.get();
		if (set == null || set.isEmpty()) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	@Nonnull
	public static Set<String> getExcludedScopes() {
		Set<String> set = EXCLUDED_SCOPE.get();
		if (set == null || set.isEmpty()) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	public static void addScope(@Nonnull DataScope dataScope) {
		setRelation(dataScope.relation());
		addIncludedScope(dataScope.include());
		addExcludedScope(dataScope.exclude());
	}

	public static void addScope(@Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) {
		setRelation(relation);
		addIncludedScope(includedScopes);
		addExcludedScope(excludedScopes);
	}

	public static void setRelation(@Nonnull DataScopeRelation relation) {
		RELATION.set(relation);
	}

	public static void addIncludedScope(@Nonnull String scope) {
		addIncludedScope(Collections.singletonList(scope));
	}

	public static void addExcludedScope(@Nonnull String scope) {
		addExcludedScope(Collections.singletonList(scope));
	}

	public static void addIncludedScope(@Nonnull String[] scopes) {
		addIncludedScope(Arrays.asList(scopes));
	}

	public static void addExcludedScope(@Nonnull String[] scopes) {
		addExcludedScope(Arrays.asList(scopes));
	}

	public static void addIncludedScope(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return;
		}
		Set<String> set = INCLUDED_SCOPE.get();
		if (set == null) {
			set = new LinkedHashSet<>();
			INCLUDED_SCOPE.set(set);
		}
		set.addAll(scopes);
	}

	public static void addExcludedScope(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return;
		}
		Set<String> set = EXCLUDED_SCOPE.get();
		if (set == null) {
			set = new LinkedHashSet<>();
			EXCLUDED_SCOPE.set(set);
		}
		set.addAll(scopes);
	}

	public static void removeIncludedScope(@Nonnull String scope) {
		Set<String> set = INCLUDED_SCOPE.get();
		if (set != null) {
			set.remove(scope);
			if (set.isEmpty()) {
				INCLUDED_SCOPE.remove();
			}
		}
	}

	public static void removeExcludedScope(@Nonnull String scope) {
		Set<String> set = EXCLUDED_SCOPE.get();
		if (set != null) {
			set.remove(scope);
			if (set.isEmpty()) {
				EXCLUDED_SCOPE.remove();
			}
		}
	}

	public static void removeIncludedScope(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return;
		}
		Set<String> set = INCLUDED_SCOPE.get();
		if (set != null) {
			set.removeAll(scopes);
			if (set.isEmpty()) {
				INCLUDED_SCOPE.remove();
			}
		}
	}

	public static void removeExcludedScope(Collection<String> scopes) {
		if (scopes == null || scopes.isEmpty()) {
			return;
		}
		Set<String> set = EXCLUDED_SCOPE.get();
		if (set != null) {
			set.removeAll(scopes);
			if (set.isEmpty()) {
				EXCLUDED_SCOPE.remove();
			}
		}
	}

	public static void clearRelation() {
		RELATION.remove();
	}

	public static void clearIncludedScopes() {
		INCLUDED_SCOPE.remove();
	}

	public static void clearExcludedScopes() {
		EXCLUDED_SCOPE.remove();
	}

	public static void clearAll() {
		clearRelation();
		clearIncludedScopes();
		clearExcludedScopes();
	}


	public static void execute(@Nonnull Runnable task, @Nonnull DataAuthEnv env) {
		execute(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static void execute(@Nonnull Runnable task, @Nonnull DataScope dataScope) {
		execute(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static void execute(@Nonnull Runnable task, @Nonnull String[] includedScopes) {
		execute(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static void execute(@Nonnull Runnable task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) {
		execute(task, relation, Arrays.asList(includedScopes));
	}

	public static void execute(@Nonnull Runnable task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) {
		execute(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void execute(@Nonnull Runnable task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) {
		execute(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void execute(@Nonnull Runnable task, Collection<String> includedScopes) {
		execute(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static void execute(@Nonnull Runnable task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) {
		execute(task, relation, includedScopes, Collections.emptySet());
	}

	public static void execute(@Nonnull Runnable task, Collection<String> includedScopes, Collection<String> excludedScopes) {
		execute(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static void execute(@Nonnull Runnable task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			task.run();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataAuthEnv env) {
		return execute(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataScope dataScope) {
		return execute(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull String[] includedScopes) {
		return execute(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) {
		return execute(task, relation, Arrays.asList(includedScopes));
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) {
		return execute(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) {
		return execute(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V execute(@Nonnull Supplier<V> task, Collection<String> includedScopes) {
		return execute(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) {
		return execute(task, relation, includedScopes, Collections.emptySet());
	}

	public static <V> V execute(@Nonnull Supplier<V> task, Collection<String> includedScopes, Collection<String> excludedScopes) {
		return execute(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static <V> V execute(@Nonnull Supplier<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			return task.get();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataAuthEnv env) throws Throwable {
		executeThrowably(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataScope dataScope) throws Throwable {
		executeThrowably(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull String[] includedScopes) throws Throwable {
		executeThrowably(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) throws Throwable {
		executeThrowably(task, relation, Arrays.asList(includedScopes));
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Throwable {
		executeThrowably(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Throwable {
		executeThrowably(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, Collection<String> includedScopes) throws Throwable {
		executeThrowably(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) throws Throwable {
		executeThrowably(task, relation, includedScopes, Collections.emptySet());
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, Collection<String> includedScopes, Collection<String> excludedScopes) throws Throwable {
		executeThrowably(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static void executeThrowably(@Nonnull ThrowableExecution task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) throws Throwable {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			task.execute();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataAuthEnv env) throws Throwable {
		return executeThrowably(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataScope dataScope) throws Throwable {
		return executeThrowably(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull String[] includedScopes) throws Throwable {
		return executeThrowably(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) throws Throwable {
		return executeThrowably(task, relation, Arrays.asList(includedScopes));
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Throwable {
		return executeThrowably(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Throwable {
		return executeThrowably(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, Collection<String> includedScopes) throws Throwable {
		return executeThrowably(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) throws Throwable {
		return executeThrowably(task, relation, includedScopes, Collections.emptySet());
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, Collection<String> includedScopes, Collection<String> excludedScopes) throws Throwable {
		return executeThrowably(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static <V> V executeThrowably(@Nonnull ThrowableSupplier<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) throws Throwable {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			return task.get();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}


	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataAuthEnv env) throws Exception {
		executeExceptionally(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataScope dataScope) throws Exception {
		executeExceptionally(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull String[] includedScopes) throws Exception {
		executeExceptionally(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) throws Exception {
		executeExceptionally(task, relation, Arrays.asList(includedScopes));
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Exception {
		executeExceptionally(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Exception {
		executeExceptionally(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static void executeExceptionally(@Nonnull Executable task, Collection<String> includedScopes) throws Exception {
		executeExceptionally(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) throws Exception {
		executeExceptionally(task, relation, includedScopes, Collections.emptySet());
	}

	public static void executeExceptionally(@Nonnull Executable task, Collection<String> includedScopes, Collection<String> excludedScopes) throws Exception {
		executeExceptionally(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static void executeExceptionally(@Nonnull Executable task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) throws Exception {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			task.execute();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataAuthEnv env) throws Exception {
		return executeExceptionally(task, env.relation, env.includedScopes, env.excludedScopes);
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataScope dataScope) throws Exception {
		return executeExceptionally(task, dataScope.relation(), dataScope.include(), dataScope.exclude());
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull String[] includedScopes) throws Exception {
		return executeExceptionally(task, DataScopeRelation.OR, Arrays.asList(includedScopes));
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes) throws Exception {
		return executeExceptionally(task, relation, Arrays.asList(includedScopes));
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Exception {
		return executeExceptionally(task, DataScopeRelation.OR, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataScopeRelation relation, @Nonnull String[] includedScopes, @Nonnull String[] excludedScopes) throws Exception {
		return executeExceptionally(task, relation, Arrays.asList(includedScopes), Arrays.asList(excludedScopes));
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, Collection<String> includedScopes) throws Exception {
		return executeExceptionally(task, DataScopeRelation.OR, includedScopes, Collections.emptySet());
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes) throws Exception {
		return executeExceptionally(task, relation, includedScopes, Collections.emptySet());
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, Collection<String> includedScopes, Collection<String> excludedScopes) throws Exception {
		return executeExceptionally(task, DataScopeRelation.OR, includedScopes, excludedScopes);
	}

	public static <V> V executeExceptionally(@Nonnull Callable<V> task, @Nonnull DataScopeRelation relation, Collection<String> includedScopes, Collection<String> excludedScopes) throws Exception {
		DataScopeRelation oldRelation = RELATION.get();
		Set<String> includedSet = INCLUDED_SCOPE.get();
		Set<String> excludedSet = EXCLUDED_SCOPE.get();

		Set<String> oldIncludeSet = includedSet != null && !includedSet.isEmpty() ? new LinkedHashSet<>(includedSet) : null;
		Set<String> oldExcludeSet = excludedSet != null && !excludedSet.isEmpty() ? new LinkedHashSet<>(excludedSet) : null;

		addScope(relation, includedScopes, excludedScopes);
		try {
			return task.call();
		} finally {
			clearAll();
			if (oldRelation != null) {
				setRelation(oldRelation);
			}
			if (oldIncludeSet != null) {
				addIncludedScope(oldIncludeSet);
			}
			if (oldExcludeSet != null) {
				addExcludedScope(oldExcludeSet);
			}
		}
	}
}
