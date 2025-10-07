package io.polaris.core.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.AnnotationFormatError;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Qt
 * @since Jan 06, 2024
 */
public class AnnotationInvocationHandler implements InvocationHandler {
	public static final String TO_STRING = "toString";
	public static final String HASH_CODE = "hashCode";
	public static final String EQUALS = "equals";
	public static final String ANNOTATION_TYPE = "annotationType";
	private final Class<? extends Annotation> annotationType;
	private final Map<String, Object> memberValues;
	private final boolean allowedIncomplete;
	private transient volatile Method[] memberMethods = null;

	AnnotationInvocationHandler(Class<? extends Annotation> annotationType, Map<String, Object> values, boolean allowedIncomplete) {
		Class<?>[] interfaces = annotationType.getInterfaces();
		if (annotationType.isAnnotation() && interfaces.length == 1 && interfaces[0] == Annotation.class) {
			this.annotationType = annotationType;
			this.memberValues = values;
			this.allowedIncomplete = allowedIncomplete;
		} else {
			throw new AnnotationFormatError("Attempt to create proxy for a non-annotation type.");
		}
	}

	public static <A extends Annotation> A createProxy(Class<A> annotationType, Map<String, Object> values) {
		return createProxy(annotationType, values, false);
	}

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A createProxy(Class<A> annotationType, Map<String, Object> values, boolean allowedIncomplete) {
		ClassLoader classLoader = annotationType.getClassLoader();
		AnnotationInvocationHandler handler = new AnnotationInvocationHandler(annotationType, values, allowedIncomplete);
		return (A) Proxy.newProxyInstance(classLoader, new Class<?>[]{annotationType}, handler);
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		String methodName = method.getName();
		if (method.getDeclaringClass() != annotationType) {
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (methodName.equals(EQUALS) && parameterTypes.length == 1 && parameterTypes[0] == Object.class) {
				return this.equalsImpl(args[0]);
			}
			if (methodName.equals(HASH_CODE) && parameterTypes.length == 0) {
				return this.hashCodeImpl();
			}
			if (methodName.equals(TO_STRING) && parameterTypes.length == 0) {
				return this.toStringImpl();
			}
			if (methodName.equals(ANNOTATION_TYPE) && parameterTypes.length == 0) {
				return this.annotationType;
			}
			throw new IllegalStateException("Unexpected method: " + method);
		}
		Object value = this.memberValues.get(methodName);
		if (value == null) {
			if (allowedIncomplete) {
				return null;
			}
			throw new IncompleteAnnotationException(this.annotationType, methodName);
		}
		if (value.getClass().isArray() && Array.getLength(value) != 0) {
			value = this.cloneArray(value);
		}
		return value;
	}


	private Object cloneArray(Object arr) {
		Class<?> arrType = arr.getClass();
		if (arrType == boolean[].class) {
			return ((boolean[]) arr).clone();
		} else if (arrType == byte[].class) {
			return ((byte[]) arr).clone();
		} else if (arrType == char[].class) {
			return ((char[]) arr).clone();
		} else if (arrType == double[].class) {
			return ((double[]) arr).clone();
		} else if (arrType == float[].class) {
			return ((float[]) arr).clone();
		} else if (arrType == int[].class) {
			return ((int[]) arr).clone();
		} else if (arrType == long[].class) {
			return ((long[]) arr).clone();
		} else if (arrType == short[].class) {
			return ((short[]) arr).clone();
		} else {
			return ((Object[]) arr).clone();
		}
	}

	private String toStringImpl() {
		StringBuilder sb = new StringBuilder(128);
		sb.append('@');
		sb.append(this.annotationType.getName());
		sb.append('(');
		boolean first = true;
		for (Map.Entry<String, Object> next : this.memberValues.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(next.getKey());
			sb.append('=');
			sb.append(memberValueToString(next.getValue()));
		}
		sb.append(')');
		return sb.toString();
	}

	private static String memberValueToString(Object val) {
		Class<?> valType = val.getClass();
		if (!valType.isArray()) {
			return val.toString();
		} else if (valType == boolean[].class) {
			return Arrays.toString((boolean[]) val);
		} else if (valType == byte[].class) {
			return Arrays.toString((byte[]) val);
		} else if (valType == char[].class) {
			return Arrays.toString((char[]) val);
		} else if (valType == double[].class) {
			return Arrays.toString((double[]) val);
		} else if (valType == float[].class) {
			return Arrays.toString((float[]) val);
		} else if (valType == int[].class) {
			return Arrays.toString((int[]) val);
		} else if (valType == long[].class) {
			return Arrays.toString((long[]) val);
		} else if (valType == short[].class) {
			return Arrays.toString((short[]) val);
		} else {
			return Arrays.toString((Object[]) val);
		}
	}

