package io.polaris.core.classpath.reader.impl;

import java.io.IOException;
import java.io.InputStream;

import io.polaris.core.classpath.reader.MetadataReader;
import io.polaris.core.classpath.reader.MetadataReaderFactory;

/**
 * @author Qt
 * @since Jan 28, 2026
 */
public class DefaultMetadataReaderFactory implements MetadataReaderFactory {

	@Override
	public MetadataReader getMetadataReader(String className, ClassLoader classLoader) throws IOException {
		return DefaultMetadataReader.of(className, classLoader);
	}

	@Override
	public MetadataReader getMetadataReader(InputStream classStream, ClassLoader classLoader) throws IOException {
		return DefaultMetadataReader.of(classStream, classLoader);
	}

	@Override
	public MetadataReader getMetadataReader(byte[] classBytes, ClassLoader classLoader) throws IOException {
		return DefaultMetadataReader.of(classBytes, classLoader);
	}

}
