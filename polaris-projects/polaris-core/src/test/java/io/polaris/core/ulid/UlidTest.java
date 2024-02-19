package io.polaris.core.ulid;

import java.time.Instant;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import io.polaris.core.TestConsole;
import io.polaris.core.time.Times;
import org.junit.jupiter.api.Test;

class UlidTest {
	private static final String HORIZONTAL_LINE = "----------------------------------------";
	private static final int MAX = 100000;

	@Test
	void test_UlidCreator_getUlid() {
		UlidCreator.getUlid();
		long time = Times.millsTime(MAX, UlidCreator::getUlid);
		TestConsole.println("Time: " + time + "ms");
	}

	@Test
	void test_UlidCreator_getMonotonicUlid() {
		UlidCreator.getMonotonicUlid();
		long time = Times.millsTime(MAX, UlidCreator::getMonotonicUlid);
		TestConsole.println("Time: " + time + "ms");
	}

	@Test
	void test_Ulid_fast() {
		Ulid.fast();
		long time = Times.millsTime(MAX, Ulid::fast);
		TestConsole.println("Time: " + time + "ms");
	}


	@Test
	void test02() {
		Ulid ulid = null;
		// Create a quick ULID:
		TestConsole.println(ulid = Ulid.fast());
		// Create a ULID from a canonical string (26 chars):
		TestConsole.println(ulid = Ulid.from("0123456789ABCDEFGHJKMNPQRS"));

		// Convert a ULID into a canonical string in lower case:
		TestConsole.println(ulid.toLowerCase());
		// Convert a ULID into a UUID:
		TestConsole.println(ulid.toUuid());
		// Convert a ULID into a RFC-4122 UUID v4:
		TestConsole.println(ulid.toRfc4122().toUuid());

		// Get the creation instant of a ULID:
		{
			Instant instant = ulid.getInstant();
			TestConsole.println(instant);
		}
		{
			Instant instant = Ulid.getInstant("0123456789ABCDEFGHJKMNPQRS");
			TestConsole.println(instant);
		}

		// A UlidFactory with java.util.Random:
		{
			// use a `java.util.Random` instance for fast generation
			UlidFactory factory = UlidFactory.newInstance(new Random());
			TestConsole.println(factory.create());
		}
		// A UlidFactory with SplittableRandom:
		{
			// use a random function that returns a long value
			SplittableRandom random = new SplittableRandom();
			UlidFactory factory = UlidFactory.newInstance(() -> random.nextLong());
			TestConsole.println(factory.create());
		}
		// A UlidFactory with ThreadLocalRandom:
		{
			// use a random supplier that returns an array of 10 bytes
			UlidFactory factory = UlidFactory.newInstance((length) -> {
				final byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			});
			TestConsole.println(factory.create());
		}
	}

	@Test
	void test03() {
		int max = 100;

		TestConsole.println(HORIZONTAL_LINE);
		TestConsole.println("### ULID");
		TestConsole.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			TestConsole.println(UlidCreator.getUlid());
		}

		TestConsole.println(HORIZONTAL_LINE);
		TestConsole.println("### Monotonic ULID");
		TestConsole.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			TestConsole.println(UlidCreator.getMonotonicUlid());
		}
		TestConsole.println(HORIZONTAL_LINE);
		TestConsole.println("### Fast ULID");
		TestConsole.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			TestConsole.println(Ulid.fast());
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
		TestConsole.println("size: " + set.size());
	}

	@Test
	void test05() throws InterruptedException {
		Set<Object> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					set.add(UlidCreator.getMonotonicUlid().toString());
				}
			});
			t.start();
		}

		Thread.sleep(5000L);
		TestConsole.println("size: " + set.size());
	}

	@Test
	void test06() throws InterruptedException {
		Set<Object> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

		for (int i = 0; i < 10; i++) {
			Thread t = new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					set.add(UlidCreator.getUlid().toString());
				}
			});
			t.start();
		}

		Thread.sleep(5000L);
		TestConsole.println("size: " + set.size());
	}
}
