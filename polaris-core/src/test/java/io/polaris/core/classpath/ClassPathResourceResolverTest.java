package io.polaris.core.classpath;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

import io.polaris.core.io.Consoles;
import io.polaris.core.io.IO;
import io.polaris.core.string.AntPathMatcher;
import io.polaris.core.string.PathMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassPathResourceResolverTest {

	private ClassPathResourceResolver resolver;

	@BeforeEach
	void setUp() {
		resolver = new ClassPathResourceResolver();
	}


	@Test
	void test_resource() throws IOException, URISyntaxException {
		{
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("io");
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				Consoles.println(resource.toString(), resource.getProtocol());
				Consoles.println(resource.getPath(), Paths.get(resource.toURI()).toFile());
			}
		}
		{
			Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources("org");
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				Consoles.println(resource.toString(), resource.getProtocol());
				Consoles.println(IO.toString(resource.openStream()));
			}
		}
	}

	@Test
	void test_getResourceNames() throws IOException {
		Consoles.println(resolver.getResourceNames("*.properties"));
		Consoles.println((resolver.getResources("*.properties")));
		Consoles.println(resolver.getResourceNames("**/*.properties"));
		Consoles.println((resolver.getResources("**/*.properties")));
	}

	@Test
	void test_getResourceNames2() throws IOException {
		Consoles.println(resolver.getResourceNames("io/**/*.properties"));
		Consoles.println((resolver.getResources("io/**/*.properties")));
	}

	@Test
	void test_getResourceNames3() throws IOException {
		Consoles.println(resolver.getResourceNames("com/**/*.properties"));
		Consoles.println((resolver.getResources("com/**/*.properties")));
	}
}
