package io.polaris.core.reflect;

import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.CharConsts;
import io.polaris.core.lang.Types;
import io.polaris.core.map.Maps;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings({"unchecked", "unused", "rawtypes"})
public class Reflects {
	public static final String TO_STRING = "toString";
	public static final String HASH_CODE = "hashCode";
	public static final String EQUALS = "equals";
	public static final String GET_CLASS = "getClass";
	public static final String CLONE = "clone";
	public static final String FINALIZE = "finalize";
	public static final String WAIT = "wait";
	public static final String NOTIFY = "notify";
	public static final String NOTIFY_ALL = "notifyAll";
	public static final String SET = "set";
	public static final String IS = "is";
	public static final String GET = "get";
	public static final String ANNOTATION_TYPE = "annotationType";
	public static final String MAIN_METHOD = "main";
	public static final Class<?>[] MAIN_METHOD_ARGS = {String[].class};
	private static final Map<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private static final Map<Class<?>, Field[]> FIELDS_CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());
	private static final Map<Class<?>, Method[]> METHODS_CACHE = Maps.newWeakKeyMap(new ConcurrentHashMap<>());


	/**
	 * 获取指定类型的继承自泛型类声明的方法的返回值实参
	 */
	public static Class findMethodGenericReturnType(Method method, Class targetType) {
		Type genericReturnType = method.getGenericReturnType();
		if (genericReturnType instanceof TypeVariable) {
			GenericDeclaration genericDeclaration = ((TypeVariable) genericReturnType).getGenericDeclaration();
			TypeVariable<?>[] typeParameters = genericDeclaration.getTypeParameters();
			int idx = -1;
			for (int i = 0; i < typeParameters.length; i++) {
				TypeVariable<?> typeParameter = typeParameters[i];
				if (typeParameter == genericReturnType) {
					idx = i;
					break;
				}
			}
			if (idx >= 0) {
				return findActualTypeArgument(method.getDeclaringClass(), targetType, idx);
			}
		}
		return null;
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param parameterizedSuperType 泛型基类
	 * @param obj                    目标类
	 * @param index                  位置
	 * @return 泛型实参
	 */
	public static Class findActualTypeArgument(Class parameterizedSuperType, Object obj, int index) {
		return findActualTypeArgument(parameterizedSuperType, obj.getClass(), index);
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param parameterizedSuperType 泛型基类
	 * @param targetClass            目标类
	 * @param index                  位置
	 * @return 泛型实参
	 */
	public static Class findActualTypeArgument(Class parameterizedSuperType, Class targetClass, int index) {
		if (parameterizedSuperType == targetClass || !parameterizedSuperType.isAssignableFrom(targetClass)) {
			return null;
		}
		TypeVariable<? extends Class>[] typeParameters = parameterizedSuperType.getTypeParameters();
		if (typeParameters.length <= index) {
			return null;
		}

		Deque<ParameterizedType> q = findParameterizedTypes(parameterizedSuperType, targetClass);

		int i = index;
		for (ParameterizedType t = q.pollLast(); t != null; t = q.pollLast()) {
			Type[] actualTypeArguments = t.getActualTypeArguments();
			if (actualTypeArguments[i] instanceof Class) {
				return (Class) actualTypeArguments[i];
			} else if (actualTypeArguments[i] instanceof WildcardType) {
				final Type[] upperBounds = ((WildcardType) actualTypeArguments[i]).getUpperBounds();
				if (upperBounds.length == 1) {
					return Types.getClass(upperBounds[0]);
				}
				return Object.class;
			} else if (actualTypeArguments[i] instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) actualTypeArguments[i]).getRawType();
				if (rawType instanceof Class) {
					return (Class) rawType;
				} else {
					return Object.class;
				}
			} else if (actualTypeArguments[i] instanceof TypeVariable) {
				for (int j = 0; j < i; j++) {
					if (actualTypeArguments[j] instanceof Class) {
						i--;
					}
				}
			} else if (actualTypeArguments[i] instanceof GenericArrayType) {
				Type componentType = ((GenericArrayType) actualTypeArguments[i]).getGenericComponentType();
				Class<?> componentClass = Types.getClass(componentType);
				return Types.getArrayClass(componentClass);
			}
		}
		return null;
	}


	@SuppressWarnings("DuplicatedCode")
	static Deque<ParameterizedType> findParameterizedTypes(Class parameterizedSuperType, Class targetClass) {
		Deque<ParameterizedType> q = new ArrayDeque<>();
		// region search
		Class that = targetClass;
		search:
		while (true) {
			//superclass
			Type genericSuperclass = that.getGenericSuperclass();
			if (genericSuperclass != null) {
				if (parameterizedSuperType == genericSuperclass) {
					break search;
				}
				if (genericSuperclass instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) genericSuperclass).getRawType();
					if (parameterizedSuperType == rawType) {
						q.offerLast((ParameterizedType) genericSuperclass);
						break search;
					} else if (rawType instanceof Class && parameterizedSuperType.isAssignableFrom((Class) rawType)) {
						that = (Class) rawType;
						q.offerLast((ParameterizedType) genericSuperclass);
						continue search;
					}
				} else if (genericSuperclass instanceof Class && parameterizedSuperType.isAssignableFrom((Class) genericSuperclass)) {
					that = (Class) genericSuperclass;
					continue search;
				}
			}
			//interfaces
			Type[] genericInterfaces = that.getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (parameterizedSuperType == genericInterface) {
					break search;
				}
				if (genericInterface instanceof ParameterizedType) {
					Type rawType = ((ParameterizedType) genericInterface).getRawType();
					if (parameterizedSuperType == rawType) {
						q.offerLast((ParameterizedType) genericInterface);
						break search;
					} else if (rawType instanceof Class && parameterizedSuperType.isAssignableFrom((Class) rawType)) {
						that = (Class) rawType;
						q.offerLast((ParameterizedType) genericInterface);
						continue search;
					}
				} else if (genericInterface instanceof Class && parameterizedSuperType.isAssignableFrom((Class) genericInterface)) {
					that = (Class) genericInterface;
					continue search;
				}
			}
			// never or error
			break search;
		}
		// endregion
		return q;
	}


	/**
	 * 得到指定类型的最近的泛型信息中指定位置的实参
	 */
	public static Class findActualTypeArgument(Class clazz, int index) {
		Class[] actualTypeArguments = findActualTypeArguments(clazz);
		if (actualTypeArguments == null || actualTypeArguments.length == 0) {
			return null;
		}
		return actualTypeArguments[index];
	}

	public static Class firstParameterizedType(Class clazz) {
		return findActualTypeArgument(clazz, 0);
	}

	/**
	 * 获取类型的最近的泛型参数
	 */
	public static Class[] findActualTypeArguments(Class clazz) {
		Deque<ParameterizedType> parameterizedTypes = findAllParameterizedTypes(clazz);
		if (parameterizedTypes.isEmpty()) {
			return null;
		}
		ParameterizedType parameterizedType = parameterizedTypes.peekFirst();
		Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

		Class[] types = new Class[actualTypeArguments.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = Types.getClass(actualTypeArguments[i]);
		}
		return types;
	}


	/**
	 * 获取类型的最近的所有泛型信息
	 */
	static Deque<ParameterizedType> findAllParameterizedTypes(Class clazz) {
		Deque<ParameterizedType> rs = new ArrayDeque<>();
		Deque<Type> q = new ArrayDeque<>();
		q.offerLast(clazz);
		while (!q.isEmpty()) {
			Type type = q.pollFirst();
			if (type instanceof ParameterizedType) {
				rs.offerLast((ParameterizedType) type);
			} else if (type instanceof Class) {
				if (!((Class) type).isInterface()) {
					Type genericSuperclass = ((Class) type).getGenericSuperclass();
					if (genericSuperclass != null && genericSuperclass != Object.class) {
						q.offerLast(genericSuperclass);
					}
				}
				Type[] genericInterfaces = ((Class) type).getGenericInterfaces();
				for (Type genericInterface : genericInterfaces) {
					q.offerLast(genericInterface);
				}
			}
		}
		return rs;
	}

	private static String toGetterOrSetterName(String name) {
		if (name.startsWith(GET) || name.startsWith(SET)) {
			name = name.substring(3);
		} else if (name.startsWith(IS)) {
			name = name.substring(2);
		}
		return Introspector.decapitalize(name);
	}

	public static String getLambdaMethodName(MethodReferenceReflection f) {
		return f.serialized().getImplMethodName();
	}


	public static <T> String getPropertyName(SerializableSupplier<T> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	public static <T> String getPropertyName(SerializableConsumer<T> setter) {
		return toGetterOrSetterName(setter.method().getName());
	}

	public static <T, R> String getPropertyName(GetterFunction<T, R> getter) {
		return toGetterOrSetterName(getter.method().getName());
	}

	public static <T, R> String getPropertyName(SetterFunction<T, R> setter) {
		return toGetterOrSetterName(setter.method().getName());
	}

	public static void setAccessible(AccessibleObject accessibleObject) {
		if ((null != accessibleObject) && (!accessibleObject.isAccessible())) {
			accessibleObject.setAccessible(true);
		}
	}

	public static <T> Constructor<T> getConstructor(@Nonnull Class<T> clazz, Class<?>... parameterTypes) {
		final Constructor<?>[] constructors = getConstructors(clazz);
		Class<?>[] pts;
		for (Constructor<?> constructor : constructors) {
			pts = constructor.getParameterTypes();
			if (Iterables.isMatchAll(pts, parameterTypes, (c1, c2) -> c1 == c2)) {
				setAccessible(constructor);
				return (Constructor<T>) constructor;
			}
		}
		for (Constructor<?> constructor : constructors) {
			pts = constructor.getParameterTypes();
			if (Iterables.isMatchAll(pts, parameterTypes, Class::isAssignableFrom)) {
				setAccessible(constructor);
				return (Constructor<T>) constructor;
			}
		}
		return null;
	}

	@SuppressWarnings({"ConstantValue", "StatementWithEmptyBody"})
	public static <T> Constructor<T>[] getConstructors(@Nonnull Class<T> beanClass) {
		Constructor<T>[] rs;
		// 防止因对象回收后导致WeakMap结果丢失，尝试多次获取
		while ((rs = (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, (c) -> getConstructorsDirectly(beanClass))) == null) {
		}
		return rs;
	}

	public static <T> Constructor<T>[] getConstructorsDirectly(@Nonnull Class<T> beanClass) {
		return (Constructor<T>[]) beanClass.getDeclaredConstructors();
	}

	/**
	 * 获得一个类中所有字段列表，包括其父类中的字段，子类字段在前
	 */
	@SuppressWarnings({"ConstantValue", "StatementWithEmptyBody"})
	public static Field[] getFields(Class<?> beanClass) {
		Field[] rs;
		// 防止因对象回收后导致WeakMap结果丢失，尝试多次获取
		while ((rs = FIELDS_CACHE.computeIfAbsent(beanClass, (c) -> getFieldsDirectly(beanClass, true))) == null) {
		}
		return rs;
	}

	@SuppressWarnings({"UseBulkOperation", "ManualArrayToCollectionCopy"})
	public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) {
		Class<?> searchType = beanClass;
		List<Field> list = new ArrayList<>();
		while (searchType != null) {
			Field[] declaredFields = searchType.getDeclaredFields();
			for (Field field : declaredFields) {
				list.add(field);
			}
			searchType = withSuperClassFields ? searchType.getSuperclass() : null;
		}
		return list.toArray(new Field[0]);
	}

	public static Field[] getFields(Class<?> beanClass, Predicate<Field> fieldFilter) {
		return Arrays.stream(getFields(beanClass)).filter(fieldFilter).toArray(Field[]::new);
	}

	public static Field getField(Class<?> beanClass, String name) {
		final Field[] fields = getFields(beanClass);
		return Arrays.stream(getFields(beanClass)).filter(f -> f.getName().equals(name)).findFirst().orElse(null);
	}

	public static Map<String, Field> getFieldMap(Class<?> beanClass) {
		final Field[] fields = getFields(beanClass);
		Map<String, Field> map = new HashMap<>((int) (fields.length * 1.5));
		for (Field field : fields) {
			map.putIfAbsent(field.getName(), field);
		}
		return map;
	}

	public static Object getFieldValueQuietly(Object o, String name) {
		try {
			return getFieldValue(o, name);
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	public static Object getFieldValue(Object o, String name) throws ReflectiveOperationException {
		Field field = getField(o.getClass(), name);
		if (field == null) {
			return null;
		}
		setAccessible(field);
		if (Modifier.isStatic(field.getModifiers())) {
			return field.get(null);
		} else {
			return field.get(o);
		}
	}

	public static void setFieldValue(Object o, String name, Object value) throws ReflectiveOperationException {
		Field field = getField(o.getClass(), name);
		if (field == null) {
			return;
		}
		if (!field.getType().isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException();
		}
		setAccessible(field);
		if (Modifier.isStatic(field.getModifiers())) {
			field.set(null, value);
		} else {
			field.set(o, value);
		}
	}

	private static String toMethodKey(Method method) {
		final StringBuilder sb = new StringBuilder();
		sb.append(method.getName()).append("(");
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		sb.append("):").append(method.getReturnType().getName());
		return sb.toString();
	}

	/**
	 * 获得一个类中所有方法列表，包括其父类中的方法
	 */
	@SuppressWarnings({"ConstantValue", "StatementWithEmptyBody"})
	public static Method[] getMethods(Class<?> beanClass) {
		Method[] rs;
		// 防止因对象回收后导致WeakMap结果丢失，尝试多次获取
		while ((rs = METHODS_CACHE.computeIfAbsent(beanClass,
			(c) -> getMethodsDirectly(beanClass, true, true))) == null) {
		}
		return rs;
	}

	/**
	 * 获得一个类中所有方法列表
	 *
	 * @param beanClass            类或接口
	 * @param withSupers           是否包括父类或接口的方法列表
	 * @param withMethodFromObject 是否包括Object中的方法
	 * @return methods
	 */
	public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) {
		if (beanClass.isInterface()) {
			// 对于接口，直接调用Class.getMethods方法获取所有方法
			return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
		}
		Map<String, Method> map = new LinkedHashMap<>();
		Class<?> searchType = beanClass;
		while (searchType != null) {
			if (!withMethodFromObject && Object.class == searchType) {
				break;
			}
			// 本类定义的方法
			for (Method m : searchType.getDeclaredMethods()) {
				map.putIfAbsent(toMethodKey(m), m);
			}
			// 对应接口中的非抽象方法（default方法）
			for (Class<?> ifc : searchType.getInterfaces()) {
				for (Method m : ifc.getMethods()) {
					if (!Modifier.isAbstract(m.getModifiers())) {
						map.putIfAbsent(toMethodKey(m), m);
					}
				}
			}
			searchType = (withSupers && !searchType.isInterface()) ? searchType.getSuperclass() : null;
		}
		return map.values().toArray(new Method[0]);
	}

	public static Method[] getMethods(Class<?> clazz, Predicate<Method> filter) {
		return Arrays.stream(getMethods(clazz)).filter(filter).toArray(Method[]::new);
	}

	public static Set<String> getMethodNames(Class<?> clazz) {
		Set<String> methodSet = new HashSet<>();
		Method[] methods = getMethods(clazz);
		for (Method method : methods) {
			methodSet.add(method.getName());
		}
		return methodSet;
	}

	/**
	 * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回null
	 */
	public static Method getMethodByName(Class<?> clazz, String methodName) {
		return getMethodByName(clazz, methodName, false);
	}

	/**
	 * 按照方法名查找指定方法名的方法，只返回匹配到的第一个方法，如果找不到对应的方法则返回null
	 */
	public static Method getMethodByName(Class<?> clazz, String methodName, boolean ignoreCase) {
		Method[] methods = getMethods(clazz);
		if (ignoreCase) {
			for (Method method : methods) {
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			}
		} else {
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
		}
		return null;
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		Method[] methods = getMethods(clazz);
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				if (Iterables.isMatchAll(method.getParameterTypes(), paramTypes, (c1, c2) -> c1 == c2)) {
					return method;
				}
			}
		}
		for (Method method : methods) {
			if (method.getName().equalsIgnoreCase(methodName)) {
				if (Iterables.isMatchAll(method.getParameterTypes(), paramTypes, Class::isAssignableFrom)) {
					return method;
				}
			}
		}
		return null;
	}

	public static boolean isGetterMethod(Method method) {
		return method != null && method.getParameterCount() == 0 && method.getName().length() > 2
			&&
			(method.getName().startsWith(IS) && method.getReturnType() == boolean.class
				|| method.getName().startsWith(GET) && method.getReturnType() != void.class)
			;
	}

	public static boolean isSetterMethod(Method method) {
		return method != null && method.getParameterCount() == 1
			&& method.getName().length() > 3 && method.getName().startsWith(SET);
	}

	public static boolean isEqualsMethod(Method method) {
		if (method == null || method.getParameterCount() != 1 || !EQUALS.equals(method.getName())) {
			return false;
		}
		return (method.getParameterTypes()[0] == Object.class);
	}

	public static boolean isHashCodeMethod(Method method) {
		return method != null && HASH_CODE.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isToStringMethod(Method method) {
		return method != null && TO_STRING.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isGetClassMethod(Method method) {
		return method != null && GET_CLASS.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isCloneMethod(Method method) {
		return method != null && CLONE.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isNotifyMethod(Method method) {
		return method != null && NOTIFY.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isNotifyAllMethod(Method method) {
		return method != null && NOTIFY_ALL.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isWaitMethod(Method method) {
		return method != null && WAIT.equals(method.getName()) &&
			(
				method.getParameterCount() == 0
					|| method.getParameterCount() == 1 && method.getParameterTypes()[0] == long.class
					|| method.getParameterCount() == 2 && method.getParameterTypes()[0] == long.class && method.getParameterTypes()[1] == int.class
			);
	}

	public static boolean isFinalizeMethod(Method method) {
		return method != null && FINALIZE.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public boolean isAnnotationTypeMethod(Method method) {
		return method != null && ANNOTATION_TYPE.equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isObjectDeclaredMethod(Method method) {
		return isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method) || isGetClassMethod(method)
			|| isCloneMethod(method) || isNotifyMethod(method) || isNotifyAllMethod(method)
			|| isWaitMethod(method) || isFinalizeMethod(method)
			;
	}


	/**
	 * 获得本类及其父类所有Public方法
	 */
	public static Method[] getPublicMethods(Class<?> clazz) {
		return null == clazz ? null : clazz.getMethods();
	}

	public static List<Method> getPublicMethods(Class<?> clazz, Predicate<Method> filter) {
		Method[] methods = getPublicMethods(clazz);
		List<Method> methodList = new ArrayList<>();
		for (Method method : methods) {
			if (filter.test(method)) {
				methodList.add(method);
			}
		}
		return methodList;
	}

	public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... argTypes) {
		try {
			return clazz.getMethod(methodName, argTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>[] argTypes, Class<?> returnType) {
		try {
			Method method = clazz.getMethod(methodName, argTypes);
			if (method.getReturnType() != returnType) {
				return null;
			}
			return method;
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}


	public static Method getPrivateMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		// see java.io.ObjectStreamClass.getPrivateMethod
		try {
			Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			int mods = method.getModifiers();
			return (
				((mods & Modifier.STATIC) == 0) &&
					((mods & Modifier.PRIVATE) != 0)
			) ? method : null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static Method getPrivateMethod(Class<?> clazz, String name, Class<?>[] argTypes, Class<?> returnType) {
		// see java.io.ObjectStreamClass.getPrivateMethod
		try {
			Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			int mods = method.getModifiers();
			return ((method.getReturnType() == returnType) &&
				((mods & Modifier.STATIC) == 0) &&
				((mods & Modifier.PRIVATE) != 0)) ? method : null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static Method getPrivateStaticMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		try {
			Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			int mods = method.getModifiers();
			return (
				((mods & Modifier.STATIC) != 0) &&
					((mods & Modifier.PRIVATE) != 0)
			) ? method : null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static Method getPrivateStaticMethod(Class<?> clazz, String name, Class<?>[] argTypes, Class<?> returnType) {
		try {
			Method method = clazz.getDeclaredMethod(name, argTypes);
			method.setAccessible(true);
			int mods = method.getModifiers();
			return ((method.getReturnType() == returnType) &&
				((mods & Modifier.STATIC) != 0) &&
				((mods & Modifier.PRIVATE) != 0)) ? method : null;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	public static Method getInheritableStaticMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		Method method = null;
		Class<?> defClass = clazz;
		while (defClass != null) {
			try {
				method = defClass.getDeclaredMethod(name, argTypes);
				break;
			} catch (NoSuchMethodException e) {
				defClass = defClass.getSuperclass();
			}
		}

		if (method == null) {
			return null;
		}
		method.setAccessible(true);
		int mods = method.getModifiers();
		if ((mods & Modifier.ABSTRACT) == 0) {
			if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
				if ((mods & Modifier.STATIC) != 0) {
					return method;
				}
			} else if ((mods & Modifier.PRIVATE) != 0) {
				if (clazz == defClass) {
					return method;
				}
			} else {
				if ((mods & Modifier.STATIC) != 0 && isSamePackage(clazz, defClass)) {
					return method;
				}
			}
		}
		return null;
	}

	public static Method getInheritableStaticMethod(Class<?> clazz, String name, Class<?>[] argTypes, Class<?> returnType) {
		Method method = null;
		Class<?> defClass = clazz;
		while (defClass != null) {
			try {
				method = defClass.getDeclaredMethod(name, argTypes);
				break;
			} catch (NoSuchMethodException e) {
				defClass = defClass.getSuperclass();
			}
		}

		if ((method == null) || (method.getReturnType() != returnType)) {
			return null;
		}
		method.setAccessible(true);
		int mods = method.getModifiers();
		if ((mods & Modifier.ABSTRACT) == 0) {
			if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
				if ((mods & Modifier.STATIC) != 0) {
					return method;
				}
			} else if ((mods & Modifier.PRIVATE) != 0) {
				if (clazz == defClass) {
					return method;
				}
			} else {
				if ((mods & Modifier.STATIC) != 0 && isSamePackage(clazz, defClass)) {
					return method;
				}
			}
		}
		return null;
	}

	public static Method getInheritableMethod(Class<?> clazz, String name, Class<?>... argTypes) {
		Method method = null;
		Class<?> defClass = clazz;
		while (defClass != null) {
			try {
				method = defClass.getDeclaredMethod(name, argTypes);
				break;
			} catch (NoSuchMethodException e) {
				defClass = defClass.getSuperclass();
			}
		}

		if (method == null) {
			return null;
		}
		method.setAccessible(true);
		int mods = method.getModifiers();
		if ((mods & (Modifier.STATIC | Modifier.ABSTRACT)) != 0) {
			return null;
		} else if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
			return method;
		} else if ((mods & Modifier.PRIVATE) != 0) {
			return (clazz == defClass) ? method : null;
		} else {
			return isSamePackage(clazz, defClass) ? method : null;
		}
	}

	public static Method getInheritableMethod(Class<?> clazz, String name, Class<?>[] argTypes, Class<?> returnType) {
		Method method = null;
		Class<?> defClass = clazz;
		while (defClass != null) {
			try {
				method = defClass.getDeclaredMethod(name, argTypes);
				break;
			} catch (NoSuchMethodException e) {
				defClass = defClass.getSuperclass();
			}
		}

		if ((method == null) || (method.getReturnType() != returnType)) {
			return null;
		}
		method.setAccessible(true);
		int mods = method.getModifiers();
		if ((mods & (Modifier.STATIC | Modifier.ABSTRACT)) != 0) {
			return null;
		} else if ((mods & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
			return method;
		} else if ((mods & Modifier.PRIVATE) != 0) {
			return (clazz == defClass) ? method : null;
		} else {
			return isSamePackage(clazz, defClass) ? method : null;
		}
	}

	public static boolean isSamePackage(Class<?> cl1, Class<?> cl2) {
		return (cl1.getClassLoader() == cl2.getClassLoader() && getPackageName(cl1).equals(getPackageName(cl2)));
	}

	public static String getPackageName(Class<?> cl) {
		// see java.io.ObjectStreamClass.getPackageName
		String s = cl.getName();
		int i = s.lastIndexOf('[');
		if (i >= 0) {
			s = s.substring(i + 2);
		}
		i = s.lastIndexOf('.');
		return (i >= 0) ? s.substring(0, i) : "";
	}

	public static <T> T newInstance(String className) throws ReflectiveOperationException {
		return (T) newInstance(Class.forName(className));
	}

	public static <T> T newInstance(Class<T> clazz, Class[] paramTypes, Object[] params) throws ReflectiveOperationException {
		Constructor<T> constructor = getConstructor(clazz, paramTypes);
		if (constructor != null) {
			return constructor.newInstance(params);
		}
		throw new NoSuchMethodException();
	}

	public static <T> T newInstance(Class<T> clazz, Object... params) throws ReflectiveOperationException {
		if (params.length == 0) {
			Constructor<T> constructor = getConstructor(clazz);
			if (constructor != null) {
				return constructor.newInstance();
			}
			constructor = getConstructor(clazz, Object[].class);
			if (constructor != null) {
				return constructor.newInstance((Object[]) params);
			}
		} else {
			Class<?>[] paramTypes = new Class[params.length];
			for (int i = 0; i < params.length; i++) {
				paramTypes[i] = params[i] == null ? Object.class : params[i].getClass();
			}
			Constructor<T> constructor = getConstructor(clazz, paramTypes);
			if (constructor != null) {
				return constructor.newInstance(params);
			}
		}
		throw new NoSuchMethodException();
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstanceIfPossible(Class<T> type) {
		if (Types.isPrimitiveWrapper(type)) {
			type = Types.getPrimitiveClassByWrapper(type);
		}
		if (type.isPrimitive()) {
			return (T) Types.getDefaultValue(type);
		}

		if (Modifier.isAbstract(type.getModifiers())) {
			// 某些特殊接口的实例化按照默认实现进行
			if (type.isAssignableFrom(AbstractMap.class)) {
				type = (Class<T>) HashMap.class;
			} else if (type.isAssignableFrom(ConcurrentNavigableMap.class)) {
				type = (Class<T>) ConcurrentSkipListMap.class;
			} else if (type.isAssignableFrom(ConcurrentMap.class)) {
				type = (Class<T>) ConcurrentHashMap.class;
			} else if (type.isAssignableFrom(NavigableMap.class)) {
				type = (Class<T>) TreeMap.class;
			} else if (type.isAssignableFrom(List.class)) {
				type = (Class<T>) ArrayList.class;
			} else if (type.isAssignableFrom(Set.class)) {
				type = (Class<T>) HashSet.class;
			} else if (type.isAssignableFrom(BlockingDeque.class)) {
				type = (Class<T>) LinkedBlockingDeque.class;
			} else if (type.isAssignableFrom(Deque.class)) {
				type = (Class<T>) ArrayDeque.class;
			} else {
				// 不可实例化
				return null;
			}
		}

		// 枚举
		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}

		// 数组
		if (type.isArray()) {
			return (T) Array.newInstance(type.getComponentType(), 0);
		}

		try {
			return newInstance(type);
		} catch (Exception ignore) {
		}

		final Constructor<T>[] constructors = getConstructors(type);
		Class<?>[] parameterTypes;
		for (Constructor<T> constructor : constructors) {
			parameterTypes = constructor.getParameterTypes();
			if (0 == parameterTypes.length) {
				continue;
			}
			setAccessible(constructor);
			try {
				return constructor.newInstance(Types.getDefaultValues(parameterTypes));
			} catch (Exception ignore) {
			}
		}
		return null;
	}

	public static <T> T invokeStatic(Method method, Object... args) throws ReflectiveOperationException {
		return invoke(null, method, args);
	}

	public static <T> T invoke(Object obj, Method method, Object... args) throws ReflectiveOperationException {
		setAccessible(method);
		return (T) method.invoke(Modifier.isStatic(method.getModifiers()) ? null : obj, args);
	}

	public static <T> T invokeQuietly(Object obj, Method method, Object... args) {
		try {
			return invoke(obj, method, args);
		} catch (ReflectiveOperationException ignore) {
			return null;
		}
	}

	public Object invokeMain(Class clazz) throws ReflectiveOperationException {
		return invokeMain(clazz, new String[0]);
	}

	public Object invokeMain(Class clazz, String... mainArgs) throws ReflectiveOperationException {
		Method main = clazz.getMethod(MAIN_METHOD, MAIN_METHOD_ARGS);
		return main.invoke(null, new Object[]{mainArgs});
	}

	public <T> T invoke(Class clazz, String methodName, Class[] paramTypes, Object[] paramValues) throws ReflectiveOperationException {
		Method method = Reflects.getMethod(clazz, methodName, paramTypes);
		if (method == null) {
			return null;
		}
		if (Modifier.isStatic(method.getModifiers())) {
			return (T) Reflects.invoke(null, method, paramValues);
		}
		Object o = Reflects.newInstanceIfPossible(clazz);
		return (T) Reflects.invoke(o, method, paramValues);
	}

	/**
	 * 设置final的field字段可以被修改
	 * 只要不会被编译器内联优化的 final 属性就可以通过反射进行修改
	 * <ul>以下属性，编译器会内联优化，无法通过反射修改：
	 * <li> 基本类型 byte, char, short, int, long, float, double, boolean</li>
	 * <li> Literal String 类型(直接双引号字符串)</li>
	 * </ul>
	 * <ul>
	 * 以下属性，可以通过反射修改：
	 * <li>基本类型的包装类 Byte、Character、Short、Long、Float、Double、Boolean</li>
	 * <li>字符串，通过 new String("")实例化</li>
	 * <li>自定义java类</li>
	 * </ul>
	 */
	public static boolean removeFinalModifier(Field field) throws ReflectiveOperationException {
		if (Modifier.isFinal(field.getModifiers())) {
			setAccessible(field);
			//去除final修饰符的影响，将字段设为可修改的
			final Field modifiersField = Field.class.getDeclaredField("modifiers");
			//Field 的 modifiers 是私有的
			modifiersField.setAccessible(true);
			modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
			return true;
		}
		return false;
	}


	public static Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, null);
	}


	public static Class<?> loadClass(String name, ClassLoader classLoader) throws ClassNotFoundException {
		name = name.replace(CharConsts.SLASH, CharConsts.DOT);
		if (classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}

		Class type = Types.getPrimitiveClassByName(name);
		if (type == null) {
			type = doLoadClass(name, classLoader);
		}
		return type;
	}

	private static Class doLoadClass(String name, ClassLoader classLoader) throws ClassNotFoundException {
		Class<?> clazz;
		if (name.endsWith("[]")) {
			// xx[]
			final String elementClassName = name.substring(0, name.length() - 2);
			final Class<?> elementClass = loadClass(elementClassName, classLoader);
			clazz = Array.newInstance(elementClass, 0).getClass();
		} else if (name.startsWith("[L") && name.endsWith(";")) {
			// [Lxx;
			final String elementName = name.substring(2, name.length() - 1);
			final Class<?> elementClass = loadClass(elementName, classLoader);
			clazz = Array.newInstance(elementClass, 0).getClass();
		} else if (name.startsWith("[")) {
			// [[I , [[Lxx;
			final String elementName = name.substring(1);
			final Class<?> elementClass = loadClass(elementName, classLoader);
			clazz = Array.newInstance(elementClass, 0).getClass();
		} else {
			try {
				clazz = Class.forName(name, true, classLoader);
			} catch (ClassNotFoundException ex) {
				// 尝试获取内部类，例如java.lang.Thread.State =》java.lang.Thread$State
				clazz = tryLoadInnerClass(name, classLoader);
				if (null == clazz) {
					throw ex;
				}
			}
		}
		return clazz;
	}

	private static Class<?> tryLoadInnerClass(String name, ClassLoader classLoader) {
		int lastDotIndex = name.lastIndexOf(CharConsts.DOT);
		if (lastDotIndex > 0) {
			String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
			try {
				return Class.forName(innerClassName, true, classLoader);
			} catch (ClassNotFoundException e) {
				return tryLoadInnerClass(innerClassName, classLoader);
			}
		}
		return null;
	}
}
