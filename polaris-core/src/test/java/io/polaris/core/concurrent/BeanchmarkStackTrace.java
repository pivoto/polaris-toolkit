package io.polaris.core.concurrent;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import io.polaris.core.time.Dates;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Qt
 * @since Mar 02, 2026
 */
@Fork(1)
@Threads(8)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class BeanchmarkStackTrace {
	public static void main(String[] args) throws RunnerException {
		for (int i = 1; i <= 3; i++) {
			String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
			String fileName = "/data/benchmark/BeanchmarkStackTrace." + i + "." + time;
			Options options = new OptionsBuilder().include(BeanchmarkStackTrace.class.getSimpleName())
				//.syncIterations(true)
				.forks(1)
				.threads(8 * i)
				.resultFormat(ResultFormatType.CSV)
				.result(fileName + ".csv")
				.output(fileName + ".log")
				.build();
			new Runner(options).run();
		}
	}

	@Benchmark
	public void Throwable_getStackTrace(Blackhole blackhole) {
		blackhole.consume(new Throwable().getStackTrace());
	}

	@Benchmark
	public void Thread_getStackTrace(Blackhole blackhole) {
		blackhole.consume(Thread.currentThread().getStackTrace());
	}

}
