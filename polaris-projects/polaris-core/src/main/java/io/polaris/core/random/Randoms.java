package io.polaris.core.random;

import io.polaris.core.collection.Iterables;
import io.polaris.core.string.Strings;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"all"})
public class Randoms {
	public static final String BASE_NUMBER = "0123456789";
	public static final String BASE_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String BASE_CHAR_NUMBER = BASE_CHAR + BASE_NUMBER;

	public static ThreadLocalRandom getRandom() {
		return ThreadLocalRandom.current();
	}

	public static SecureRandom createSecureRandom(byte[] seed) {
		return (seed == null) ? new SecureRandom() : new SecureRandom(seed);
	}

	public static SecureRandom getSecureRandom() {
		return getSecureRandom(null);
	}

	public static SecureRandom getSecureRandom(byte[] seed) {
		return createSecureRandom(seed);
	}

	public static SecureRandom getSHA1PRNGRandom(byte[] seed) throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		if (null != seed) {
			random.setSeed(seed);
		}
		return random;
	}

	public static Random getRandom(boolean isSecure) {
		return isSecure ? getSecureRandom() : getRandom();
	}

	public static boolean randomBoolean() {
		return 0 == randomInt(2);
	}

	public static char randomChinese() {
		return (char) randomInt('\u4E00', '\u9FFF');
	}

	public static int randomInt(int min, int max) {
		return getRandom().nextInt(min, max);
	}

	public static int randomInt() {
		return getRandom().nextInt();
	}

	public static int randomInt(int limit) {
		return getRandom().nextInt(limit);
	}

	public static long randomLong(long min, long max) {
		return getRandom().nextLong(min, max);
	}

	public static long randomLong() {
		return getRandom().nextLong();
	}

	public static long randomLong(long limit) {
		return getRandom().nextLong(limit);
	}

	public static double randomDouble(double min, double max) {
		return getRandom().nextDouble(min, max);
	}

	public static double randomDouble() {
		return getRandom().nextDouble();
	}

	public static double randomDouble(double limit) {
		return getRandom().nextDouble(limit);
	}

	public static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		getRandom().nextBytes(bytes);
		return bytes;
	}

	public static <T> T randomElement(List<T> list) {
		return randomElement(list, list.size());
	}

	public static <T> T randomElement(List<T> list, int limit) {
		if (list.size() < limit || limit < 0) {
			limit = list.size();
		}
		return list.get(randomInt(limit));
	}


	public static <T> T randomElement(T[] array) {
		return randomElement(array, array.length);
	}

	public static <T> T randomElement(T[] array, int limit) {
		if (array.length < limit || limit < 0) {
			limit = array.length;
		}
		return array[randomInt(limit)];
	}


	public static <T> List<T> randomSubList(List<T> source, int count) {
		int size = source.size();
		if (count >= size) {
			return Iterables.asList(source);
		}
		List<T> result = new ArrayList<>();
		int[] indices = randomIndices(size);
		for (int i = 0; i < count; i++) {
			result.add(source.get(indices[i]));
		}
		return result;
	}

	public static <T> Set<T> randomSubSet(Set<T> set, int count) {
		int size = set.size();
		if (count >= size) {
			return Iterables.asCollection(LinkedHashSet::new, set);
		}
		final ArrayList<T> source = new ArrayList<>(set);
		final Set<T> result = new LinkedHashSet<>(count);
		int[] indices = randomIndices(size);
		for (int i = 0; i < count; i++) {
			result.add(source.get(indices[i]));
		}
		return result;
	}

	public static int[] randomIndices(int length) {
		final int[] range = new int[length];
		for (int i = 0; i < length; i++) {
			range[i] = i;
		}
		for (int i = 0; i < length; i++) {
			int random = randomInt(i, length);
			int v = range[i];
			range[i] = range[random];
			range[random] = v;
		}
		return range;
	}

	public static String randomString(String baseString, int length) {
		if (Strings.isEmpty(baseString)) {
			return "";
		}
		if (length < 1) {
			length = 1;
		}
		final StringBuilder sb = new StringBuilder(length);
		int baseLength = baseString.length();
		for (int i = 0; i < length; i++) {
			int number = randomInt(baseLength);
			sb.append(baseString.charAt(number));
		}
		return sb.toString();
	}

	public static String randomNumberString(int length) {
		return randomString(BASE_NUMBER, length);
	}

	public static String randomString(int length) {
		return randomString(BASE_CHAR_NUMBER, length);
	}

	public static String randomStringLowerCase(int length) {
		return randomString(BASE_CHAR_NUMBER, length).toLowerCase();
	}


	public static char randomNumberChar() {
		return randomChar(BASE_NUMBER);
	}

	public static char randomChar() {
		return randomChar(BASE_CHAR_NUMBER);
	}

	public static char randomChar(String baseString) {
		return baseString.charAt(randomInt(baseString.length()));
	}


	public static Color randomColor() {
		final Random random = getRandom();
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	public static Date randomDate(Date baseDate, int min, int max) {
		long time = baseDate.getTime();
		time = time + randomInt(min, max);
		return new Date(time);
	}
}
