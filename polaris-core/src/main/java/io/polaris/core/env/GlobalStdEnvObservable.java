package io.polaris.core.env;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Qt
 * @since Dec 19, 2025
 */
public class GlobalStdEnvObservable {
	private static final Set<Runnable> listeners = new CopyOnWriteArraySet<>();

	public static void addListener(Runnable listener) {
		listeners.add(listener);
	}

	public static void removeListener(Runnable listener) {
		listeners.remove(listener);
	}

	public static void notifyListeners() {
		listeners.forEach(Runnable::run);
	}

	public static void clearListeners() {
		listeners.clear();
	}

}
