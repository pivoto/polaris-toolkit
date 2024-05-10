package io.polaris.core.asm.reflect;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Date;

import io.polaris.core.random.Randoms;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Apr 10, 2024
 */
@Data
@FieldNameConstants
public class AccessBean00 {
	private String strVal0;
	private int intVal0;
	private long longVal0;
	private double doubleVal0;
	private boolean booleanVal0;

	private int[] intArrVal0;
	private int[][] intArrArrVal0;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public String publicStrVal0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public int publicIntVal0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public long publicLongVal0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public double publicDoubleVal0;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public boolean publicBooleanVal0;

	@Setter
	@Getter
	public static String publicStaticStrVal0;
	@Setter
	@Getter
	public static int publicStaticIntVal0;
	@Setter
	@Getter
	public static long publicStaticLongVal0;
	@Setter
	@Getter
	public static double publicStaticDoubleVal0;
	@Setter
	@Getter
	public static boolean publicStaticBooleanVal0;


	public AccessBean00() {
	}

	public AccessBean00(String strVal0) {
		this.strVal0 = strVal0;
	}

	public AccessBean00(String strVal0, int intVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
	}

	public AccessBean00(String strVal0, int intVal0, long longVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
	}


	public static AccessBean00 newRandom() {
		AccessBean00 bean = new AccessBean00();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(AccessBean00.class);
			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod != null) {
					Class<?> type = pd.getPropertyType();
					Object val = null;
					if (type == String.class) {
						val = Randoms.randomString(16);
					} else if (type == int.class || type == Integer.class) {
						val = Randoms.randomInt(0xff);
					} else if (type == long.class || type == Long.class) {
						val = Randoms.randomLong(0xff, 0xffff);
					} else if (type == float.class || type == Float.class) {
						val = (float) Randoms.randomInt(0xff);
					} else if (type == double.class || type == Double.class) {
						val = (double) Randoms.randomInt(0xff);
					} else if (type == boolean.class || type == Boolean.class) {
						val = Randoms.randomBoolean();
					} else if (type == char.class || type == Character.class) {
						val = Randoms.randomChar();
					} else if (type == byte.class || type == Byte.class) {
						val = (byte) Randoms.randomInt(0xff);
					} else if (type == short.class || type == Short.class) {
						val = (short) Randoms.randomInt(0xff);
					} else if (type == Date.class) {
						val = new Date(Randoms.randomLong(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 365, System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 365));
					} else if (type == Object.class) {
						val = new Object();
						val = Randoms.randomBytes(16);
					} else if (type == int[].class) {
						val = new int[Randoms.randomInt(8)];
					} else if (type == long[].class) {
						val = new long[Randoms.randomInt(8)];
					} else if (type == float[].class) {
						val = new float[Randoms.randomInt(8)];
					} else if (type == double[].class) {
						val = new double[Randoms.randomInt(8)];
					} else if (type == boolean[].class) {
						val = new boolean[Randoms.randomInt(8)];
					} else if (type == char[].class) {
						val = new char[Randoms.randomInt(8)];
					} else if (type == byte[].class) {
						val = Randoms.randomBytes(16);
					} else if (type == short[].class) {
						val = new short[Randoms.randomInt(8)];
					} else if (type == AccessBean00[].class) {
						val = new AccessBean00[Randoms.randomInt(8)];
					} else if (type == Date[].class) {
						val = new Date[Randoms.randomInt(8)];
					} else if (type == Object[].class) {
						val = new Object[Randoms.randomInt(8)];
					} else if (type == AccessBean00.class) {
						val = new AccessBean00();
					}

					if (val != null) {
						writeMethod.invoke(bean, val);
					}
				}
			}
		} catch (Exception ignore) {
		}
		return bean;
	}


	public byte[] randomBytes() throws Exception {
		return Randoms.randomBytes(16);
	}

	public byte[] randomBytes(int len) throws Exception {
		return Randoms.randomBytes(len);
	}


	public Object getObjForError() throws Exception {
		return new Object();
	}

	public void setObjForError(Object obj) throws Exception {
	}

}
