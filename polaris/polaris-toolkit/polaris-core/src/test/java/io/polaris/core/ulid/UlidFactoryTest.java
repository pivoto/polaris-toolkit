package io.polaris.core.ulid;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UlidFactoryTest {
	protected static final int DEFAULT_LOOP_MAX = 10_000;

	protected static final String DUPLICATE_UUID_MSG = "A duplicate ULID was created.";

	protected static final int THREAD_TOTAL = availableProcessors();

	protected static final Random RANDOM = new Random();

	protected static final long TIME_MASK = 0x0000ffffffffffffL;

	private static int availableProcessors() {
		int processors = Runtime.getRuntime().availableProcessors();
		if (processors < 4) {
			processors = 4;
		}
		return processors;
	}

	protected void checkNullOrInvalid(Ulid[] list) {
		for (Ulid ulid : list) {
			assertNotNull(ulid, "ULID is null");
		}
	}

	protected void checkUniqueness(Ulid[] list) {

		HashSet<Ulid> set = new HashSet<>();

		for (Ulid ulid : list) {
			assertTrue(set.add(ulid), String.format("ULID is duplicated %s", ulid));
		}

		assertEquals(set.size(), list.length, "There are duplicated ULIDs");
	}

	protected void checkCreationTime(Ulid[] list, long startTime, long endTime) {

		assertTrue(startTime <= endTime, "Start time was after end time");

		for (Ulid ulid : list) {
			long creationTime = ulid.getTime();
			assertTrue(creationTime >= startTime, "Creation time was before start time " + creationTime + " " + startTime);
			assertTrue(creationTime <= endTime, "Creation time was after end time");
		}
	}

	protected static class TestThread extends Thread {

		public static Set<UUID> hashSet = new HashSet<>();
		private UlidFactory creator;
		private int loopLimit;

		public TestThread(UlidFactory creator, int loopLimit) {
			this.creator = creator;
			this.loopLimit = loopLimit;
		}

		public static void clearHashSet() {
			hashSet = new HashSet<>();
		}

		@Override
		public void run() {
			long timestamp = System.currentTimeMillis();
			for (int i = 0; i < loopLimit; i++) {
				synchronized (hashSet) {
					hashSet.add(creator.create(timestamp).toUuid());
				}
			}
		}
	}
}
