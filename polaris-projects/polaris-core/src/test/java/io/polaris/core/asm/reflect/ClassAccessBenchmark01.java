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
public class ClassAccessBenchmark01 {

	public static void main(String[] args) throws RunnerException {
		Options options = new OptionsBuilder().include(ClassAccessBenchmark01.class.getSimpleName())
			.forks(1)
			//.syncIterations(true)
			.resultFormat(ResultFormatType.CSV)
			.result("/data/benchmark/ClassAccessBenchmark01.csv")
			.output("/data/benchmark/ClassAccessBenchmark01.log")
			.build();
		new Runner(options).run();
	}

	private final ClassLambdaAccess<AccessBean01> classLambdaAccess = ClassLambdaAccess.get(AccessBean01.class);
	private final ClassAccess<AccessBean01> classAccess = ClassAccess.get(AccessBean01.class);

	@Benchmark
	public Object originConstructor(ThreadState state) {
		AccessBean01 o = state.source;
		return new AccessBean01("test", 123);
	}

	@Benchmark
	public Object testClassLambdaAccessConstructor(ThreadState state) {
		AccessBean01 o = state.source;
		ClassLambdaAccess<AccessBean01> access = ClassLambdaAccess.get(AccessBean01.class);
		return access.newInstance("test", 123);
	}

	@Benchmark
	public Object testClassAccessConstructor(ThreadState state) {
		AccessBean01 o = state.source;
		return classAccess.newInstance("test", 123);
	}


	@Benchmark
	public Object originInvoker(ThreadState state) {
		AccessBean01 o = state.source;
		o.setStrVal0("strVal0");
		return o.getStrVal0();
	}

	@Benchmark
	public Object testClassLambdaAccessInvoker(ThreadState state) {
		AccessBean01 o = state.source;
		classLambdaAccess.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classLambdaAccess.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}

	@Benchmark
	public Object testClassAccessInvoker(ThreadState state) {
		AccessBean01 o = state.source;
		classAccess.invokeMethod(o, "set" + StringCases.capitalize(AccessBean01.Fields.strVal0), "strVal0");
		return classAccess.invokeMethod(o, "get" + StringCases.capitalize(AccessBean01.Fields.strVal0));
	}


	@Benchmark
	public Object originField(ThreadState state) {
		AccessBean01 o = state.source;
		o.publicStrVal0 = "strVal0";
		return o.publicStrVal0;
	}

	@Benchmark
	public Object testClassLambdaAccessField(ThreadState state) {
		AccessBean01 o = state.source;
		classLambdaAccess.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classLambdaAccess.getField(o, AccessBean01.Fields.publicStrVal0);
	}

	@Benchmark
	public Object testClassAccessField(ThreadState state) {
		AccessBean01 o = state.source;
		classAccess.setField(o, AccessBean01.Fields.publicStrVal0, "strVal0");
		return classAccess.getField(o, AccessBean01.Fields.publicStrVal0);
	}


	@State(Scope.Thread)
	public static class ThreadState {
		public AccessBean01 source = AccessBean01.newRandom();
	}
}
