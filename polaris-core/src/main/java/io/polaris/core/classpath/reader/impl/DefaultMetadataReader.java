package io.polaris.core.classpath.reader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.classpath.reader.MetadataReader;
import org.objectweb.asm.ClassReader;


/**
 * @author Qt
 * @since Jan 26, 2026
 */
class DefaultMetadataReader implements MetadataReader {

	static final int PARSING_OPTIONS = ClassReader.SKIP_DEBUG
		| ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES;


	private final DefaultClassMetadata classMetadata;


	DefaultMetadataReader(String classInternalName, @Nullable ClassLoader classLoader, Map<String, DefaultClassMetadata> visitedClasses) throws IOException {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		classInternalName = classInternalName.replace(".", "/");
		if (visitedClasses == null) {
			visitedClasses = new HashMap<>();
		}
		DefaultClassMetadata classMetadata = visitedClasses.get(classInternalName);
		if (classMetadata == null) {
			try (InputStream in = classLoader.getResourceAsStream(classInternalName + ".class");) {
				if (in == null) {
					throw new IOException("Class not found: " + classInternalName);
				}
				this.classMetadata = new DefaultClassMetadata();
				visitedClasses.put(classInternalName, this.classMetadata);
				ClassReader classReader = new ClassReader(in);
				MetadataClassVisitor visitor = new MetadataClassVisitor(classLoader, this.classMetadata, visitedClasses);
				classReader.accept(visitor, PARSING_OPTIONS);
			}
		} else {
			this.classMetadata = classMetadata;
		}
	}

	private DefaultMetadataReader(InputStream in, @Nullable ClassLoader classLoader, Map<String, DefaultClassMetadata> visitedClasses) throws IOException {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		if (visitedClasses == null) {
			visitedClasses = new HashMap<>();
		}
		this.classMetadata = new DefaultClassMetadata();
		ClassReader classReader = new ClassReader(in);
		MetadataClassVisitor visitor = new MetadataClassVisitor(classLoader, this.classMetadata, visitedClasses);
		classReader.accept(visitor, PARSING_OPTIONS);
	}


	private DefaultMetadataReader(byte[] bytes, @Nullable ClassLoader classLoader, Map<String, DefaultClassMetadata> visitedClasses) throws IOException {
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		if (visitedClasses == null) {
			visitedClasses = new HashMap<>();
		}
		this.classMetadata = new DefaultClassMetadata();
		ClassReader classReader = new ClassReader(bytes);
		MetadataClassVisitor visitor = new MetadataClassVisitor(classLoader, this.classMetadata, visitedClasses);
		classReader.accept(visitor, PARSING_OPTIONS);
	}

	static DefaultMetadataReader of(String className, ClassLoader classLoader) throws IOException {
		Map<String, DefaultClassMetadata> visitedClasses = new HashMap<>();
		return new DefaultMetadataReader(className, classLoader, visitedClasses);
	}

	static DefaultMetadataReader of(InputStream in, ClassLoader classLoader) throws IOException {
		Map<String, DefaultClassMetadata> visitedClasses = new HashMap<>();
		return new DefaultMetadataReader(in, classLoader, visitedClasses);
	}

	static DefaultMetadataReader of(byte[] bytes, ClassLoader classLoader) throws IOException {
		Map<String, DefaultClassMetadata> visitedClasses = new HashMap<>();
		return new DefaultMetadataReader(bytes, classLoader, visitedClasses);
	}


	@Override
	public DefaultClassMetadata getClassMetadata() {
		return this.classMetadata;
	}

}
