package io.polaris.scan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import io.polaris.core.classpath.ClassPathResource;
import io.polaris.core.classpath.ClassPathResources;
import io.polaris.core.collection.comparator.Comparators;
import io.polaris.core.io.Consoles;
import io.polaris.core.io.IO;
import io.polaris.json.Jacksons;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * @author Qt
 * @since Jan 29, 2026
 */
public class ConfigurationPropertiesScanner {

	@Test
	void test01() {
		try {
			ClassPathResource[] resources = ClassPathResources.getResources("org/springframework/**/*.class");
			Consoles.log(resources.length);
			for (ClassPathResource resource : resources) {
				IO.toBytes(resource.getInputStream());
			}
		} catch (IOException e) {
			Consoles.printStackTrace(e);
		}
	}

	@Test
	void test02() throws IOException {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "org/springframework/**/*.class";
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			Consoles.log(resources.length);
			for (Resource resource : resources) {
				IO.toBytes(resource.getInputStream());
			}
		} catch (IOException e) {
			Consoles.printStackTrace(e);
		}
	}

	@Test
	void testSpring() {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			// 只扫描枚举
			TypeFilter typeFilter = new AnnotationTypeFilter(ConfigurationProperties.class);

			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "org/springframework/**/*.class";
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					try {
						if (typeFilter.match(reader, readerFactory)) {
							ClassMetadata classMetadata = reader.getClassMetadata();
							String className = classMetadata.getClassName();
							System.out.println(className);
						}
					} catch (Exception ignored) {
					}
				}
			}
		} catch (IOException ignored) {
		}
		try {
			Enumeration<URL> es = Thread.currentThread().getContextClassLoader().getResources("META-INF/spring-configuration-metadata.json");
			List<SpringConfigurationMetadata> metadataList = new ArrayList<>();
			while (es.hasMoreElements()) {
				URL url = es.nextElement();
				if (url.toString().startsWith("file:")) {
					try (InputStream in = url.openStream();) {
						String json = IO.toString(in);
						SpringConfigurationMetadata meta = Jacksons.toJavaObject(json, SpringConfigurationMetadata.class);
						metadataList.add(meta);
					}
				}
			}


			List<SpringConfigurationMetadata.Group> groups = new ArrayList<>();
			List<SpringConfigurationMetadata.Property> properties = new ArrayList<>();
			for (SpringConfigurationMetadata meta : metadataList) {
				groups.addAll(meta.getGroups());
				properties.addAll(meta.getProperties());
			}

			groups.sort(Comparator.comparing(SpringConfigurationMetadata.Group::getName, Comparators.natural()));
			properties.sort(Comparator.comparing(SpringConfigurationMetadata.Property::getName, Comparators.natural()));

			for (SpringConfigurationMetadata.Group group : groups) {
				System.out.println(group);
			}
			for (SpringConfigurationMetadata.Property property : properties) {
				System.out.println(property);
			}
		} catch (IOException e) {
			Consoles.printStackTrace(e);
		}
	}

	@Data
	public static class SpringConfigurationMetadata {
		private List<Group> groups = new ArrayList<>();
		private List<Property> properties = new ArrayList<>();

		@Data
		public static class Group {
			private String name;
			private String type;
			private String sourceType;
		}

		@Data
		public static class Property {
			private String name;
			private String type;
			private String description;
			private String sourceType;
		}
	}

}
