package io.polaris.core.ulid;

import io.polaris.core.time.Times;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

class UlidTest {
	private static final String HORIZONTAL_LINE = "----------------------------------------";
	private static final int MAX = 100000;

	@Test
	void test_UlidCreator_getUlid() {
		long time = Times.millsTime(MAX, () -> UlidCreator.getUlid());
		System.out.println("Time: " + time + "ms");
	}

	@Test
	void test_UlidCreator_getMonotonicUlid() {
		long time = Times.millsTime(MAX, () -> UlidCreator.getMonotonicUlid());
		System.out.println("Time: " + time + "ms");
	}

	@Test
	void test_Ulid_fast() {
		long time = Times.millsTime(MAX, () -> Ulid.fast());
		System.out.println("Time: " + time + "ms");
	}


	@Test
	void test02() {
		Ulid ulid = null;
		// Create a quick ULID:
		System.out.println(ulid = Ulid.fast());
		// Create a ULID from a canonical string (26 chars):
		System.out.println(ulid = Ulid.from("0123456789ABCDEFGHJKMNPQRS"));

		// Convert a ULID into a canonical string in lower case:
		System.out.println(ulid.toLowerCase());
		// Convert a ULID into a UUID:
		System.out.println(ulid.toUuid());
		// Convert a ULID into a RFC-4122 UUID v4:
		System.out.println(ulid.toRfc4122().toUuid());

		// Get the creation instant of a ULID:
		{
			Instant instant = ulid.getInstant();
			System.out.println(instant);
		}
		{
			Instant instant = Ulid.getInstant("0123456789ABCDEFGHJKMNPQRS");
			System.out.println(instant);
		}

		// A UlidFactory with java.util.Random:
		{
			// use a `java.util.Random` instance for fast generation
			UlidFactory factory = UlidFactory.newInstance(new Random());
			System.out.println(factory.create());
		}
		// A UlidFactory with SplittableRandom:
		{
			// use a random function that returns a long value
			SplittableRandom random = new SplittableRandom();
			UlidFactory factory = UlidFactory.newInstance(() -> random.nextLong());
			System.out.println(factory.create());
		}
		// A UlidFactory with ThreadLocalRandom:
		{
			// use a random supplier that returns an array of 10 bytes
			UlidFactory factory = UlidFactory.newInstance((length) -> {
				final byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			});
			System.out.println(factory.create());
		}
	}

	@Test
	void test03() {
		int max = 100;

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### ULID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getUlid());
		}

		System.out.println(HORIZONTAL_LINE);
		System.out.println("### Monotonic ULID");
		System.out.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			System.out.println(UlidCreator.getMonotonicUlid());
		}
	}

	@Test
	void test04() throws InterruptedException {
		Set<Object> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					set.add(Ulid.fast().toString());
				}
			});
			t.start();
		}

		Thread.sleep(5000L);
		System.out.println("size: " + set.size());
	}
}
