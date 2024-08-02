package io.polaris.core.data.buffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class AtomicRangeIntegerTest {

	private static AtomicRangeInteger ATOMIC_V3 = new AtomicRangeInteger(0, 100);
	private static AtomicRangeIntegerV1 ATOMIC_V1 = new AtomicRangeIntegerV1(0, 100);
	private static AtomicRangeIntegerV2 ATOMIC_V2 = new AtomicRangeIntegerV2(0, 100);

	@Test
	public void testGetAndIncrement() {
		AtomicRangeInteger atomicI = new AtomicRangeInteger(0, 10);
		for (int i = 0; i < 10; i++) {
			Assertions.assertEquals(i, atomicI.getAndIncrement());
		}
		Assertions.assertEquals(0, atomicI.getAndIncrement());
		Assertions.assertEquals(1, atomicI.get());
		Assertions.assertEquals(1, atomicI.intValue());
		Assertions.assertEquals(1, atomicI.longValue());
		Assertions.assertEquals(1, (int) atomicI.floatValue());
		Assertions.assertEquals(1, (int) atomicI.doubleValue());
	}

	@Test
	@Benchmark
	public void testGetAndIncrementV1Performance() {
		ATOMIC_V1.getAndIncrement();
	}

	@Test
	@Benchmark
	public void testGetAndIncrementV2Performance() {
		ATOMIC_V2.getAndIncrement();
	}

//	@Test
	@Benchmark
	public void testGetAndIncrementV3Performance() {
		ATOMIC_V3.getAndIncrement();
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(AtomicRangeIntegerTest.class.getSimpleName())
			.forks(1)
			.warmupIterations(3)
			.threads(128)
			.syncIterations(false)
			.output("/log/jmh.log")
			.measurementIterations(5)
			.build();

		new Runner(opt).run();
	}

}
