package io.polaris.toolkit.spring.jdbc;

import lombok.Getter;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @version Dec 30, 2021
 * @since 1.8
 */
@Getter
public class DynamicDataSource extends AbstractRoutingDataSource {

	private final String defaultKey;
	private final Map<Object, Object> targetDataSources;

	public DynamicDataSource(Map<String, Object> multiDataSources, String defaultKey) {
		super();

		Map<Object, Object> targetDataSources = new HashMap<>();
		Set<String> keys = new HashSet<>();
		keys.add(defaultKey);
		multiDataSources.forEach((k, v) -> {
			targetDataSources.put(k, v);
			keys.add(k);
		});
		DynamicDataSourceKeys.setDefaultKey(defaultKey);
		DynamicDataSourceKeys.setKeys(Collections.unmodifiableSet(keys));
		setTargetDataSources(targetDataSources);
		setDefaultTargetDataSource(defaultKey);
		this.defaultKey = defaultKey;
		this.targetDataSources = targetDataSources;
	}

	@Override
	protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
		if (dataSource instanceof DataSource) {
			return (DataSource) dataSource;
		} else if (dataSource instanceof String) {
			return (DataSource) this.targetDataSources.get(dataSource);
		} else {
			throw new IllegalArgumentException(
					"Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
		}
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceKeys.get();
	}
}
