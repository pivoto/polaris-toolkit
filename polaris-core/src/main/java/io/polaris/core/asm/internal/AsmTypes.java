package io.polaris.core.asm.internal;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.polaris.core.javapoet.ArrayTypeName;
import io.polaris.core.javapoet.ClassName;
import io.polaris.core.javapoet.ParameterizedTypeName;
import io.polaris.core.javapoet.TypeName;
import io.polaris.core.javapoet.TypeVariableName;
import io.polaris.core.javapoet.WildcardTypeName;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.string.StringCases;
import org.objectweb.asm.Type;

/**
 * ASM 类型工具类
 * <p>
 * 提供 Java 类型与 ASM Type 之间的转换、解析和判断功能。
 * 支持反射类型（java.lang.reflect.Type）和 JavaPoet 类型（TypeName）到 ASM 类型签名的转换，
 * 以及类型访问修饰符的判断、类型名称的获取等常用操作。
 * </p>
 *
 * @author Qt
 * @since Aug 05, 2023
 */
public class AsmTypes {

	/**
	 * 将 Java 反射类型转换为 ASM 类型签名
	 * <p>
	 * 支持以下类型的转换：
	 * <ul>
	 *   <li>Class - 基本类型和引用类型</li>
	 *   <li>ParameterizedType - 参数化类型（泛型）</li>
	 *   <li>WildcardType - 通配符类型（? extends T, ? super T）</li>
	 *   <li>GenericArrayType - 泛型数组类型</li>
	 * </ul>
	 * </p>
	 *
	 * @param type Java 反射类型，不能为 null
	 * @return ASM 类型签名字符串，例如 "Ljava/lang/String;"、"I"、"[Ljava/lang/Object;"
	 * @throws IllegalArgumentException 当遇到不支持的类型（如 TypeVariable）时抛出
	 */
	public static String toTypeSignature(java.lang.reflect.Type type) {
		if (type instanceof JavaType) {
			return toTypeSignature(((JavaType<?>) type).getRawType());
		}
		if (type instanceof Class) {
			if (((Class<?>) type).isPrimitive()) {
				return Type.getDescriptor(Types.getWrapperClass((Class<?>) type));
			} else {
				return Type.getDescriptor((Class<?>) type);
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			StringBuilder sb = new StringBuilder();
			sb.append("L").append(parameterizedType.getRawType().getTypeName().replace('.', '/'));
			sb.append("<");
			java.lang.reflect.Type[] args = parameterizedType.getActualTypeArguments();
			for (int i = 0; i < args.length; i++) {
				sb.append(toTypeSignature(args[i]));
			}
			sb.append(">;");
			return sb.toString();
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			java.lang.reflect.Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds.length > 0) {
				return "-" + toTypeSignature(lowerBounds[0]);
			} else {
				java.lang.reflect.Type[] upperBounds = wildcardType.getUpperBounds();
				if (upperBounds.length > 0) {
					return "+" + toTypeSignature(upperBounds[0]);
				} else {
					return toTypeSignature(Object.class);
				}
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			java.lang.reflect.Type componentType = genericArrayType.getGenericComponentType();
			return "[" + toTypeSignature(componentType);
		} else if (type instanceof TypeVariable) {
			throw new IllegalArgumentException("Unsupported type: " + type);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}

	/**
	 * 将 JavaPoet TypeName 转换为 ASM 类型签名
	 * <p>
	 * 支持以下类型的转换：
	 * <ul>
	 *   <li>ClassName - 类名（包括基本类型的装箱处理）</li>
	 *   <li>ParameterizedTypeName - 参数化类型（泛型）</li>
	 *   <li>WildcardTypeName - 通配符类型</li>
	 *   <li>ArrayTypeName - 数组类型</li>
	 * </ul>
	 * </p>
	 *
	 * @param typeName JavaPoet 类型名称，不能为 null
	 * @return ASM 类型签名字符串
	 * @throws IllegalArgumentException 当遇到不支持的类型（如 TypeVariableName）时抛出
	 */
	public static String toTypeSignature(TypeName typeName) {
		if (typeName instanceof ClassName) {
			if (typeName.isPrimitive()) {
				typeName = typeName.box();
			}
			return 'L' + ((ClassName) typeName).reflectionName().replace('.', '/') + ';';
		} else if (typeName instanceof ParameterizedTypeName) {
			StringBuilder sb = new StringBuilder();
			sb.append("L").append(((ParameterizedTypeName) typeName).rawType.reflectionName().replace('.', '/'));
			sb.append("<");
			List<TypeName> typeArguments = ((ParameterizedTypeName) typeName).typeArguments;
			for (TypeName typeArgument : typeArguments) {
				sb.append(toTypeSignature(typeArgument));
			}
			sb.append(">;");
			return sb.toString();
		} else if (typeName instanceof WildcardTypeName) {
			List<TypeName> lowerBounds = ((WildcardTypeName) typeName).lowerBounds;
			if (lowerBounds != null && !lowerBounds.isEmpty()) {
				return "-" + toTypeSignature(lowerBounds.get(0));
			}
			List<TypeName> upperBounds = ((WildcardTypeName) typeName).upperBounds;
			if (upperBounds != null && !upperBounds.isEmpty()) {
				return "+" + toTypeSignature(upperBounds.get(0));
			}
			return Type.getDescriptor(Object.class);
		} else if (typeName instanceof ArrayTypeName) {
			TypeName componentType = ((ArrayTypeName) typeName).componentType;
			return "[" + toTypeSignature(componentType);
		} else if (typeName instanceof TypeVariableName) {
			throw new IllegalArgumentException("Unsupported typeName: " + typeName);
		} else {
			throw new IllegalArgumentException("Unsupported typeName: " + typeName);
		}
	}

	/**
	 * 根据类名获取 ASM Type 对象
	 *
	 * @param className 类名（可以是简单类名或全限定类名）
	 * @return ASM Type 对象
	 */
	public static Type getType(String className) {
		return Type.getType(toInternalDescriptor(className));
	}

	/**
	 * 判断访问标志是否包含 final 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 final 返回 true，否则返回 false
	 */
	public static boolean isFinal(int access) {
		return (AsmConsts.ACC_FINAL & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 static 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 static 返回 true，否则返回 false
	 */
	public static boolean isStatic(int access) {
		return (AsmConsts.ACC_STATIC & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 protected 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 protected 返回 true，否则返回 false
	 */
	public static boolean isProtected(int access) {
		return (AsmConsts.ACC_PROTECTED & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 public 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 public 返回 true，否则返回 false
	 */
	public static boolean isPublic(int access) {
		return (AsmConsts.ACC_PUBLIC & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 abstract 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 abstract 返回 true，否则返回 false
	 */
	public static boolean isAbstract(int access) {
		return (AsmConsts.ACC_ABSTRACT & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 interface 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 interface 返回 true，否则返回 false
	 */
	public static boolean isInterface(int access) {
		return (AsmConsts.ACC_INTERFACE & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 private 修饰符
	 *
	 * @param access 访问标志
	 * @return 如果是 private 返回 true，否则返回 false
	 */
	public static boolean isPrivate(int access) {
		return (AsmConsts.ACC_PRIVATE & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 synthetic 修饰符（编译器生成的合成成员）
	 *
	 * @param access 访问标志
	 * @return 如果是 synthetic 返回 true，否则返回 false
	 */
	public static boolean isSynthetic(int access) {
		return (AsmConsts.ACC_SYNTHETIC & access) != 0;
	}

	/**
	 * 判断访问标志是否包含 bridge 修饰符（桥接方法）
	 *
	 * @param access 访问标志
	 * @return 如果是 bridge 返回 true，否则返回 false
	 */
	public static boolean isBridge(int access) {
		return (AsmConsts.ACC_BRIDGE & access) != 0;
	}

	/**
	 * 获取 ASM Type 对应的包名
	 *
	 * @param type ASM Type 对象
	 * @return 包名字符串，如果没有包名则返回空字符串
	 */
	public static String getPackageName(Type type) {
		return getPackageName(getClassName(type));
	}

	/**
	 * 获取类名对应的包名
	 *
	 * @param className 类的全限定名
	 * @return 包名字符串，如果没有包名则返回空字符串
	 */
	public static String getPackageName(String className) {
		int idx = className.lastIndexOf('.');
		return (idx < 0) ? "" : className.substring(0, idx);
	}

	/**
	 * 将字符串首字母大写
	 *
	 * @param s 输入字符串
	 * @return 首字母大写后的字符串
	 */
	public static String upperFirst(String s) {
		return StringCases.capitalize(s);
		/* if (s == null || s.length() == 0) {
			return s;
		}
		return Character.toUpperCase(s.charAt(0)) + s.substring(1); */
	}

	/**
	 * 获取 ASM Type 对应的 Java 类名
	 * <p>
	 * 对于数组类型，会递归获取组件类型并添加 "[]" 后缀。
	 * 对于基本类型，返回对应的关键字（如 "int"、"boolean" 等）。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return Java 类名字符串
	 */
	public static String getClassName(Type type) {
		switch (type.getSort()) {
			case Type.VOID:
				return "void";
			case Type.BOOLEAN:
				return "boolean";
			case Type.CHAR:
				return "char";
			case Type.BYTE:
				return "byte";
			case Type.SHORT:
				return "short";
			case Type.INT:
				return "int";
			case Type.FLOAT:
				return "float";
			case Type.LONG:
				return "long";
			case Type.DOUBLE:
				return "double";
			case Type.ARRAY:
				return getClassName(getComponentType(type)) + "[]";
			case Type.OBJECT:
			default:
				return type.getClassName();
		}
	}

	/**
	 * 获取 ASM Type 对应的类加载器使用的类名
	 * <p>
	 * 对于数组类型，返回描述符并将 '/' 替换为 '.'；
	 * 对于非数组类型，返回类名。
	 * 注意：此方法不考虑基本类型，因为 Class.forName() 不支持基本类型。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 类加载器使用的类名字符串
	 */
	public static String getClassLoadedName(Type type) {
		if (isArray(type)) {
			return type.getDescriptor().replace('/', '.');
		} else {
			// 这里不考虑基本类型，Class.forName() 不支持基本类型
			return type.getClassName();
		}
	}

	/**
	 * 通过 ASM Type 获取对应的 Class 对象
	 * <p>
	 * 使用当前线程的上下文类加载器加载类。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 对应的 Class 对象
	 * @throws ClassNotFoundException 当类无法找到时抛出
	 */
	public static Class<?> getClass(Type type) throws ClassNotFoundException {
		return getClass(type, Thread.currentThread().getContextClassLoader());
	}

	/**
	 * 通过 ASM Type 和指定的类加载器获取对应的 Class 对象
	 * <p>
	 * 支持基本类型、数组类型和普通类类型的加载。
	 * 对于数组类型，使用描述符进行加载；对于其他类型，使用类名进行加载。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @param classLoader 类加载器
	 * @return 对应的 Class 对象
	 * @throws ClassNotFoundException 当类无法找到时抛出
	 */
	public static Class<?> getClass(Type type, ClassLoader classLoader) throws ClassNotFoundException {
		switch (type.getSort()) {
			case Type.VOID:
				return Void.TYPE;
			case Type.BOOLEAN:
				return Boolean.TYPE;
			case Type.CHAR:
				return Character.TYPE;
			case Type.BYTE:
				return Byte.TYPE;
			case Type.SHORT:
				return Short.TYPE;
			case Type.INT:
				return Integer.TYPE;
			case Type.FLOAT:
				return Float.TYPE;
			case Type.LONG:
				return Long.TYPE;
			case Type.DOUBLE:
				return Double.TYPE;
			case Type.ARRAY:
				return Class.forName(type.getDescriptor().replace('/', '.'), true, classLoader);
			case Type.OBJECT:
			default:
				return Class.forName(type.getClassName(), true, classLoader);
		}
	}

	/**
	 * 向类型数组中添加一个额外的类型
	 * <p>
	 * 如果原数组为 null，则创建只包含额外类型的新数组；
	 * 如果额外类型已存在于数组中，则返回原数组；
	 * 否则创建新数组并追加额外类型。
	 * </p>
	 *
	 * @param types 原始类型数组，可以为 null
	 * @param extra 要添加的额外类型
	 * @return 包含额外类型的数组
	 */
	public static Type[] add(Type[] types, Type extra) {
		if (types == null) {
			return new Type[]{extra};
		} else {
			if (Arrays.asList(types).contains(extra)) {
				return types;
			}
			Type[] copy = new Type[types.length + 1];
			System.arraycopy(types, 0, copy, 0, types.length);
			copy[types.length] = extra;
			return copy;
		}
	}

	/**
	 * 合并两个类型数组
	 *
	 * @param t1 第一个类型数组
	 * @param t2 第二个类型数组
	 * @return 合并后的新数组，长度为 t1.length + t2.length
	 */
	public static Type[] add(Type[] t1, Type[] t2) {
		Type[] all = new Type[t1.length + t2.length];
		System.arraycopy(t1, 0, all, 0, t1.length);
		System.arraycopy(t2, 0, all, t1.length, t2.length);
		return all;
	}

	/**
	 * 从内部名称创建 ASM Type 对象
	 *
	 * @param name 类的内部名称（不包含 'L' 和 ';'，路径分隔符为 '/'）
	 * @return ASM Type 对象
	 */
	public static Type fromInternalName(String name) {
		return Type.getType("L" + name + ";");
	}

	/**
	 * 从内部名称数组创建 ASM Type 对象数组
	 *
	 * @param names 内部名称数组，可以为 null
	 * @return ASM Type 对象数组，如果输入为 null 则返回 null
	 */
	public static Type[] fromInternalNames(String[] names) {
		if (names == null) {
			return null;
		}
		Type[] types = new Type[names.length];
		for (int i = 0; i < names.length; i++) {
			types[i] = fromInternalName(names[i]);
		}
		return types;
	}

	/**
	 * 计算类型数组占用的栈大小
	 *
	 * @param types 类型数组
	 * @return 总栈大小（long 和 double 类型占用 2 个栈单元，其他类型占用 1 个）
	 */
	public static int getStackSize(Type[] types) {
		int size = 0;
		for (int i = 0; i < types.length; i++) {
			size += types[i].getSize();
		}
		return size;
	}

	/**
	 * 将 ASM Type 数组转换为内部名称数组
	 *
	 * @param types ASM Type 数组，可以为 null
	 * @return 内部名称数组，如果输入为 null 则返回 null
	 */
	public static String[] toInternalNames(Type[] types) {
		if (types == null) {
			return null;
		}
		String[] names = new String[types.length];
		for (int i = 0; i < types.length; i++) {
			names[i] = types[i].getInternalName();
		}
		return names;
	}


	/**
	 * 解析方法签名字符串为 Signature 对象
	 * <p>
	 * 签名字符串格式："返回类型 方法名(参数类型1, 参数类型2, ...)"
	 * 例如："void init(String, int)" 或 "java.lang.String toString()"
	 * </p>
	 *
	 * @param s 方法签名字符串
	 * @return 解析后的 Signature 对象
	 */
	public static Signature parseSignature(String s) {
		int space = s.indexOf(' ');
		int lparen = s.indexOf('(', space);
		int rparen = s.indexOf(')', lparen);
		String returnType = s.substring(0, space);
		String methodName = s.substring(space + 1, lparen);
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (Iterator<String> it = parseTypes(s, lparen + 1, rparen).iterator(); it.hasNext(); ) {
			sb.append(it.next());
		}
		sb.append(')');
		sb.append(toInternalDescriptor(returnType));
		return new Signature(methodName, sb.toString());
	}

	/**
	 * 将类型字符串解析为 ASM Type 对象
	 *
	 * @param s 类型字符串（可以是基本类型、类名或数组类型）
	 * @return ASM Type 对象
	 */
	public static Type parseType(String s) {
		return Type.getType(toInternalDescriptor(s));
	}

	/**
	 * 解析逗号分隔的类型字符串列表为 ASM Type 数组
	 * <p>
	 * 例如："String, int, java.util.List" 将被解析为三个 Type 对象
	 * </p>
	 *
	 * @param s 逗号分隔的类型字符串
	 * @return ASM Type 数组
	 */
	public static Type[] parseTypes(String s) {
		List<String> names = parseTypes(s, 0, s.length());
		Type[] types = new Type[names.size()];
		for (int i = 0; i < types.length; i++) {
			types[i] = Type.getType(names.get(i));
		}
		return types;
	}

	/**
	 * 根据参数类型数组解析构造方法签名
	 *
	 * @param types 构造方法的参数类型数组
	 * @return 构造方法的 Signature 对象，方法名为 "<init>"，返回类型为 "V"
	 */
	public static Signature parseConstructor(Type[] types) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < types.length; i++) {
			sb.append(types[i].getDescriptor());
		}
		sb.append(")");
		sb.append("V");
		return new Signature(AsmConsts.CONSTRUCTOR_NAME, sb.toString());
	}

	/**
	 * 解析构造方法参数字符串为 Signature 对象
	 * <p>
	 * 参数字符串格式："参数类型1, 参数类型2, ..."
	 * 例如："String, int" 或 "java.util.Map"
	 * </p>
	 *
	 * @param sig 构造方法参数字符串
	 * @return 构造方法的 Signature 对象
	 */
	public static Signature parseConstructor(String sig) {
		return parseSignature("void <init>(" + sig + ")");
	}

	/**
	 * 解析类型字符串列表（内部辅助方法）
	 * <p>
	 * 从指定位置开始，解析逗号分隔的类型字符串，直到结束位置。
	 * 每个类型字符串会被转换为内部描述符格式。
	 * </p>
	 *
	 * @param s 完整的类型字符串
	 * @param mark 起始位置
	 * @param end 结束位置
	 * @return 内部描述符格式的字符串列表
	 */
	private static List<String> parseTypes(String s, int mark, int end) {
		List<String> types = new ArrayList<>(5);
		for (; ; ) {
			int next = s.indexOf(',', mark);
			if (next < 0) {
				break;
			}
			types.add(toInternalDescriptor(s.substring(mark, next).trim()));
			mark = next + 1;
		}
		types.add(toInternalDescriptor(s.substring(mark, end).trim()));
		return types;
	}

	/**
	 * 将 Java 类型字符串转换为 ASM 内部描述符
	 * <p>
	 * 支持基本类型（void、boolean、char 等）、引用类型和数组类型。
	 * 对于没有包名的引用类型，自动添加 "java.lang." 前缀。
	 * 数组类型通过检测 "[]" 后缀来处理，并在描述符前添加 '[' 字符。
	 * </p>
	 *
	 * @param type Java 类型字符串
	 * @return ASM 内部描述符字符串
	 */
	private static String toInternalDescriptor(String type) {
		switch (type) {
			case "":
				return type;
			case "void":
				return "V";
			case "boolean":
				return "Z";
			case "char":
				return "C";
			case "byte":
				return "B";
			case "short":
				return "S";
			case "int":
				return "I";
			case "float":
				return "F";
			case "double":
				return "D";
			case "long":
				return "J";
			default:
				StringBuilder sb = new StringBuilder();
				int index = 0;
				while ((index = type.indexOf("[]", index) + 1) > 0) {
					sb.append('[');
				}
				type = type.substring(0, type.length() - sb.length() * 2);
				if (type.indexOf('.') < 0) {
					type = "java.lang." + type;
				}
				sb.append('L').append(type.replace('.', '/')).append(';');
				return sb.toString();
		}
	}

	/**
	 * 获取基本类型对应的包装类型
	 * <p>
	 * 例如：int -> Integer、boolean -> Boolean、double -> Double 等。
	 * 如果输入的不是基本类型，则直接返回原类型。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 对应的包装类型，如果不是基本类型则返回原类型
	 */
	public static Type getBoxedType(Type type) {
		switch (type.getSort()) {
			case Type.CHAR:
				return AsmConsts.TYPE_CHARACTER;
			case Type.BOOLEAN:
				return AsmConsts.TYPE_BOOLEAN;
			case Type.DOUBLE:
				return AsmConsts.TYPE_DOUBLE;
			case Type.FLOAT:
				return AsmConsts.TYPE_FLOAT;
			case Type.LONG:
				return AsmConsts.TYPE_LONG;
			case Type.INT:
				return AsmConsts.TYPE_INTEGER;
			case Type.SHORT:
				return AsmConsts.TYPE_SHORT;
			case Type.BYTE:
				return AsmConsts.TYPE_BYTE;
			default:
				return type;
		}
	}

	/**
	 * 获取包装类型对应的基本类型
	 * <p>
	 * 例如：Integer -> int、Boolean -> boolean、Double -> double 等。
	 * 如果输入的不是包装类型，则直接返回原类型。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 对应的基本类型，如果不是包装类型则返回原类型
	 */
	public static Type getUnboxedType(Type type) {
		if (AsmConsts.TYPE_INTEGER.equals(type)) {
			return Type.INT_TYPE;
		} else if (AsmConsts.TYPE_BOOLEAN.equals(type)) {
			return Type.BOOLEAN_TYPE;
		} else if (AsmConsts.TYPE_DOUBLE.equals(type)) {
			return Type.DOUBLE_TYPE;
		} else if (AsmConsts.TYPE_LONG.equals(type)) {
			return Type.LONG_TYPE;
		} else if (AsmConsts.TYPE_CHARACTER.equals(type)) {
			return Type.CHAR_TYPE;
		} else if (AsmConsts.TYPE_BYTE.equals(type)) {
			return Type.BYTE_TYPE;
		} else if (AsmConsts.TYPE_FLOAT.equals(type)) {
			return Type.FLOAT_TYPE;
		} else if (AsmConsts.TYPE_SHORT.equals(type)) {
			return Type.SHORT_TYPE;
		} else {
			return type;
		}
	}

	/**
	 * 判断类型是否为数组类型
	 *
	 * @param type ASM Type 对象
	 * @return 如果是数组类型返回 true，否则返回 false
	 */
	public static boolean isArray(Type type) {
		return type.getSort() == Type.ARRAY;
	}

	/**
	 * 获取数组类型的组件类型
	 * <p>
	 * 例如：String[] -> String、int[][] -> int[]、Object[][][] -> Object[][]
	 * </p>
	 *
	 * @param type 数组类型
	 * @return 组件类型
	 * @throws IllegalArgumentException 如果输入的不是数组类型
	 */
	public static Type getComponentType(Type type) {
		if (!isArray(type)) {
			throw new IllegalArgumentException("Type " + type + " is not an array");
		}
		return Type.getType(type.getDescriptor().substring(1));
	}

	/**
	 * 判断类型是否为基本类型
	 * <p>
	 * 基本类型包括：void、boolean、char、byte、short、int、float、long、double。
	 * 数组类型和对象类型不被视为基本类型。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 如果是基本类型返回 true，否则返回 false
	 */
	public static boolean isPrimitive(Type type) {
		switch (type.getSort()) {
			case Type.ARRAY:
			case Type.OBJECT:
				return false;
			default:
				return true;
		}
	}

	/**
	 * 模拟 Class.getName() 的行为获取类型名称
	 * <p>
	 * 对于数组类型，返回描述符并将 '/' 替换为 '.'；
	 * 对于非数组类型，返回类名（不考虑基本类型）。
	 * </p>
	 *
	 * @param type ASM Type 对象
	 * @return 类型名称字符串
	 */
	public static String emulateClassGetName(Type type) {
		if (isArray(type)) {
			return type.getDescriptor().replace('/', '.');
		} else {
			//return getClassName(type);
			// 这里不考虑基本类型
			return type.getClassName();
		}
	}

	/**
	 * 判断方法是否为构造方法
	 *
	 * @param method 方法信息对象
	 * @return 如果是构造方法（方法名为 "<init>"）返回 true，否则返回 false
	 */
	public static boolean isConstructor(MethodDescriptor method) {
		return method.getSignature().getName().equals(AsmConsts.CONSTRUCTOR_NAME);
	}

	/**
	 * 将 Class 数组转换为 ASM Type 数组
	 *
	 * @param classes Class 对象数组，可以为 null
	 * @return ASM Type 数组，如果输入为 null 则返回 null
	 */
	public static Type[] getTypes(Class<?>[] classes) {
		if (classes == null) {
			return null;
		}
		Type[] types = new Type[classes.length];
		for (int i = 0; i < classes.length; i++) {
			types[i] = Type.getType(classes[i]);
		}
		return types;
	}

	/**
	 * 获取 int 常量对应的 ASM 指令操作码
	 * <p>
	 * 仅支持 -1 到 5 范围内的整数常量，超出范围返回 -1 表示错误。
	 * 这些常量对应 ICONST_M1、ICONST_0 到 ICONST_5 指令。
	 * </p>
	 *
	 * @param value int 常量值（-1 到 5）
	 * @return 对应的操作码，如果超出范围返回 -1
	 */
	public static int ICONST(int value) {
		switch (value) {
			case -1:
				return AsmConsts.ICONST_M1;
			case 0:
				return AsmConsts.ICONST_0;
			case 1:
				return AsmConsts.ICONST_1;
			case 2:
				return AsmConsts.ICONST_2;
			case 3:
				return AsmConsts.ICONST_3;
			case 4:
				return AsmConsts.ICONST_4;
			case 5:
				return AsmConsts.ICONST_5;
		}
		return -1; // error
	}


	/**
	 * 获取 long 常量对应的 ASM 指令操作码
	 * <p>
	 * 仅支持 0L 和 1L 两个常量，其他值返回 -1 表示错误。
	 * 这些常量对应 LCONST_0 和 LCONST_1 指令。
	 * </p>
	 *
	 * @param value long 常量值（0 或 1）
	 * @return 对应的操作码，如果超出范围返回 -1
	 */
	public static int LCONST(long value) {
		if (value == 0L) {
			return AsmConsts.LCONST_0;
		} else if (value == 1L) {
			return AsmConsts.LCONST_1;
		} else {
			return -1; // error
		}
	}

	/**
	 * 获取 float 常量对应的 ASM 指令操作码
	 * <p>
	 * 仅支持 0f、1f 和 2f 三个常量，其他值返回 -1 表示错误。
	 * 这些常量对应 FCONST_0、FCONST_1 和 FCONST_2 指令。
	 * </p>
	 *
	 * @param value float 常量值（0、1 或 2）
	 * @return 对应的操作码，如果超出范围返回 -1
	 */
	public static int FCONST(float value) {
		if (value == 0f) {
			return AsmConsts.FCONST_0;
		} else if (value == 1f) {
			return AsmConsts.FCONST_1;
		} else if (value == 2f) {
			return AsmConsts.FCONST_2;
		} else {
			return -1; // error
		}
	}

	/**
	 * 获取 double 常量对应的 ASM 指令操作码
	 * <p>
	 * 仅支持 0d 和 1d 两个常量，其他值返回 -1 表示错误。
	 * 这些常量对应 DCONST_0 和 DCONST_1 指令。
	 * </p>
	 *
	 * @param value double 常量值（0 或 1）
	 * @return 对应的操作码，如果超出范围返回 -1
	 */
	public static int DCONST(double value) {
		if (value == 0d) {
			return AsmConsts.DCONST_0;
		} else if (value == 1d) {
			return AsmConsts.DCONST_1;
		} else {
			return -1; // error
		}
	}

	/**
	 * 获取基本类型数组对应的 NEWARRAY 指令操作码
	 * <p>
	 * 支持所有基本类型：byte、char、double、float、int、long、short、boolean。
	 * 引用类型数组不支持，返回 -1 表示错误。
	 * </p>
	 *
	 * @param type 基本类型的 ASM Type 对象
	 * @return 对应的操作码（T_BYTE、T_CHAR 等），如果不是基本类型返回 -1
	 */
	public static int NEWARRAY(Type type) {
		switch (type.getSort()) {
			case Type.BYTE:
				return AsmConsts.T_BYTE;
			case Type.CHAR:
				return AsmConsts.T_CHAR;
			case Type.DOUBLE:
				return AsmConsts.T_DOUBLE;
			case Type.FLOAT:
				return AsmConsts.T_FLOAT;
			case Type.INT:
				return AsmConsts.T_INT;
			case Type.LONG:
				return AsmConsts.T_LONG;
			case Type.SHORT:
				return AsmConsts.T_SHORT;
			case Type.BOOLEAN:
				return AsmConsts.T_BOOLEAN;
			default:
				return -1; // error
		}
	}

	/**
	 * 转义类型字符串中的特殊字符
	 * <p>
	 * 将以下特殊字符替换为 '$' 加十六进制 ASCII 码的形式：
	 * <ul>
	 *   <li>$ -> $24</li>
	 *   <li>. -> $2E</li>
	 *   <li>[ -> $5B</li>
	 *   <li>; -> $3B</li>
	 *   <li>( -> $28</li>
	 *   <li>) -> $29</li>
	 *   <li>/ -> $2F</li>
	 * </ul>
	 * 用于在生成类名或标识符时避免特殊字符冲突。
	 * </p>
	 *
	 * @param s 原始类型字符串
	 * @return 转义后的字符串
	 */
	public static String escapeType(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, len = s.length(); i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '$':
					sb.append("$24");
					break;
				case '.':
					sb.append("$2E");
					break;
				case '[':
					sb.append("$5B");
					break;
				case ';':
					sb.append("$3B");
					break;
				case '(':
					sb.append("$28");
					break;
				case ')':
					sb.append("$29");
					break;
				case '/':
					sb.append("$2F");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}


}
