package io.polaris.core.classpath.reader;

import java.io.IOException;
import java.io.InputStream;

import io.polaris.core.classpath.reader.impl.DefaultMetadataReaderFactory;
import io.polaris.core.spi.Spi;
import io.polaris.core.spi.SpiLoadable;
import io.polaris.core.spi.SpiLoaders;

/**
 * @author Qt
 * @since Jan 28, 2026
 */
@Spi
public interface MetadataReaderFactory extends SpiLoadable {


	static MetadataReaderFactory buildDefaultFactory() {
		return new DefaultMetadataReaderFactory();
	}

	static MetadataReaderFactory buildFactory() {
		return SpiLoaders.load(MetadataReaderFactory.class).getSingleton();
	}

	MetadataReader getMetadataReader(String className, ClassLoader classLoader) throws IOException;

	default MetadataReader getMetadataReader(String className) throws IOException {
		return getMetadataReader(className, null);
	}

	MetadataReader getMetadataReader(InputStream classStream, ClassLoader classLoader) throws IOException;

	default MetadataReader getMetadataReader(InputStream classStream) throws IOException {
		return getMetadataReader(classStream, null);
	}

	MetadataReader getMetadataReader(byte[] classBytes, ClassLoader classLoader) throws IOException;

	default MetadataReader getMetadataReader(byte[] classBytes) throws IOException {
		return getMetadataReader(classBytes, null);
	}
}
