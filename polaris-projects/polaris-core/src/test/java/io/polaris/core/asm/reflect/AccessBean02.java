package io.polaris.core.asm.reflect;

/**
 * @author Qt
 * @since 1.8,  Apr 14, 2024
 */
public class AccessBean02 {

	public int intVal0;
	public byte byteVal0;
	public short shortVal0;
	public long longVal0;
	public float floatVal0;
	public double doubleVal0;
	public char charVal0;
	public boolean booleanVal0;
	public String stringVal0;

	public int[] intArrVal0;

	private Object objectVal0;

	public int getIntVal0() {
		return intVal0;
	}

	public void setIntVal0(int intVal0) {
		this.intVal0 = intVal0;
	}

	public Object getObjectVal0() throws Exception{
		return objectVal0;
	}

	public void setObjectVal0(Object objectVal0) throws Exception{
		this.objectVal0 = objectVal0;
	}
}
