package io.polaris.core.lang.bean;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.random.Randoms;
import io.polaris.core.tuple.Tuple;
import io.polaris.core.tuple.Tuples;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author Qt
 * @since  Aug 05, 2023
 */
@Data
@FieldNameConstants
public class Bean01 extends Bean01Parent{
	private static final ILogger log = ILoggers.of(Bean01.class);
	static String staticId;
	String id;
	String name;
	protected String nameProtected;
	private String namePrivate;
	String nickName;
	protected String nickNameProtected;
	private String nickNamePrivate;
	Bean01 raw;
	protected Bean01 rawProtected;
	private Bean01 rawPrivate;
	Map<String, BigDecimal> numMap;
	protected Map<String, BigDecimal> numMapProtected;
	private Map<String, BigDecimal> numMapPrivate;
	int intVal;
	protected int intValProtected;
	private int intValPrivate;
	long longVal;
	protected long longValProtected;
	private long longValPrivate;
	byte byteVal;
	protected byte byteValProtected;
	private byte byteValPrivate;
	short shortVal;
	protected short shortValProtected;
	private short shortValPrivate;
	double doubleVal;
	protected double doubleValProtected;
	private double doubleValPrivate;
	char charVal;
	protected char charValProtected;
	private char charValPrivate;
	boolean booleanVal;
	protected boolean booleanValProtected;
	private boolean booleanValPrivate;
	float floatVal;
	protected float floatValProtected;
	private float floatValPrivate;
	BigDecimal bigDecimalVal;
	protected BigDecimal bigDecimalValProtected;
	private BigDecimal bigDecimalValPrivate;
	BigInteger bigIntegerVal;
	protected BigInteger bigIntegerValProtected;
	private BigInteger bigIntegerValPrivate;
	String strVal;
	protected String strValProtected;
	private String strValPrivate;
	List<String> strList;
	protected List<String> strListProtected;
	private List<String> strListPrivate;
	Set<String> strSet;
	protected Set<String> strSetProtected;
	private Set<String> strSetPrivate;
	Map<String, Object> strMap;
	protected Map<String, Object> strMapProtected;
	private Map<String, Object> strMapPrivate;
	Queue<String> strQueue;
	protected Queue<String> strQueueProtected;
	private Queue<String> strQueuePrivate;
	Deque<String> strDeque;
	protected Deque<String> strDequeProtected;
	private Deque<String> strDequePrivate;

	public Bean01() {
	}

	public Bean01(boolean error) {
		log.info("constructor error: {}", error);
		if (error) {
			throw new SecurityException("test...");
		}
	}

	public Bean01(String p1, Object p2, Object p3) {
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void setIntProperty(int v) {
		log.warn("setIntProperty：{}", v);
	}

	public int getIntProperty() {
		int v = Randoms.randomInt();
		log.warn("getIntProperty：{}", v);
		return v;
	}

	public void setBooleanProperty(boolean v) {
		log.warn("setBooleanProperty：{}", v);
	}

	public boolean getBooleanProperty() {
		boolean v = Randoms.randomBoolean();
		log.warn("getBooleanProperty：{}", v);
		return v;
	}

	public void setExceptionProperty(Object v) throws Exception {
		throw new UnsupportedOperationException("setExceptionProperty");
	}

	public Object getExceptionProperty() throws Exception {
		return new UnsupportedOperationException("getExceptionProperty");
	}


	public void testVoid() {
		log.warn("testVoid");
	}

	public void testVoidWithException() throws Exception {
		throw new UnsupportedOperationException("testVoidWithException");
	}

	public void testVoidWithArgs(String str, int intVal) {
		log.warn("testVoidWithArgs: {}, {}", str, intVal);
	}

	public Tuple testReturnObjectWithArgs(String str, int intVal) {
		log.warn("testReturnObjectWithArgs: {}, {}", str, intVal);
		return Tuples.of(str, intVal);
	}

	public int testReturnPrimitiveWithArgs(String str, int intVal) {
		log.warn("testReturnPrimitiveWithArgs: {}, {}", str, intVal);
		return Integer.parseInt(str) + intVal;
	}

	protected void testProtectedVoid() {
		log.warn("testProtectedVoid");
	}

	protected void testProtectedVoidWithException() throws Exception {
		throw new UnsupportedOperationException("testProtectedVoidWithException");
	}

	protected void testProtectedVoidWithArgs(String str, int intVal) {
		log.warn("testProtectedVoidWithArgs: {}, {}", str, intVal);
	}

	protected Tuple testProtectedReturnObjectWithArgs(String str, int intVal) {
		log.warn("testProtectedReturnObjectWithArgs: {}, {}", str, intVal);
		return Tuples.of(str, intVal);
	}

	protected int testProtectedReturnPrimitiveWithArgs(String str, int intVal) {
		log.warn("testProtectedReturnPrimitiveWithArgs: {}, {}", str, intVal);
		return Integer.parseInt(str) + intVal;
	}


	void testDefaultVoid() {
		log.warn("testDefaultVoid");
	}

	void testDefaultVoidWithException() throws Exception {
		throw new UnsupportedOperationException("testDefaultVoidWithException");
	}

	void testDefaultVoidWithArgs(String str, int intVal) {
		log.warn("testDefaultVoidWithArgs: {}, {}", str, intVal);
	}

	Tuple testDefaultReturnObjectWithArgs(String str, int intVal) {
		log.warn("testDefaultReturnObjectWithArgs: {}, {}", str, intVal);
		return Tuples.of(str, intVal);
	}

	int testDefaultReturnPrimitiveWithArgs(String str, int intVal) {
		log.warn("testDefaultReturnPrimitiveWithArgs: {}, {}", str, intVal);
		return Integer.parseInt(str) + intVal;
	}


	public static void testStaticVoid() {
		log.warn("testStaticVoid");
	}

	public static void testStaticVoidWithException() throws Exception {
		throw new UnsupportedOperationException("testStaticVoidWithException");
	}

	public static void testStaticVoidWithArgs(String str, int intVal) {
		log.warn("testStaticVoidWithArgs: {}, {}", str, intVal);
	}

}
