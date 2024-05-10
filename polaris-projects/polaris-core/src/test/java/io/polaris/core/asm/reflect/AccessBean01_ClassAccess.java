package io.polaris.core.asm.reflect;

import java.util.Date;

/**
 * @author Qt
 * @since  Apr 10, 2024
 */
public class AccessBean01_ClassAccess extends ClassAccessV2<AccessBean01> {

//	protected Object newIndexInstance(int index, Object... args) {
//		switch (index) {
//			case 0: {
//				return new AccessBean01();
//			}
//			case 1: {
//				try {
//					return new AccessBean01((String) args[0]);
//				} catch (Throwable e) {
//					throw new InvocationException(e);
//				}
//			}
//			case 2: {
//				try {
//					return new AccessBean01((String) args[0], (Integer) args[1]);
//				} catch (Exception e) {
//					throw new InvocationException(e);
//				}
//			}
//			default:
//				throw new IllegalArgumentException("找不到指定的非私有构造方法");
//		}
//	}

//	protected String[] buildMethodNames() {
//		String[] arr = new String[65535];
//		arr[0] = "get0";
//		arr[1] = "get1";
//		arr[2] = "get2";
//		arr[3] = "get3";
//		arr[4] = "get4";
//		arr[5] = "get5";
//		arr[6] = "get6";
//		arr[7] = "get7";
//		arr[8] = "get8";
//		arr[9] = "get9";
//		arr[10] = "get10";
//		arr[11] = "get11";
//		arr[12] = "get12";
//		arr[13] = "get13";
//		arr[14] = "get14";
//		arr[15] = "get15";
//		arr[16] = "get16";
//		arr[17] = "get17";
//		arr[18] = "get18";
//		arr[19] = "get19";
//		arr[65534] = "get65535";
//		return arr;
//	}

//	protected Class[][][] buildMethodParamTypes(){
//		Class[][][] arr = new Class[65536][][];
//		arr[65535] = new Class[55555][];
//		arr[65535][55550] = new Class[44444];
//		arr[65535][55550][1] = String.class;
//		arr[65535][55550][10] = Date.class;
//		arr[65535][55550][44440] = AccessBean01.class;
//		return arr;
//	}

//	protected Object invokeIndexMethod(Object instance, int index, int overloadIndex, Object... args) {
//		switch (index) {
//			case 0: {
//				switch (overloadIndex) {
//					case 0: {
//						return ((AccessBean01) instance).getStrVal0();
//					}
//					case 1: {
//						((AccessBean01) instance).setStrVal0((String) args[0]);
//						return null;
//					}
//					default:
//						throw new IllegalArgumentException("找不到指定的非私有方法");
//				}
//			}
//			case 1: {
//				switch (overloadIndex) {
//					case 0: {
//						return ((AccessBean01) instance).getStrVal0();
//					}
//					case 1: {
//						try {
//							((AccessBean01) instance).setStrVal0((String) args[0]);
//							return null;
//						} catch (Exception e) {
//							throw new InvocationException(e);
//						}
//					}
//					default:
//						throw new IllegalArgumentException("找不到指定的非私有方法");
//				}
//			}
//			default:
//				throw new IllegalArgumentException("找不到指定的非私有方法");
//		}
//	}


	@Override
	protected String[] buildFieldNames() {
		String[] arr = new String[65535];
		arr[0] = "get0";
		arr[1] = "get1";
		arr[2] = "get2";
		arr[3] = "get3";
		arr[4] = "get4";
		arr[5] = "get5";
		arr[6] = "get6";
		arr[7] = "get7";
		arr[8] = "get8";
		arr[9] = "get9";
		arr[10] = "get10";
		arr[11] = "get11";
		arr[12] = "get12";
		arr[13] = "get13";
		arr[14] = "get14";
		arr[15] = "get15";
		arr[16] = "get16";
		arr[17] = "get17";
		arr[18] = "get18";
		arr[19] = "get19";
		arr[65534] = "get65535";
		return arr;
	}

	@Override
	protected Class[] buildFieldTypes() {
		Class[] arr = new Class[65535];
		arr[19] = String.class;
		arr[65534] = int.class;
		return arr;
	}

	protected Object getIndexField(Object instance, int index) {
		switch (index) {
			case 0: {
				return ((AccessBean01) instance).publicStrVal0;
			}
			case 1: {
				return ((AccessBean01) instance).publicDateVal0;
			}
			default:
				throw new IllegalArgumentException("找不到指定的非私有成员");
		}
	}

	protected void setIndexField(Object instance, int index, Object value) {
		switch (index) {
			case 0: {
				((AccessBean01) instance).publicStrVal0 = (String) value;
				break;
			}
			case 1: {
				((AccessBean01) instance).publicDateVal0 = (Date) value;
				break;
			}
			case 2: {
				((AccessBean01) instance).publicIntVal0 = (Integer) value;
				break;
			}
			default:
				throw new IllegalArgumentException("找不到指定的非私有成员");
		}
	}


}
