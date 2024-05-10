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
public class AccessBean01 {
	private String strVal0;
	private int intVal0;
	private long longVal0;
	private float floatVal0;
	private double doubleVal0;
	private boolean booleanVal0;
	private char charVal0;
	private byte byteVal0;
	private short shortVal0;
	private AccessBean01 beanVal0;
	private Date dateVal0;
	private Object objVal0;

	private String[] strArrVal0;
	private int[] intArrVal0;
	private long[] longArrVal0;
	private float[] floatArrVal0;
	private double[] doubleArrVal0;
	private boolean[] booleanArrVal0;
	private char[] charArrVal0;
	private byte[] byteArrVal0;
	private short[] shortArrVal0;
	private AccessBean01[] beanArrVal0;
	private Date[] dateArrVal0;
	private Object[] objArrVal0;

	public String publicStrVal0;
	public int publicIntVal0;
	public long publicLongVal0;
	public float publicFloatVal0;
	public double publicDoubleVal0;
	public boolean publicBooleanVal0;
	public char publicCharVal0;
	public byte publicByteVal0;
	public short publicShortVal0;
	public AccessBean01 publicBeanVal0;
	public Date publicDateVal0;
	public Object publicObjVal0;

	public String[] publicStrArrVal0;
	public int[] publicIntArrVal0;
	public long[] publicLongArrVal0;
	public float[] publicFloatArrVal0;
	public double[] publicDoubleArrVal0;
	public boolean[] publicBooleanArrVal0;
	public char[] publicCharArrVal0;
	public byte[] publicByteArrVal0;
	public short[] publicShortArrVal0;
	public AccessBean01[] publicBeanArrVal0;
	public Date[] publicDateArrVal0;
	public Object[] publicObjArrVal0;

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
	public static float publicStaticFloatVal0;
	@Setter
	@Getter
	public static double publicStaticDoubleVal0;
	@Setter
	@Getter
	public static boolean publicStaticBooleanVal0;
	@Setter
	@Getter
	public static char publicStaticCharVal0;
	@Setter
	@Getter
	public static byte publicStaticByteVal0;
	@Setter
	@Getter
	public static short publicStaticShortVal0;
	@Setter
	@Getter
	public static AccessBean01 publicStaticBeanVal0;
	@Setter
	@Getter
	public static Date publicStaticDateVal0;
	@Setter
	@Getter
	public static Object publicStaticObjVal0;

	@Setter
	@Getter
	public static String[] publicStaticStrArrVal0;
	@Setter
	@Getter
	public static int[] publicStaticIntArrVal0;
	@Setter
	@Getter
	public static long[] publicStaticLongArrVal0;
	@Setter
	@Getter
	public static float[] publicStaticFloatArrVal0;
	@Setter
	@Getter
	public static double[] publicStaticDoubleArrVal0;
	@Setter
	@Getter
	public static boolean[] publicStaticBooleanArrVal0;
	@Setter
	@Getter
	public static char[] publicStaticCharArrVal0;
	@Setter
	@Getter
	public static byte[] publicStaticByteArrVal0;
	@Setter
	@Getter
	public static short[] publicStaticShortArrVal0;
	@Setter
	@Getter
	public static AccessBean01[] publicStaticBeanArrVal0;
	@Setter
	@Getter
	public static Date[] publicStaticDateArrVal0;
	@Setter
	@Getter
	public static Object[] publicStaticObjArrVal0;


	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public String publicStrVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public int publicIntVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public long publicLongVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public float publicFloatVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public double publicDoubleVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public boolean publicBooleanVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public char publicCharVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public byte publicByteVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public short publicShortVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public AccessBean01 publicBeanVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public Date publicDateVal1;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public Object publicObjVal1;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static String publicStaticStrVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static int publicStaticIntVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static long publicStaticLongVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static float publicStaticFloatVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static double publicStaticDoubleVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static boolean publicStaticBooleanVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static char publicStaticCharVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static byte publicStaticByteVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static short publicStaticShortVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static AccessBean01 publicStaticBeanVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static Date publicStaticDateVal1;
	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	public static Object publicStaticObjVal1;


	public AccessBean01() {
	}

	public AccessBean01(String strVal0) {
		this.strVal0 = strVal0;
	}

	public AccessBean01(String strVal0, int intVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0, double doubleVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
		this.doubleVal0 = doubleVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0, double doubleVal0, boolean booleanVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
		this.doubleVal0 = doubleVal0;
		this.booleanVal0 = booleanVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0, double doubleVal0, boolean booleanVal0, char charVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
		this.doubleVal0 = doubleVal0;
		this.booleanVal0 = booleanVal0;
		this.charVal0 = charVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0, double doubleVal0, boolean booleanVal0, char charVal0, byte byteVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
		this.doubleVal0 = doubleVal0;
		this.booleanVal0 = booleanVal0;
		this.charVal0 = charVal0;
		this.byteVal0 = byteVal0;
	}

	public AccessBean01(String strVal0, int intVal0, long longVal0, float floatVal0, double doubleVal0, boolean booleanVal0, char charVal0, byte byteVal0, short shortVal0) {
		this.strVal0 = strVal0;
		this.intVal0 = intVal0;
		this.longVal0 = longVal0;
		this.floatVal0 = floatVal0;
		this.doubleVal0 = doubleVal0;
		this.booleanVal0 = booleanVal0;
		this.charVal0 = charVal0;
		this.byteVal0 = byteVal0;
		this.shortVal0 = shortVal0;
	}

	public static AccessBean01 newRandom() {
		AccessBean01 bean = new AccessBean01();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(AccessBean01.class);
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
					} else if (type == AccessBean01[].class) {
						val = new AccessBean01[Randoms.randomInt(8)];
					} else if (type == Date[].class) {
						val = new Date[Randoms.randomInt(8)];
					} else if (type == Object[].class) {
						val = new Object[Randoms.randomInt(8)];
					} else if (type == AccessBean01.class) {
						val = new AccessBean01();
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

//	public void setBeanVal0(AccessBean01 beanVal0) throws Exception{
//		this.beanVal0 = beanVal0;
//	}

	public byte[] randomBytes() throws Exception {
		return Randoms.randomBytes(16);
	}

	public byte[] randomBytes(int len) throws Exception {
		return Randoms.randomBytes(len);
	}
}
