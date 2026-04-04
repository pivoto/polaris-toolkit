package io.polaris.core.asm.reflect.sample;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since Apr 04, 2026
 */
@ToString
@FieldNameConstants
public class PrimitiveArrayBeanSource {
	// @formatter:off

	@Getter@Setter
	private byte[] byteArray;
	@Getter@Setter
	private short[] shortArray;
	@Getter@Setter
	private int[] intArray;
	@Getter@Setter
	private long[] longArray;
	@Getter@Setter
	private float[] floatArray;
	@Getter@Setter
	private double[] doubleArray;
	@Getter@Setter
	private boolean[] booleanArray;
	@Getter@Setter
	private char[] charArray;
	@Getter@Setter
	private String[] stringArray;
	@Getter@Setter
	private Object[] objectArray;

	// @formatter:on
}
