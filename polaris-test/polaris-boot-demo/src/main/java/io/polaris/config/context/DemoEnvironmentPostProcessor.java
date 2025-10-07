package io.polaris.config.context;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.polaris.core.env.Env;
import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.env.StdEnv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * @author Qt
 * @since Apr 23, 2024
 */
@Slf4j
public class DemoEnvironmentPostProcessor implements EnvironmentPostProcessor {

	@SuppressWarnings("rawtypes")
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {

		GlobalStdEnv.addEnvFirst(new SpringEnv(environment));

		StdEnv shade = GlobalStdEnv.env().shade(env -> !"SpringEnv".equals(env.name()));
		environment.getPropertySources().addLast(new EnumerablePropertySource("StdEnv") {
			@Override
			public Object getProperty(String name) {
				return shade.get(name);
			}

			@Override
			public String[] getPropertyNames() {
				return shade.keys().toArray(new String[0]);
			}
		});


	}


	private static class SpringEnv implements Env {
		private final ConfigurableEnvironment environment;

		public SpringEnv(ConfigurableEnvironment environment) {
			this.environment = environment;
		}

		@Override
		public String name() {
			return "SpringEnv";
		}

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
	}
}
