package io.polaris.core.env;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Qt
 * @since 1.8,  Apr 23, 2024
 */
public class GroupEnv implements Env {

	private final String name;
	private Env runtime;
	private final CopyOnWriteArrayList<Env> envList = new CopyOnWriteArrayList<>();
	private final ThreadLocal<Deque<String>> resolvedKeys = new ThreadLocal<>();
	private final ThreadLocal<Boolean> retrieved = new ThreadLocal<>();

	public GroupEnv() {
		this(null, false);
	}

	public GroupEnv(String name) {
		this(name, false);
	}

	public GroupEnv(String name, boolean mutable) {
		this.name = name;
		if (mutable) {
			runtime = Env.wrap(new Properties());
		}
	}

	@Override
	public String name() {
		return name;
	}

	public void addEnvFirst(Env properties) {
		synchronized (envList) {
			envList.remove(properties);
			envList.add(0, properties);
		}
	}

	public void addEnvLast(Env properties) {
		synchronized (envList) {
			envList.remove(properties);
			envList.add(properties);
		}
	}

	public boolean removeEnv(String name) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean replaceEnv(String name, Env properties) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.set(i, properties);
					return true;
				}
			}
		}
		return false;
	}

	public boolean addEnvBefore(String name, Env properties) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.add(i, properties);
					return true;
				}
			}
		}
		return false;
	}

	public boolean addEnvAfter(String name, Env properties) {
		synchronized (envList) {
			int size = envList.size();
			for (int i = 0; i < size; i++) {
				if (Objects.equals(envList.get(i).name(), name)) {
					envList.add(i + 1, properties);
					return true;
				}
			}
		}
		return false;
	}

	public void clearEnv() {
		synchronized (envList) {
			envList.clear();
		}
	}


	@Override
	public void set(String key, String value) {
		if (runtime != null) {
			runtime.set(key, value);
		}
	}

	@Override
	public String get(String key) {
		if (isResolvingKey(key)) {
			return null;
		}
		try {
			pushResolveKey(key);
			String val = null;
			if (runtime != null) {
				val = runtime.get(key);
			}
			if (val == null) {
				for (Env properties : envList) {
					val = properties.get(key);
					if (val != null) {
						break;
					}
				}
			}
			return val;
		} finally {
			pollResolveKey(key);
		}
	}

	@Override
	public void remove(String key) {
		if (isResolvingKey(key)) {
			return;
		}
		try {
			pushResolveKey(key);
			if (runtime != null) {
				runtime.remove(key);
			}
			for (Env properties : envList) {
				properties.remove(key);
			}
		} finally {
			pollResolveKey(key);
		}
	}

	@Override
	public Set<String> keys() {
		if (Boolean.TRUE.equals(retrieved.get())) {
			return Collections.emptySet();
		}
		retrieved.set(true);
		try {
			Set<String> keys = new LinkedHashSet<>();
			if (runtime != null) {
				keys.addAll(runtime.keys());
			}
			for (Env properties : envList) {
				Set<String> keySet = properties.keys();
				keys.addAll(keySet);
			}
			return keys;
		} finally {
			retrieved.remove();
		}
	}


	private boolean isResolvingKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		return queue != null && queue.contains(key);
	}

	private void pushResolveKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		if (queue == null) {
			resolvedKeys.set(queue = new ArrayDeque<>());
		}
		queue.offerFirst(key);

	}

	private void pollResolveKey(String key) {
		Deque<String> queue = resolvedKeys.get();
		if (Objects.equals(queue.peekFirst(), key)) {
			queue.pollFirst();
		}
		if (queue.isEmpty()) {
			resolvedKeys.remove();
		}
	}
}
