package io.polaris.core.cache;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.polaris.core.map.Maps;
import io.polaris.core.time.Dates;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author Qt
 * @since Oct 14, 2025
 */
@Fork(1)
@Threads(8)
@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@Warmup(iterations = 1)
@Measurement(iterations = 5)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TestMemCache {
	public static void main(String[] args) throws RunnerException {
		for (int i = 1; i <= 3; i++) {
			String time = Dates.YYYYMMDDHHMMSSSSS.format(Instant.now());
			String fileName = "/data/benchmark/TestMemCache." + i + "." + time;
			Options options = new OptionsBuilder().include(TestMemCache.class.getSimpleName())
				//.syncIterations(true)
				.forks(1)
				.threads(8 * i)
				.resultFormat(ResultFormatType.CSV)
				.output(fileName + ".log")
				.build();
			new Runner(options).run();
		}
	}

	@State(Scope.Thread)
	public static class ThreadState {
		private final Map<String, String> map0 = Collections.synchronizedMap(Maps.newLimitCapacityMap(5000));
		private final Map<String, String> map1 = Collections.synchronizedMap(Maps.newLimitCapacityMap(Integer.MAX_VALUE));
		private final Map<String, String> map2 = new ConcurrentHashMap<>();

		{
			for (int i = 0; i < 10000; i++) {
				map0.put(String.valueOf(i), String.valueOf(i));
				map1.put(String.valueOf(i), String.valueOf(i));
				map2.put(String.valueOf(i), String.valueOf(i));
			}
		}
	}

	@Benchmark
	public Object test_synchronizedMapLimit_put(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map0;
		for (int i = 0; i < 10000; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}

	@Benchmark
	public Object test_synchronizedMapLimit_get(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map0;
		for (int i = 0; i < 10000; i++) {
			map.get(String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}

	@Benchmark
	public Object test_synchronizedMap_put(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map1;
		for (int i = 0; i < 10000; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}
	@Benchmark
	public Object test_synchronizedMap_get(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map1;
		for (int i = 0; i < 10000; i++) {
			map.get(String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}

	@Benchmark
	public Object test_ConcurrentHashMap_put(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map2;
		for (int i = 0; i < 10000; i++) {
			map.put(String.valueOf(i), String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}
	@Benchmark
	public Object test_ConcurrentHashMap_get(ThreadState state, Blackhole blackhole) {
		Map<String, String> map = state.map2;
		for (int i = 0; i < 10000; i++) {
			map.get(String.valueOf(i));
		}
		blackhole.consume(map);
		return map;
	}
}
