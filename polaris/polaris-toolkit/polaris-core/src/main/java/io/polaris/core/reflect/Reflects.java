package io.polaris.core.reflect;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.CharConsts;
import io.polaris.core.lang.Types;
import io.polaris.core.map.Maps;

import javax.annotation.Nonnull;
import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

/**
 * @author Qt
 * @since 1.8
 */
public class Reflects {
	public static final String MAIN_METHOD = "main";
	public static final Class[] MAIN_METHOD_ARGS = {String[].class};
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
				return findParameterizedType(method.getDeclaringClass(), targetType, idx);
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
	 * @return
	 */
	public static Class findParameterizedType(Class parameterizedSuperType, Object obj, int index) {
		return findParameterizedType(parameterizedSuperType, obj.getClass(), index);
	}

	/**
	 * 得到指定类型的指定位置的泛型实参
	 *
	 * @param parameterizedSuperType 泛型基类
	 * @param targetClass            目标类
	 * @param index                  位置
	 * @return
	 */
	public static Class findParameterizedType(Class parameterizedSuperType, Class targetClass, int index) {
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
			if (genericInterfaces != null && genericInterfaces.length > 0) {
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
	public static Class findParameterizedType(Class clazz, int index) {
		Class[] actualTypeArguments = findParameterizedTypes(clazz);
		if (actualTypeArguments == null || actualTypeArguments.length == 0) {
			return null;
		}
		return actualTypeArguments[index];
	}

	public static Class firstParameterizedType(Class clazz) {
		return findParameterizedType(clazz, 0);
	}

	/**
	 * 获取类型的最近的泛型参数
	 */
	public static Class[] findParameterizedTypes(Class clazz) {
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
		if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else if (name.startsWith("is")) {
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

	public static <T, R> String getPropertyName(SerializableFunction<T, R> getter) {
		return toGetterOrSetterName(getter.method().getName());
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
			if (Iterables.isMatchAll(pts, parameterTypes, (c1, c2) -> c1.isAssignableFrom(c2))) {
				setAccessible(constructor);
				return (Constructor<T>) constructor;
			}
		}
		return null;
	}

	public static <T> Constructor<T>[] getConstructors(@Nonnull Class<T> beanClass) {
		return (Constructor<T>[]) CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, (c) -> getConstructorsDirectly(beanClass));
	}

	public static <T> Constructor<T>[] getConstructorsDirectly(@Nonnull Class<T> beanClass) {
		return (Constructor<T>[]) beanClass.getDeclaredConstructors();
	}

	/**
	 * 获得一个类中所有字段列表，包括其父类中的字段，子类字段在前
	 */
	public static Field[] getFields(Class<?> beanClass) {
		return FIELDS_CACHE.computeIfAbsent(beanClass, (c) -> getFieldsDirectly(beanClass, true));
	}

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
		return Arrays.stream(getFields(beanClass)).filter(fieldFilter).toArray(i -> new Field[i]);
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

	public static String toMethodKey(Method method) {
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
	public static Method[] getMethods(Class<?> beanClass) {
		return METHODS_CACHE.computeIfAbsent(beanClass,
			(c) -> getMethodsDirectly(beanClass, true, true));
	}

	/**
	 * 获得一个类中所有方法列表
	 *
	 * @param beanClass            类或接口
	 * @param withSupers           是否包括父类或接口的方法列表
	 * @param withMethodFromObject 是否包括Object中的方法
	 * @return
	 * @
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
		return Arrays.stream(getMethods(clazz)).filter(filter).toArray(l -> new Method[l]);
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
				if (Iterables.isMatchAll(method.getParameterTypes(), paramTypes, (c1, c2) -> c1.isAssignableFrom(c2))) {
					return method;
				}
			}
		}
		return null;
	}

	public static boolean isGetterMethod(Method method) {
		return method != null && method.getParameterCount() == 0
			&& (
			method.getName().length() > 2 && method.getName().startsWith("is") && method.getReturnType() == boolean.class
				|| method.getName().length() > 3 && method.getName().startsWith("get")
		);
	}

	public static boolean isSetterMethod(Method method) {
		return method != null && method.getParameterCount() == 1
			&& method.getName().length() > 3 && method.getName().startsWith("set");
	}

	public static boolean isEqualsMethod(Method method) {
		if (method == null || method.getParameterCount() != 1 || !"equals".equals(method.getName())) {
			return false;
		}
		return (method.getParameterTypes()[0] == Object.class);
	}

	public static boolean isHashCodeMethod(Method method) {
		return method != null && "hashCode".equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isToStringMethod(Method method) {
		return method != null && "toString".equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static boolean isGetClassMethod(Method method) {
		return method != null && "getClass".equals(method.getName()) && method.getParameterCount() == 0;
	}

	public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
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

	public static <T> T newInstance(String className) throws ReflectiveOperationException {
		return (T) Class.forName(className).newInstance();
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

	public static <T> T newInstanceIfPossible(Class<T> type) {
		if (type.isPrimitive()) {
			return (T) Types.getDefaultValue(type);
		}
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
		}

		try {
			return newInstance(type);
		} catch (Exception ignore) {
		}

		// 枚举
		if (type.isEnum()) {
			return type.getEnumConstants()[0];
		}

		// 数组
		if (type.isArray()) {
			return (T) Array.newInstance(type.getComponentType(), 0);
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
			// xxx[]
			final String elementClassName = name.substring(0, name.length() - 2);
			final Class<?> elementClass = loadClass(elementClassName, classLoader);
			clazz = Array.newInstance(elementClass, 0).getClass();
		} else if (name.startsWith("[L") && name.endsWith(";")) {
			// [Lxxx;
			final String elementName = name.substring(2, name.length() - 1);
			final Class<?> elementClass = loadClass(elementName, classLoader);
			clazz = Array.newInstance(elementClass, 0).getClass();
		} else if (name.startsWith("[")) {
			// [[I , [[Lxxx;
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