	private Boolean equalsImpl(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!this.annotationType.isInstance(obj)) {
			return false;
		}

		Method[] methods = this.getMemberMethods();
		AnnotationInvocationHandler targetHandler = this.asOneOfUs(obj);
		for (Method method : methods) {
			String name = method.getName();
			Object value = this.memberValues.get(name);

			Object targetValue = null;
			if (targetHandler != null) {
				targetValue = targetHandler.memberValues.get(name);
			} else {
				try {
					targetValue = method.invoke(obj);
				} catch (InvocationTargetException e) {
					return false;
				} catch (IllegalAccessException e) {
					throw new AssertionError(e);
				}
			}

			if (!memberValueEquals(value, targetValue)) {
				return false;
			}
		}
		return true;
	}

	private AnnotationInvocationHandler asOneOfUs(Object obj) {
		if (Proxy.isProxyClass(obj.getClass())) {
			InvocationHandler handler = Proxy.getInvocationHandler(obj);
			if (handler instanceof AnnotationInvocationHandler) {
				return (AnnotationInvocationHandler) handler;
			}
		}
		return null;
	}

	private static boolean memberValueEquals(Object value, Object target) {
		if (value == target) {
			return true;
		}
		if (value == null || target == null) {
			return false;
		}
		Class<?> valueType = value.getClass();
		if (!valueType.isArray()) {
			return value.equals(target);
		} else if (value instanceof Object[] && target instanceof Object[]) {
			return Arrays.equals((Object[]) value, (Object[]) target);
		} else if (target.getClass() != valueType) {
			return false;
		} else if (valueType == boolean[].class) {
			return Arrays.equals((boolean[]) value, (boolean[]) target);
		} else if (valueType == byte[].class) {
			return Arrays.equals((byte[]) value, (byte[]) target);
		} else if (valueType == char[].class) {
			return Arrays.equals((char[]) value, (char[]) target);
		} else if (valueType == double[].class) {
			return Arrays.equals((double[]) value, (double[]) target);
		} else if (valueType == float[].class) {
			return Arrays.equals((float[]) value, (float[]) target);
		} else if (valueType == int[].class) {
			return Arrays.equals((int[]) value, (int[]) target);
		} else if (valueType == long[].class) {
			return Arrays.equals((long[]) value, (long[]) target);
		} else if (valueType == short[].class) {
			return Arrays.equals((short[]) value, (short[]) target);
		} else {
			// 执行不到此处
			return false;
		}
	}

	private Method[] getMemberMethods() {
		if (this.memberMethods == null) {
			Method[] methods = this.annotationType.getDeclaredMethods();
			List<Method> list = new ArrayList<>();
			for (Method method : methods) {
				if ((method.getParameterCount() == 0 && method.getReturnType() != void.class)) {
					list.add(method);
				}
			}
			this.memberMethods = list.toArray(new Method[0]);
		}
		return this.memberMethods;
	}


	private int hashCodeImpl() {
		int hash = 0;
		for (Map.Entry<String, Object> next : this.memberValues.entrySet()) {
			String key = next.getKey();
			Object val = next.getValue();
			hash += 127 * key.hashCode() ^ memberValueHashCode(val);
		}
		return hash;
	}

	private static int memberValueHashCode(Object value) {
		Class<?> valueType = value.getClass();
		if (!valueType.isArray()) {
			return value.hashCode();
		} else if (valueType == boolean[].class) {
			return Arrays.hashCode((boolean[]) value);
		} else if (valueType == byte[].class) {
			return Arrays.hashCode((byte[]) value);
		} else if (valueType == char[].class) {
			return Arrays.hashCode((char[]) value);
		} else if (valueType == double[].class) {
			return Arrays.hashCode((double[]) value);
		} else if (valueType == float[].class) {
			return Arrays.hashCode((float[]) value);
		} else if (valueType == int[].class) {
			return Arrays.hashCode((int[]) value);
		} else if (valueType == long[].class) {
			return Arrays.hashCode((long[]) value);
		} else if (valueType == short[].class) {
			return Arrays.hashCode((short[]) value);
		} else {
			return Arrays.hashCode((Object[]) value);
		}
	}

}
