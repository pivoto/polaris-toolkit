package io.polaris.config.context;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.env.Env;
import io.polaris.core.env.GlobalStdEnv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * @author Qt
 * @since  Apr 23, 2024
 */
@Slf4j
public class DemoEnvironmentPostProcessor implements EnvironmentPostProcessor {

	@SuppressWarnings("rawtypes")
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		environment.getPropertySources().addLast(new EnumerablePropertySource("StdEnv") {
			@Override
			public Object getProperty(String name) {
				return GlobalStdEnv.get(name);
			}

			@Override
			public String[] getPropertyNames() {
				return GlobalStdEnv.keys().toArray(new String[0]);
			}
		});

		GlobalStdEnv.addEnvFirst(new Env() {
			@Override
			public String get(String key) {
				return environment.getProperty(key);
			}

			@Override
			public Set<String> keys() {
				Set<String> keys = new LinkedHashSet<>();
				for (PropertySource<?> propertySource : environment.getPropertySources()) {
					if (propertySource instanceof EnumerablePropertySource) {
						String[] propertyNames = ((EnumerablePropertySource<?>) propertySource).getPropertyNames();
						keys.addAll(Arrays.asList(propertyNames));
					}
				}
				return keys;
			}
		});

	}

}
