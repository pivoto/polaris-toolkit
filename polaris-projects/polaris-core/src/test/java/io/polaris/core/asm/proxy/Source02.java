package io.polaris.core.asm.proxy;

/**
 * @author Qt
 * @since May 11, 2024
 */
abstract class Source02 implements java.io.Serializable {
	public static class CheckedException extends Exception {}

	public static class UndeclaredException extends Exception {}

	public String toString() {
		return "";
	}

	public void callAll() {
		protectedMethod();
		packageMethod();
		abstractMethod();
		synchronizedMethod();
		finalMethod();
		intType(1);
		longType(1L);
		floatType(1f);
		doubleType(1.0);
		objectType("1");
		voidType();
		multiArg(1, 1, 1, 1, "", "", "");
	}

	protected void protectedMethod() {
	}

	void packageMethod() {
	}

	abstract void abstractMethod();

	public void throwChecked() throws CheckedException {
		throw new CheckedException();
	}


	public void throwIndexOutOfBoundsException() {
		throw new IndexOutOfBoundsException();
	}

	public void throwAbstractMethodError() {
		throw new AbstractMethodError();
	}


	public synchronized void synchronizedMethod() {
	}

	public final void finalMethod() {
	}

	public int intType(int val) {
		return val;
	}

	public long longType(long val) {
		return val;
	}

	public double doubleType(double val) {
		return val;
	}

	public float floatType(float val) {
		return val;
	}

	public boolean booleanType(boolean val) {
		return val;
	}

	public short shortType(short val) {
		return val;
	}

	public char charType(char val) {
		return val;
	}

	public byte byteType(byte val) {
		return val;
	}

	public int[] arrayType(int val[]) {
		return val;
	}

	public String[] arrayType(String val[]) {
		return val;
	}


	public Object objectType(Object val) {
		return val;
	}

	public void voidType() {

	}

	public void multiArg(int arg1, long arg2,
		double arg3, float arg4, Object arg5, Object arg6, Object arg7) {
	}
}
