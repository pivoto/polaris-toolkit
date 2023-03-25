package io.polaris.toolkit.spring.jdbc;

import io.polaris.toolkit.spring.core.tuple.Tuple;
import io.polaris.toolkit.spring.jdbc.properties.TargetDataSourceProperties;
import lombok.Getter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

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
	private final Map<String, TargetDataSourceProperties> targetProperties;
	private final Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties;

	public DynamicDataSource(Map<String, DataSource> multiDataSources, String defaultKey, Map<String, TargetDataSourceProperties> multiProperties) {
		super();

		Map<String, Tuple<DataSource, DataSourceProperties>> dataSourceProperties = new HashMap<>();
		Map<Object, Object> targetDataSources = new HashMap<>();
		Set<String> keys = new HashSet<>();
		if(defaultKey != null){
			keys.add(defaultKey);
		}
		multiDataSources.forEach((k, v) -> {
			targetDataSources.put(k, v);
			keys.add(k);

			TargetDataSourceProperties targetProperties = multiProperties.get(k);
			DataSourceProperties dsProperties = targetProperties.getProperties();
			if (dsProperties == null) {
				dsProperties = targetProperties.asDataSourceProperties();
			}
			dataSourceProperties.put(k, Tuple.of(v, dsProperties));
		});
		DynamicDataSourceKeys.setDefaultKey(defaultKey);
		DynamicDataSourceKeys.setKeys(Collections.unmodifiableSet(keys));
		setTargetDataSources(targetDataSources);
		setDefaultTargetDataSource(defaultKey);
		this.defaultKey = defaultKey;
		this.targetDataSources = Collections.unmodifiableMap(targetDataSources);
		this.targetProperties = Collections.unmodifiableMap(multiProperties);
		this.dataSourceProperties = Collections.unmodifiableMap(dataSourceProperties);
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
