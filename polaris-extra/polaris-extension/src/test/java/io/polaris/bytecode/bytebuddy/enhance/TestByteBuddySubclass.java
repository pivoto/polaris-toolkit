package io.polaris.bytecode.bytebuddy.enhance;

import java.io.File;
import java.lang.reflect.Method;

import io.polaris.bytecode.bytebuddy.GeneratedInterface;
import io.polaris.bytecode.bytebuddy.TestByteBuddy;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author Qt
 * @since Apr 15, 2026
 */
public class TestByteBuddySubclass {
	static String path;

	@BeforeAll
	static void beforeAll() {
		ByteBuddyAgent.install();
		String dir = TestByteBuddy.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		dir = dir.replaceFirst("/target/test-classes/?$", "");
		File file = new File(dir + "/target/bytebuddy-classes/");
		path = file.getAbsolutePath();
	}

	@Test
	void test_subclass(TestInfo testInfo) throws Exception {
		Class<SampleClass> originClass = SampleClass.class;
		String className = originClass.getName() + "_" + testInfo.getTestClass().map(Class::getSimpleName).orElse("") + "_" + testInfo.getTestMethod().map(Method::getName).orElse("test");

		ByteBuddy byteBuddy = new ByteBuddy();
		DynamicType.Builder<?> builder = byteBuddy.subclass(originClass);
		builder = builder.name(className);
		builder = builder.implement(GeneratedInterface.class);
		builder = builder.method(ElementMatchers.named("testMethod"))
			.intercept(MethodDelegation.withDefaultConfiguration()
				.withBinders(Morph.Binder.install(OverrideCallable.class))
				.to(new SampleClassInterceptor()))
		;
		DynamicType.Unloaded<?> unloaded = builder.make();
		unloaded.saveIn(new File(path));
		DynamicType.Loaded<?> load = unloaded.load(originClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION);
		Class<?> clazz = load.getLoaded();
		SampleClass sample = (SampleClass) clazz.newInstance();
		String result = sample.testMethod("test", 1, "test");
		System.out.println(sample);
		System.out.println(result);
	}


}
