package io.polaris.core.string;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import io.polaris.core.random.Randoms;
import io.polaris.core.time.Dates;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Qt
 * @since Dec 08, 2024
 */
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class KmpMatchersBenchmark {

	private static final String[][] testData = new String[5][];
	private static final String[][] testData2 = new String[5][];

	static {
		for (int i = 0; i < testData.length; i++) {
			String pattern = Randoms.randomString(100);
			testData[i] = new String[]{
				pattern, Randoms.randomString(100 * (i + 1)) + pattern + Randoms.randomString(100 * (i + 1))
			};
		}
		for (int i = 0; i < testData2.length; i++) {
			String pattern = Strings.repeat(Randoms.randomString(10), 10);
			testData2[i] = new String[]{
				pattern, Randoms.randomString(100 * (i + 1)) + pattern + Randoms.randomString(100 * (i + 1))
			};
		}
	}

	public static void main(String[] args) throws RunnerException {
		String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
		String fileName = "/data/benchmark/KmpMatchersBenchmark." + time;
		Options opt = new OptionsBuilder().include(KmpMatchersBenchmark.class.getSimpleName())
			.forks(1)
			.threads(128)
			.warmupIterations(3)
			.syncIterations(false)
			.measurementIterations(3)
			.resultFormat(ResultFormatType.CSV)
			.result(fileName + ".csv")
			.output(fileName + ".log")
			.build();

		new Runner(opt).run();
	}

	@Benchmark
	public boolean[] KmpMatchers_contains() {
		boolean[] rs = new boolean[testData.length];
		for (int i = 0; i < testData.length; i++) {
			String[] datum = testData[i];
			rs[i] = KmpMatchers.contains(datum[0], datum[1]);
		}
		return rs;
	}


	@Benchmark
	public boolean[] String_contains() {
		boolean[] rs = new boolean[testData.length];
		for (int i = 0; i < testData.length; i++) {
			String[] datum = testData[i];
			rs[i] = datum[0].contains(datum[1]);
		}
		return rs;
	}

	@Benchmark
	public boolean[] KmpMatchers_contains2() {
		boolean[] rs = new boolean[testData2.length];
		for (int i = 0; i < testData2.length; i++) {
			String[] datum = testData2[i];
			rs[i] = KmpMatchers.contains(datum[0], datum[1]);
		}
		return rs;
	}


	@Benchmark
	public boolean[] String_contains2() {
		boolean[] rs = new boolean[testData2.length];
		for (int i = 0; i < testData2.length; i++) {
			String[] datum = testData2[i];
			rs[i] = datum[0].contains(datum[1]);
		}
		return rs;
	}

}
