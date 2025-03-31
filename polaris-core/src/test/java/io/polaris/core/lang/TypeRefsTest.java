package io.polaris.core.lang;

import io.polaris.core.javapoet.TypeName;

import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All) // 吞吐量
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 结果所使用的时间单位
@State(Scope.Thread) // 每个测试线程分配一个实例
@Fork(2) // Fork进行的数目
@Warmup(iterations = 1) // 先预热1轮
@Measurement(iterations = 2) // 进行2轮测试
@Threads(128)
public class TypeRefsTest {
	static String[] names = new String[]{
		"java.util.Map<java.lang.String, java.util.List<java.lang.Integer>>",
		"java.util.Map<java.lang.String, java.util.List<java.util.Map<java.lang.String,java.lang.Integer>>>",
	};

	public static void main(String[] args) throws RunnerException {
		new Runner(new OptionsBuilder()
			.include(TypeRefsTest.class.getSimpleName())
			.build())
			.run();
	}

	@Setup(Level.Trial)
	public void setup() {
	}

	@TearDown(Level.Trial)
	public void tearDown() {

	}


	@Test
	public void test01() throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			TypeName typeName = TypeRefs.parse(name);
			TypeRef<?> ref = TypeRefs.createTypeRefByAsm(typeName, TypeRef.class.getPackage().getName(), TypeRef.class.getSimpleName() + "$$" + i);
			Type type = ref.getType();
			Consoles.println(type);
		}
	}

	@Test
	public void test02() throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			TypeName typeName = TypeRefs.parse(name);
			TypeRef<?> ref = TypeRefs.createTypeRefByJdk(typeName, TypeRef.class.getPackage().getName(), TypeRef.class.getSimpleName() + "$$" + i);
			Type type = ref.getType();
			Consoles.println(type);
		}
	}

	@Benchmark
	public void benchmark01() throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			TypeName typeName = TypeRefs.parse(name);
			TypeRef<?> ref = TypeRefs.createTypeRefByAsm(typeName, TypeRef.class.getPackage().getName(), TypeRef.class.getSimpleName() + "$$" + i);
			Type type = ref.getType();
		}
	}

	@Benchmark
	public void benchmark02() throws ClassNotFoundException {
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			TypeName typeName = TypeRefs.parse(name);
			TypeRef<?> ref = TypeRefs.createTypeRefByJdk(typeName, TypeRef.class.getPackage().getName(), TypeRef.class.getSimpleName() + "$$" + i);
			Type type = ref.getType();
		}
	}


}
