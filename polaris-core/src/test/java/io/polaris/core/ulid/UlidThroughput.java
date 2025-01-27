package io.polaris.core.ulid;

import java.io.File;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.polaris.core.time.Dates;
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
 * @since 1.8
 */
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class UlidThroughput {
	@Benchmark
	public UUID UUID_randomUUID() {
		return UUID.randomUUID();
	}

	@Benchmark
	public String UUID_randomUUID_toString() {
		return UUID.randomUUID().toString();
	}

	@Benchmark
	public Ulid Ulid_fast() {
		return Ulid.fast();
	}

	@Benchmark
	public String Ulid_fast_toString() {
		return Ulid.fast().toString();
	}

	@Benchmark
	public Ulid Ulid_fast2() {
		return Ulid.fast2();
	}

	@Benchmark
	public String Ulid_fast2_toString() {
		return Ulid.fast2().toString();
	}

	@Benchmark
	public Ulid UlidCreator_getUlid() {
		return UlidCreator.getUlid();
	}

	@Benchmark
	public String UlidCreator_getUlid_toString() {
		return UlidCreator.getUlid().toString();
	}

	@Benchmark
	public Ulid UlidCreator_getMonotonicUlid() {
		return UlidCreator.getMonotonicUlid();
	}

	@Benchmark
	public String UlidCreator_getMonotonicUlid_toString() {
		return UlidCreator.getMonotonicUlid().toString();
	}

	public static void main(String[] args) throws RunnerException {
		String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
		String fileName = "/data/benchmark/UlidThroughput." + time;
		String absolutePath = new File(fileName).getAbsolutePath();
		System.out.println(absolutePath);
		Options opt = new OptionsBuilder().include(UlidThroughput.class.getSimpleName())
			.forks(1)
			.threads(24)
			.warmupIterations(3)
			.syncIterations(false)
			.measurementIterations(3)
			.resultFormat(ResultFormatType.CSV)
			.result(fileName + ".csv")
			.output(fileName + ".log")
			.build();

		new Runner(opt).run();
	}
}
