package io.polaris.core.service;

import java.util.*;
import java.util.ServiceLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.ClassMetadata;
import io.polaris.core.service.support.DefaultSpiDirectoryLoadingStrategy;
import io.polaris.core.service.support.DefaultSpiMetadataReader;
import io.polaris.core.service.support.DefaultSpiPackageLoadingStrategy;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since Mar 13, 2026
 */
class SpiHelper {

	private static final SpiHelper instance = new SpiHelper();
	private final List<SpiMetadataReader> spiMetadataReaders;
	private final String[] loadingDirectories;
	private final String[] loadingFilePatterns;

	static SpiHelper instance() {
		return instance;
	}

	private SpiHelper() {
		this.spiMetadataReaders = loadMetadataReaders();
		this.loadingDirectories = getLoadingDirectories();
		this.loadingFilePatterns = getLoadingFilePatterns();
	}


	public String[] loadingDirectories() {
		return loadingDirectories;
	}

	public String[] loadingFilePatterns() {
		return loadingFilePatterns;
	}

	public List<SpiMetadataReader> spiMetadataReaders() {
		return spiMetadataReaders;
	}

	private String[] getLoadingDirectories() {
		List<SpiDirectoryLoadingStrategy> strategies = loadDirectoryStrategies();
		Set<String> directories = new LinkedHashSet<>();
		for (SpiDirectoryLoadingStrategy strategy : strategies) {
			String[] arr = strategy.directories();
			if (arr == null) {
				continue;
			}
			for (String dir : arr) {
				if (Strings.isNotBlank(dir)) {
					dir = dir.replace(".", "/").replaceAll("^/+|/+$", "");
					if (Strings.isNotBlank(dir)) {
						directories.add(dir + "/");
					}
				}
			}
		}
		return directories.toArray(new String[0]);
	}

	private List<SpiDirectoryLoadingStrategy> loadDirectoryStrategies() {
		try {
			List<SpiDirectoryLoadingStrategy> strategies = new ArrayList<>();
			ServiceLoader<SpiDirectoryLoadingStrategy> loader = ServiceLoader.load(SpiDirectoryLoadingStrategy.class);
			for (SpiDirectoryLoadingStrategy strategy : loader) {
				strategies.add(strategy);
			}
			if (strategies.isEmpty()) {
				return Collections.singletonList(new DefaultSpiDirectoryLoadingStrategy());
			}
			strategies.sort(Comparator.comparingInt(SpiDirectoryLoadingStrategy::priority));
			return Collections.unmodifiableList(strategies);
		} catch (Throwable e) {
			return Collections.singletonList(new DefaultSpiDirectoryLoadingStrategy());
		}
	}

	private String[] getLoadingFilePatterns() {
		List<SpiPackageLoadingStrategy> strategies = loadPackageStrategies();
		Set<String> patterns = new LinkedHashSet<>();
		for (SpiPackageLoadingStrategy strategy : strategies) {
			String[] arr = strategy.basePackages();
			if (arr == null) {
				continue;
			}
			for (String dir : arr) {
				if (Strings.isNotBlank(dir)) {
					dir = dir.replace(".", "/").replaceAll("^/+|/+$", "");
					if (Strings.isNotBlank(dir)) {
						patterns.add(dir + "/**/*.class");
					}
				}
			}
		}
		return patterns.toArray(new String[0]);
	}

	private List<SpiPackageLoadingStrategy> loadPackageStrategies() {
		try {
			List<SpiPackageLoadingStrategy> strategies = new ArrayList<>();
			ServiceLoader<SpiPackageLoadingStrategy> loader = ServiceLoader.load(SpiPackageLoadingStrategy.class);
			for (SpiPackageLoadingStrategy strategy : loader) {
				strategies.add(strategy);
			}
			if (strategies.isEmpty()) {
				return Collections.singletonList(new DefaultSpiPackageLoadingStrategy());
			}
			strategies.sort(Comparator.comparingInt(SpiPackageLoadingStrategy::priority));
			return Collections.unmodifiableList(strategies);
		} catch (Throwable e) {
			return Collections.singletonList(new DefaultSpiPackageLoadingStrategy());
		}
	}

	private List<SpiMetadataReader> loadMetadataReaders() {
		try {
			List<SpiMetadataReader> readers = new ArrayList<>();
			ServiceLoader<SpiMetadataReader> loader = ServiceLoader.load(SpiMetadataReader.class);
			for (SpiMetadataReader strategy : loader) {
				readers.add(strategy);
			}
			if (readers.isEmpty()) {
				return Collections.singletonList(new DefaultSpiMetadataReader());
			}
			readers.sort(Comparator.comparingInt(SpiMetadataReader::priority));
			return Collections.unmodifiableList(readers);
		} catch (Throwable e) {
			return Collections.singletonList(new DefaultSpiMetadataReader());
		}
	}


	@Nullable
	public String getName(Class<?> clazz) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			String name = reader.getName(clazz);
			if (name != null) {
				return name;
			}
		}
		return null;
	}

	@Nonnull
	public Map<String, String> getProperties(Class<?> clazz) {
		Map<String, String> rs = new HashMap<>();
		for (SpiMetadataReader reader : spiMetadataReaders) {
			Map<String, String> properties = reader.getProperties(clazz);
			if (properties != null) {
				rs.putAll(properties);
			}
		}
		return rs;
	}

	public boolean isOrdered(Class<?> clazz) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			if (reader.isOrdered(clazz)) {
				return true;
			}
		}
		return false;
	}


	public boolean isDefault(Class<?> clazz) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			if (reader.isDefault(clazz)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public Integer getOrder(Class<?> clazz) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			Integer order = reader.getOrder(clazz);
			if (order != null) {
				return order;
			}
		}
		return null;
	}

	@Nullable
	public Integer getDefaultOrder(Class<?> clazz) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			Integer order = reader.getDefaultOrder(clazz);
			if (order != null) {
				return order;
			}
		}
		return null;
	}

	public boolean isSpiClass(ClassMetadata target, Class<?> spiClass) {
		for (SpiMetadataReader reader : spiMetadataReaders) {
			if (reader.isSpiClass(target, spiClass)) {
				return true;
			}
		}
		return false;
	}
}
