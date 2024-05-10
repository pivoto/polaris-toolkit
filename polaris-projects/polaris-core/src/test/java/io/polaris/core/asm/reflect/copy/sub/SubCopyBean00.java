package io.polaris.core.asm.reflect.copy.sub;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Apr 14, 2024
 */
@ToString
@FieldNameConstants
public class SubCopyBean00 {
	// @formatter:off

	public int errorIntVal0 = 1;
	public String fieldNoMapping1;
	@Getter@Setter
	private String methodNoMapping1;

	public String methodToFieldStr0;
	public int methodToFieldInt0;

	@Getter@Setter
	private String fieldToMethodStrVal0;
	@Getter@Setter
	private int fieldToMethodIntVal0;

	public String FieldCapitalizeVal0;
	public String fieldUnderlineToCamelVal0;
	public String field_camel_to_underline_val0;
	public String fieldignorecaseval0;
	public String fieldIgnoreCaseUnderlineToCamelVal0;
	public String field_IGNORE_CASE_camel_to_underline_val0;

	@Getter@Setter
	private String strVal0;
	@Getter@Setter
	private int intVal0;
	@Getter@Setter
	private long longVal0;
	@Getter@Setter
	private float floatVal0;
	@Getter@Setter
	private double doubleVal0;
	@Getter@Setter
	private boolean booleanVal0;
	@Getter@Setter
	private char charVal0;
	@Getter@Setter
	private byte byteVal0;
	@Getter@Setter
	private short shortVal0;
	@Getter@Setter
	private Date dateVal0;
	@Getter@Setter
	private Object objVal0;

	public String publicStrVal0;
	public int publicIntVal0;
	public long publicLongVal0;
	public float publicFloatVal0;
	public double publicDoubleVal0;
	public boolean publicBooleanVal0;
	public char publicCharVal0;
	public byte publicByteVal0;
	public short publicShortVal0;
	public Date publicDateVal0;
	public Object publicObjVal0;

	// @formatter:on

	public int getErrorIntVal0() throws Exception {
		throw new UnsupportedOperationException();
	}

	public void setErrorIntVal0(int val) throws Exception {
		throw new UnsupportedOperationException();
	}
}
