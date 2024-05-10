package io.polaris.core.asm.reflect;

import java.util.concurrent.TimeUnit;

import io.polaris.core.string.StringCases;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Qt
 * @since  Apr 10, 2024
 */
@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ClassAccessVerBenchmark01 {

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(ClassAccessVerBenchmark01.class.getSimpleName())
			.forks(1)
			//.syncIterations(true)
			.resultFormat(ResultFormatType.CSV)
			.result("/data/benchmark/ClassAccessVerBenchmark01.csv")
			.output("/data/benchmark/ClassAccessVerBenchmark01.log")
			.build();
		new Runner(options).run();
	}


	private final ClassAccessV1<AccessBean01> classAccessV1 = ClassAccessV1.get(AccessBean01.class);
	private final ClassAccessV2<AccessBean01> classAccessV2 = ClassAccessV2.get(AccessBean01.class);
	private final ClassAccessV3<AccessBean01> classAccessV3 = ClassAccessV3.get(AccessBean01.class);
	private final ClassAccessV4<AccessBean01> classAccessV4 = ClassAccessV4.get(AccessBean01.class);

	@Benchmark
	public Object originConstructor(ThreadState state) {
		AccessBean01 o = state.source;
		return new AccessBean01("test", 123);
	}

	@Benchmark
	public Object testClassAccessV1Constructor(ThreadState state) {
		return classAccessV1.newInstance("test", 123);
	}

	@Benchmark
	public Object testClassAccessV2Constructor(ThreadState state) {
		return classAccessV2.newInstance("test", 123);
	}

	@Benchmark
	public Object testClassAccessV2ConstructorX(ThreadState state) {
		AccessBean01 o = state.source;
		return classAccessV2.getConstructor(String.class, int.class).apply(new Object[]{"test", 123});
	}

	@Benchmark
	public Object testClassAccessV3Constructor(ThreadState state) {
		return classAccessV3.newInstance("test", 123);
	}

	@Benchmark
	public Object testClassAccessV4Constructor(ThreadState state) {
		return classAccessV4.newInstance("test", 123);
	}


	@Benchmark
	public Object originInvoker(ThreadState state) {
		AccessBean01 o = state.source;
		o.setStrVal0("strVal0");
		return o.getStrVal0();
	}

	@Benchmark
	public Object testClassAccessV1Invoker(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV1.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classAccessV1.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}

	@Benchmark
	public Object testClassAccessV2Invoker(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV2.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classAccessV2.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}

	@Benchmark
	public Object testClassAccessV2InvokerX(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV2.getMethodInvoker("set" + StringCases.capitalize(AccessBean01.Fields.strVal0), String.class)
			.apply(o, new Object[]{"strVal0"});
		return classAccessV2.getMethodInvoker("get" + StringCases.capitalize(AccessBean01.Fields.strVal0))
			.apply(o, new Object[0]);
	}

	@Benchmark
	public Object testClassAccessV3Invoker(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV3.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classAccessV3.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}

	@Benchmark
	public Object testClassAccessV4Invoker(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV4.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classAccessV4.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}

	@Benchmark
	public Object originField(ThreadState state) {
		AccessBean01 o = state.source;
		o.publicStrVal0 = "strVal0";
		return o.publicStrVal0;
	}

	@Benchmark
	public Object testClassAccessV1Field(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV1.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classAccessV1.getField(o, AccessBean01.Fields.publicStrVal0);
	}

	@Benchmark
	public Object testClassAccessV2Field(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV2.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classAccessV2.getField(o, AccessBean01.Fields.publicStrVal0);
	}

	@Benchmark
	public Object testClassAccessV2FieldX(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV2.getFieldSetter(AccessBean01.Fields.publicStrVal0).accept(o, "strVal0");
		return classAccessV2.getFieldGetter(AccessBean01.Fields.publicStrVal0).apply(o);
	}

	@Benchmark
	public Object testClassAccessV3Field(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV3.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classAccessV3.getField(o, AccessBean01.Fields.publicStrVal0);
	}

	@Benchmark
	public Object testClassAccessV4Field(ThreadState state) {
		AccessBean01 o = state.source;
		classAccessV4.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classAccessV4.getField(o, AccessBean01.Fields.publicStrVal0);
	}

	@State(Scope.Thread)
	public static class ThreadState {
		public AccessBean01 source = AccessBean01.newRandom();
	}
}
