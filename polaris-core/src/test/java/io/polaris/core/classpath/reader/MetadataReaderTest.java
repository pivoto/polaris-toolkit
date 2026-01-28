package io.polaris.core.classpath.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.polaris.core.io.Consoles;
import io.polaris.core.io.IO;
import io.polaris.core.junit.Heavy;
import io.polaris.core.string.Strings;
import org.junit.jupiter.api.Test;

@Heavy
class MetadataReaderTest {

	@Test
	void test01() throws IOException {
		System.out.println(String[][].class.getName());
		MetadataReaderFactory factory = MetadataReaderFactory.buildFactory();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(DemoAnno.class.getName().replace(".", "/") + ".class");
		//	MetadataReader metadataReader = factory.getMetadataReader(in);
		MetadataReader metadataReader = factory.getMetadataReader(IO.toBytes(in));
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		Consoles.println("{}", classMetadata.dump());

		List<AnnotationMetadata> annotations = classMetadata.getAnnotations();
		for (AnnotationMetadata annotation : annotations) {
			Consoles.println(Strings.repeat('=', 80));
			Consoles.println("{}", annotation.dump());
		}

		List<MethodMetadata> methods = classMetadata.getMethods();
		for (MethodMetadata method : methods) {
			Consoles.println(Strings.repeat('=', 80));
			Consoles.println("{}", method.dump());
		}

	}
}
