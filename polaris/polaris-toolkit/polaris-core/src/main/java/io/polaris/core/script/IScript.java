package io.polaris.core.script;

import java.util.Map;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface IScript extends Function<Map<String, Object>, Object> {

	Object run(Map<String, Object> binding);

	default Object apply(Map<String, Object> binding) {
		return run(binding);
	}

}
