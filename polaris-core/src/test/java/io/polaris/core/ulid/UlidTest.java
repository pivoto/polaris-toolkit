package io.polaris.core.ulid;

import java.time.Instant;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import io.polaris.core.io.Consoles;
import io.polaris.core.time.Times;
import org.junit.jupiter.api.Test;

class UlidTest {
	private static final String HORIZONTAL_LINE = "----------------------------------------";
	private static final int MAX = 100000;

	@Test
	void test_UlidCreator_getUlid() {
		UlidCreator.getUlid();
		long time = Times.millsTime(MAX, UlidCreator::getUlid);
		Consoles.println("Time: " + time + "ms");
	}

	@Test
	void test_UlidCreator_getMonotonicUlid() {
		UlidCreator.getMonotonicUlid();
		long time = Times.millsTime(MAX, UlidCreator::getMonotonicUlid);
		Consoles.println("Time: " + time + "ms");
	}

	@Test
	void test_Ulid_fast() {
		Ulid.fast();
		long time = Times.millsTime(MAX, Ulid::fast);
		Consoles.println("Time: " + time + "ms");
	}


	@Test
	void test02() {
		Ulid ulid = null;
		// Create a quick ULID:
		Object[] args2 = new Object[]{ulid = Ulid.fast()};
		Consoles.println(args2);
		// Create a ULID from a canonical string (26 chars):
		Object[] args1 = new Object[]{ulid = Ulid.from("0123456789ABCDEFGHJKMNPQRS")};
		Consoles.println(args1);

		// Convert a ULID into a canonical string in lower case:
		Consoles.println(ulid.toLowerCase());
		// Convert a ULID into a UUID:
		Consoles.println(ulid.toUuid());
		// Convert a ULID into a RFC-4122 UUID v4:
		Consoles.println(ulid.toRfc4122().toUuid());

		// Get the creation instant of a ULID:
		{
			Instant instant = ulid.getInstant();
			Consoles.println(instant);
		}
		{
			Instant instant = Ulid.getInstant("0123456789ABCDEFGHJKMNPQRS");
			Consoles.println(instant);
		}

		// A UlidFactory with java.util.Random:
		{
			// use a `java.util.Random` instance for fast generation
			UlidFactory factory = UlidFactory.newInstance(new Random());
			Object[] args = new Object[]{factory.create()};
			Consoles.println(args);
		}
		// A UlidFactory with SplittableRandom:
		{
			// use a random function that returns a long value
			SplittableRandom random = new SplittableRandom();
			UlidFactory factory = UlidFactory.newInstance(() -> random.nextLong());
			Object[] args = new Object[]{factory.create()};
			Consoles.println(args);
		}
		// A UlidFactory with ThreadLocalRandom:
		{
			// use a random supplier that returns an array of 10 bytes
			UlidFactory factory = UlidFactory.newInstance((length) -> {
				final byte[] bytes = new byte[length];
				ThreadLocalRandom.current().nextBytes(bytes);
				return bytes;
			});
			Object[] args = new Object[]{factory.create()};
			Consoles.println(args);
		}
	}

	@Test
	void test03() {
		int max = 100;

		Consoles.println(HORIZONTAL_LINE);
		Consoles.println("### ULID");
		Consoles.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			Object[] args = new Object[]{UlidCreator.getUlid()};
			Consoles.println(args);
		}

		Consoles.println(HORIZONTAL_LINE);
		Consoles.println("### Monotonic ULID");
		Consoles.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			Object[] args = new Object[]{UlidCreator.getMonotonicUlid()};
			Consoles.println(args);
		}
		Consoles.println(HORIZONTAL_LINE);
		Consoles.println("### Fast ULID");
		Consoles.println(HORIZONTAL_LINE);

		for (int i = 0; i < max; i++) {
			Object[] args = new Object[]{Ulid.fast()};
			Consoles.println(args);
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
		Consoles.println("size: " + set.size());
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
		Consoles.println("size: " + set.size());
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
		Consoles.println("size: " + set.size());
	}
}
