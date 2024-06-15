package io.polaris.core.lang.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.polaris.core.string.StringCases;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Apr 12, 2024
 */
@Data
@FieldNameConstants
public class MetaObjectTestBean {
	// region 集合

	private List<MetaObjectTestBean> privateList = new ArrayList<>();
	private Map<String, MetaObjectTestBean> privateMap = new HashMap<>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public List<MetaObjectTestBean> publicList = new ArrayList<>();
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public Map<String, MetaObjectTestBean> publicMap = new HashMap<>();

	// endregion 集合

	// region 单元素

	private java.lang.String privateStringVal;
	private int privateIntVal;
	private long privateLongVal;
	private float privateFloatVal;
	private double privateDoubleVal;
	private boolean privateBooleanVal;
	private byte privateByteVal;
	private char privateCharVal;
	private short privateShortVal;
	private java.math.BigDecimal privateBigDecimalVal;
	private java.util.Date privateDateVal;
	private java.sql.Timestamp privateTimestampVal;
	private io.polaris.core.lang.bean.MetaObjectTestBean privateMetaObjectTestBeanVal;
	private java.lang.Object privateObjectVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.lang.String publicStringVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public int publicIntVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public long publicLongVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public float publicFloatVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public double publicDoubleVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public boolean publicBooleanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public byte publicByteVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public char publicCharVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public short publicShortVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.math.BigDecimal publicBigDecimalVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.util.Date publicDateVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.sql.Timestamp publicTimestampVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public io.polaris.core.lang.bean.MetaObjectTestBean publicMetaObjectTestBeanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.lang.Object publicObjectVal;


	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected java.lang.String protectedStringVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected int protectedIntVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected long protectedLongVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected float protectedFloatVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected double protectedDoubleVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected boolean protectedBooleanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected byte protectedByteVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected char protectedCharVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected short protectedShortVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected java.math.BigDecimal protectedBigDecimalVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected java.util.Date protectedDateVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected java.sql.Timestamp protectedTimestampVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected io.polaris.core.lang.bean.MetaObjectTestBean protectedMetaObjectTestBeanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	protected java.lang.Object protectedObjectVal;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	java.lang.String defaultStringVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	int defaultIntVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	long defaultLongVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	float defaultFloatVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	double defaultDoubleVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	boolean defaultBooleanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	byte defaultByteVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	char defaultCharVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	short defaultShortVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	java.math.BigDecimal defaultBigDecimalVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	java.util.Date defaultDateVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	java.sql.Timestamp defaultTimestampVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	io.polaris.core.lang.bean.MetaObjectTestBean defaultMetaObjectTestBeanVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	java.lang.Object defaultObjectVal;

	// endregion 单元素

	// region 数组

	private java.lang.String[] privateStringArrVal;
	private int[] privateIntArrVal;
	private long[] privateLongArrVal;
	private float[] privateFloatArrVal;
	private double[] privateDoubleArrVal;
	private boolean[] privateBooleanArrVal;
	private byte[] privateByteArrVal;
	private char[] privateCharArrVal;
	private short[] privateShortArrVal;
	private java.math.BigDecimal[] privateBigDecimalArrVal;
	private java.util.Date[] privateDateArrVal;
	private java.sql.Timestamp[] privateTimestampArrVal;
	private io.polaris.core.lang.bean.MetaObjectTestBean[] privateMetaObjectTestBeanArrVal;
	private java.lang.Object[] privateObjectArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.lang.String[] publicStringArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public int[] publicIntArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public long[] publicLongArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public float[] publicFloatArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public double[] publicDoubleArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public boolean[] publicBooleanArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public byte[] publicByteArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public char[] publicCharArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public short[] publicShortArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.math.BigDecimal[] publicBigDecimalArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.util.Date[] publicDateArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.sql.Timestamp[] publicTimestampArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public io.polaris.core.lang.bean.MetaObjectTestBean[] publicMetaObjectTestBeanArrVal;
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	public java.lang.Object[] publicObjectArrVal;

	// endregion 数组


	public static void main(String[] args) {
		Class[] types = {
			String.class,
			int.class,
			long.class,
			float.class,
			double.class,
			boolean.class,
			byte.class,
			char.class,
			short.class,
			BigDecimal.class,
			Date.class,
			Timestamp.class,
			MetaObjectTestBean.class,
			Object.class,
		};

		for (Class type : types) {
			System.out.println("private " + type.getName() + " private" + StringCases.capitalize(type.getSimpleName()) + "Val;");
		}

		for (Class type : types) {
			System.out.println("@Getter(AccessLevel.NONE)");
			System.out.println("@Setter(AccessLevel.NONE)");
			System.out.println("public " + type.getName() + " public" + StringCases.capitalize(type.getSimpleName()) + "Val;");
		}

		for (Class type : types) {
			System.out.println("private " + type.getName() + "[] private" + StringCases.capitalize(type.getSimpleName()) + "ArrVal;");
		}

		for (Class type : types) {
			System.out.println("@Getter(AccessLevel.NONE)");
			System.out.println("@Setter(AccessLevel.NONE)");
			System.out.println("public " + type.getName() + "[] public" + StringCases.capitalize(type.getSimpleName()) + "ArrVal;");
		}
	}

}
