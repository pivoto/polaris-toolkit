package io.polaris.core.random;

import java.awt.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import io.polaris.core.asm.reflect.BeanAccess;
import io.polaris.core.collection.Iterables;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.string.Strings;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"all"})
public class Randoms {
	public static final String BASE_NUMBER = "0123456789";
	public static final String BASE_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String BASE_CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	public static final String BASE_CHAR_NUMBER = BASE_CHAR + BASE_NUMBER;
	public static final String BASE_CHAR_NUMBER_LOWER = BASE_CHAR_LOWER + BASE_NUMBER;

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
		return randomString(BASE_CHAR_NUMBER_LOWER, length);
	}

	public static String randomChineseString(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(randomChinese());
		}
		return sb.toString();
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


	/**
	 * 生成指定数量的随机实体对象列表
	 *
	 * @param entityClass 实体类的Class对象，用于创建该类型的随机实例
	 * @param count       要生成的随机实体对象数量
	 * @return 包含指定数量随机实体对象的列表
	 */
	public static <E> List<E> randomEntityList(Class<E> entityClass, int count) {
		List<E> list = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			list.add(randomEntity(entityClass));
		}
		return list;
	}

	/**
	 * 生成随机实体对象
	 * <p>
	 * 根据给定的实体类创建一个实例，并为其所有可写属性随机填充值。
	 * 支持基本类型、包装类型、日期时间类型、BigDecimal、BigInteger等常用类型。
	 * 对于Map类型的实体类，返回空实例。
	 * </p>
	 *
	 * @param <E>         实体类型
	 * @param entityClass 实体类的Class对象
	 * @return 填充了随机数据的实体实例
	 */
	public static <E> E randomEntity(Class<E> entityClass) {
		E entity = Reflects.newInstanceIfPossible(entityClass);
		if (Map.class.isAssignableFrom(entityClass)) {
			return entity;
		}
		BeanAccess<E> beanAccess = BeanAccess.get(entityClass);
		Set<String> setters = beanAccess.setterPropertyNames();
		for (String setter : setters) {
			try {
				Type type = beanAccess.propertyType(setter);
				Class<?> clazz = Types.getClass(type);
				if (clazz == String.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomString(6));
				} else if (clazz == Long.class || clazz == long.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomInt(0, 1000000));
				} else if (clazz == Integer.class || clazz == int.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomInt(0, 1000000));
				} else if (clazz == Double.class || clazz == double.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomDouble(0, 1000000));
				} else if (clazz == Short.class || clazz == short.class) {
					beanAccess.setPropertyOrField(entity, setter, (short) Randoms.randomInt(0, 1000000));
				} else if (clazz == Byte.class || clazz == byte.class) {
					beanAccess.setPropertyOrField(entity, setter, (byte) Randoms.randomInt(0, 1000000));
				} else if (clazz == Float.class || clazz == float.class) {
					beanAccess.setPropertyOrField(entity, setter, (float) Randoms.randomDouble(0, 1000000));
				} else if (clazz == Boolean.class || clazz == boolean.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomBoolean());
				} else if (clazz == Character.class || clazz == char.class) {
					beanAccess.setPropertyOrField(entity, setter, Randoms.randomChar());
				} else if (clazz == BigDecimal.class) {
					beanAccess.setPropertyOrField(entity, setter, new BigDecimal(Randoms.randomNumberString(6)));
				} else if (clazz == BigInteger.class) {
					beanAccess.setPropertyOrField(entity, setter, new BigInteger(Randoms.randomNumberString(6)));
				} else if (clazz == Date.class) {
					beanAccess.setPropertyOrField(entity, setter, new Date());
				} else if (clazz == Timestamp.class) {
					beanAccess.setPropertyOrField(entity, setter, new Timestamp(System.currentTimeMillis()));
				} else if (clazz == java.sql.Date.class) {
					beanAccess.setPropertyOrField(entity, setter, new java.sql.Date(System.currentTimeMillis()));
				} else if (clazz == java.sql.Time.class) {
					beanAccess.setPropertyOrField(entity, setter, new java.sql.Time(System.currentTimeMillis()));
				} else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
					beanAccess.setPropertyOrField(entity, setter, Converters.convertQuietly(type, Instant.now()));
				}
			} catch (Exception ignored) {
			}
		}

		return entity;
	}
}
