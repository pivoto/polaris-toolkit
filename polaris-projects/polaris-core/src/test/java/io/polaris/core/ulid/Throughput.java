package io.polaris.core.ulid;

import org.openjdk.jmh.annotations.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Qt
 * @since 1.8
 */@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class Throughput {
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
}
