package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;

/**
 * Java 类型系统中的任何类型，包括 {@code void}。此类是原始类型的标识符，
 * 如 {@code int}，以及原始引用类型如 {@code String} 和 {@code List}。
 * 它还标识复合类型如 {@code char[]} 和 {@code Set<Long>}。
 *
 * <p>类型名称仅仅是标识符，不模拟它们所命名的值。例如，
 * {@code java.util.List} 的类型名称不知道 {@code size()} 方法、
 * 列表是集合的事实，甚至不知道它接受单个类型参数。
 *
 * <p>此类的实例是不可变的值对象，正确实现了 {@code equals()} 和 {@code hashCode()}。
 *
 * <h3>引用现有类型</h3>
 *
 * <p>原始类型和 void 是可以直接引用的常量：参见 {@link #INT}、{@link #DOUBLE} 和 {@link #VOID}。
 *
 * <p>在注解处理器中，可以通过调用 {@link #get(TypeMirror)} 获取类型镜像的类型名称实例。
 * 在反射代码中，可以使用 {@link #get(Type)}。
 *
 * <h3>定义新类型</h3>
 *
 * <p>使用 {@link ClassName#get(String, String, String...)} 创建新的引用类型，
 * 如 {@code com.example.HelloWorld}。要构建复合类型如 {@code char[]} 和 {@code Set<Long>}，
 * 请使用 {@link ArrayTypeName}、{@link ParameterizedTypeName}、
 * {@link TypeVariableName} 和 {@link WildcardTypeName} 上的工厂方法。
 */
public class TypeName {
	public static final TypeName VOID = new TypeName("void");
	public static final TypeName BOOLEAN = new TypeName("boolean");
	public static final TypeName BYTE = new TypeName("byte");
	public static final TypeName SHORT = new TypeName("short");
	public static final TypeName INT = new TypeName("int");
	public static final TypeName LONG = new TypeName("long");
	public static final TypeName CHAR = new TypeName("char");
	public static final TypeName FLOAT = new TypeName("float");
	public static final TypeName DOUBLE = new TypeName("double");
	public static final ClassName OBJECT = ClassName.get("java.lang", "Object");

	private static final ClassName BOXED_VOID = ClassName.get("java.lang", "Void");
	private static final ClassName BOXED_BOOLEAN = ClassName.get("java.lang", "Boolean");
	private static final ClassName BOXED_BYTE = ClassName.get("java.lang", "Byte");
	private static final ClassName BOXED_SHORT = ClassName.get("java.lang", "Short");
	private static final ClassName BOXED_INT = ClassName.get("java.lang", "Integer");
	private static final ClassName BOXED_LONG = ClassName.get("java.lang", "Long");
	private static final ClassName BOXED_CHAR = ClassName.get("java.lang", "Character");
	private static final ClassName BOXED_FLOAT = ClassName.get("java.lang", "Float");
	private static final ClassName BOXED_DOUBLE = ClassName.get("java.lang", "Double");

	/** 如果此类型是关键字，则为其名称，否则为 null。 */
	private final String keyword;
	public final List<AnnotationSpec> annotations;

	/** 此类型名称的延迟初始化 toString。 */
	private String cachedString;

	private TypeName(String keyword) {
		this(keyword, new ArrayList<>());
	}

	private TypeName(String keyword, List<AnnotationSpec> annotations) {
		this.keyword = keyword;
		this.annotations = Util.immutableList(annotations);
	}

	// 包私有构造函数，防止第三方子类化。
	TypeName(List<AnnotationSpec> annotations) {
		this(null, annotations);
	}

	public final TypeName annotated(AnnotationSpec... annotations) {
		return annotated(Arrays.asList(annotations));
	}

	public TypeName annotated(List<AnnotationSpec> annotations) {
		Util.checkNotNull(annotations, "annotations == null");
		return new TypeName(keyword, concatAnnotations(annotations));
	}

	public TypeName withoutAnnotations() {
		if (annotations.isEmpty()) {
			return this;
		}
		return new TypeName(keyword);
	}

	protected final List<AnnotationSpec> concatAnnotations(List<AnnotationSpec> annotations) {
		List<AnnotationSpec> allAnnotations = new ArrayList<>(this.annotations);
		allAnnotations.addAll(annotations);
		return allAnnotations;
	}

	public boolean isAnnotated() {
		return !annotations.isEmpty();
	}

	/**
	 * 如果这是原始类型如 {@code int}，则返回 true。对于所有其他类型，
	 * 包括装箱原始类型和 {@code void}，返回 false。
	 */
	public boolean isPrimitive() {
		return keyword != null && this != VOID;
	}

	/**
	 * 如果这是装箱原始类型如 {@code Integer}，则返回 true。对于所有其他类型，
	 * 包括未装箱原始类型和 {@code java.lang.Void}，返回 false。
	 */
	public boolean isBoxedPrimitive() {
		TypeName thisWithoutAnnotations = withoutAnnotations();
		return thisWithoutAnnotations.equals(BOXED_BOOLEAN)
			|| thisWithoutAnnotations.equals(BOXED_BYTE)
			|| thisWithoutAnnotations.equals(BOXED_SHORT)
			|| thisWithoutAnnotations.equals(BOXED_INT)
			|| thisWithoutAnnotations.equals(BOXED_LONG)
			|| thisWithoutAnnotations.equals(BOXED_CHAR)
			|| thisWithoutAnnotations.equals(BOXED_FLOAT)
			|| thisWithoutAnnotations.equals(BOXED_DOUBLE);
	}

	/**
	 * 如果这是原始类型（如 {@code int} 对应 {@code Integer}）或 {@code void}，
	 * 则返回装箱类型。如果不适用装箱，则返回此类型。
	 */
	public TypeName box() {
		if (keyword == null) return this; // Doesn't need boxing.
		TypeName boxed = null;
		if (keyword.equals(VOID.keyword)) boxed = BOXED_VOID;
		else if (keyword.equals(BOOLEAN.keyword)) boxed = BOXED_BOOLEAN;
		else if (keyword.equals(BYTE.keyword)) boxed = BOXED_BYTE;
		else if (keyword.equals(SHORT.keyword)) boxed = BOXED_SHORT;
		else if (keyword.equals(INT.keyword)) boxed = BOXED_INT;
		else if (keyword.equals(LONG.keyword)) boxed = BOXED_LONG;
		else if (keyword.equals(CHAR.keyword)) boxed = BOXED_CHAR;
		else if (keyword.equals(FLOAT.keyword)) boxed = BOXED_FLOAT;
		else if (keyword.equals(DOUBLE.keyword)) boxed = BOXED_DOUBLE;
		else throw new AssertionError(keyword);
		return annotations.isEmpty() ? boxed : boxed.annotated(annotations);
	}

	/**
	 * 如果这是装箱原始类型（如 {@code Integer} 对应 {@code int}）或 {@code Void}，
	 * 则返回未装箱类型。如果已未装箱，则返回此类型。
	 *
	 * @throws UnsupportedOperationException 如果此类型不符合拆箱条件。
	 */
	public TypeName unbox() {
		if (keyword != null) return this; // Already unboxed.
		TypeName thisWithoutAnnotations = withoutAnnotations();
		TypeName unboxed = null;
		if (thisWithoutAnnotations.equals(BOXED_VOID)) unboxed = VOID;
		else if (thisWithoutAnnotations.equals(BOXED_BOOLEAN)) unboxed = BOOLEAN;
		else if (thisWithoutAnnotations.equals(BOXED_BYTE)) unboxed = BYTE;
		else if (thisWithoutAnnotations.equals(BOXED_SHORT)) unboxed = SHORT;
		else if (thisWithoutAnnotations.equals(BOXED_INT)) unboxed = INT;
		else if (thisWithoutAnnotations.equals(BOXED_LONG)) unboxed = LONG;
		else if (thisWithoutAnnotations.equals(BOXED_CHAR)) unboxed = CHAR;
		else if (thisWithoutAnnotations.equals(BOXED_FLOAT)) unboxed = FLOAT;
		else if (thisWithoutAnnotations.equals(BOXED_DOUBLE)) unboxed = DOUBLE;
		else throw new UnsupportedOperationException("cannot unbox " + this);
		return annotations.isEmpty() ? unboxed : unboxed.annotated(annotations);
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (getClass() != o.getClass()) return false;
		return toString().equals(o.toString());
	}

	@Override
	public final int hashCode() {
		return toString().hashCode();
	}

	@Override
	public final String toString() {
		String result = cachedString;
		if (result == null) {
			try {
				StringBuilder resultBuilder = new StringBuilder();
				CodeWriter codeWriter = new CodeWriter(resultBuilder);
				emit(codeWriter);
				result = resultBuilder.toString();
				cachedString = result;
			} catch (IOException e) {
				throw new AssertionError();
			}
		}
		return result;
	}

	CodeWriter emit(CodeWriter out) throws IOException {
		if (keyword == null) throw new AssertionError();

		if (isAnnotated()) {
			out.emit("");
			emitAnnotations(out);
		}
		return out.emitAndIndent(keyword);
	}

	CodeWriter emitAnnotations(CodeWriter out) throws IOException {
		for (AnnotationSpec annotation : annotations) {
			annotation.emit(out, true);
			out.emit(" ");
		}
		return out;
	}


	/** 返回等效于 {@code mirror} 的类型名称。 */
	public static TypeName get(TypeMirror mirror) {
		return get(mirror, new LinkedHashMap<>());
	}

	static TypeName get(TypeMirror mirror,
		final Map<TypeParameterElement, TypeVariableName> typeVariables) {
		return mirror.accept(new SimpleTypeVisitor8<TypeName, Void>() {
			@Override
			public TypeName visitPrimitive(PrimitiveType t, Void p) {
				switch (t.getKind()) {
					case BOOLEAN:
						return TypeName.BOOLEAN;
					case BYTE:
						return TypeName.BYTE;
					case SHORT:
						return TypeName.SHORT;
					case INT:
						return TypeName.INT;
					case LONG:
						return TypeName.LONG;
					case CHAR:
						return TypeName.CHAR;
					case FLOAT:
						return TypeName.FLOAT;
					case DOUBLE:
						return TypeName.DOUBLE;
					default:
						throw new AssertionError();
				}
			}

			@Override
			public TypeName visitDeclared(DeclaredType t, Void p) {
				ClassName rawType = ClassName.get((TypeElement) t.asElement());
				TypeMirror enclosingType = t.getEnclosingType();
				TypeName enclosing =
					(enclosingType.getKind() != TypeKind.NONE)
						&& !t.asElement().getModifiers().contains(Modifier.STATIC)
						? enclosingType.accept(this, null)
						: null;
				if (t.getTypeArguments().isEmpty() && !(enclosing instanceof ParameterizedTypeName)) {
					return rawType;
				}

				List<TypeName> typeArgumentNames = new ArrayList<>();
				for (TypeMirror mirror : t.getTypeArguments()) {
					typeArgumentNames.add(get(mirror, typeVariables));
				}
				return enclosing instanceof ParameterizedTypeName
					? ((ParameterizedTypeName) enclosing).nestedClass(
					rawType.simpleName(), typeArgumentNames)
					: new ParameterizedTypeName(null, rawType, typeArgumentNames);
			}

			@Override
			public TypeName visitError(ErrorType t, Void p) {
				return visitDeclared(t, p);
			}

			@Override
			public ArrayTypeName visitArray(ArrayType t, Void p) {
				return ArrayTypeName.get(t, typeVariables);
			}

			@Override
			public TypeName visitTypeVariable(javax.lang.model.type.TypeVariable t, Void p) {
				return TypeVariableName.get(t, typeVariables);
			}

			@Override
			public TypeName visitWildcard(javax.lang.model.type.WildcardType t, Void p) {
				return WildcardTypeName.get(t, typeVariables);
			}

			@Override
			public TypeName visitNoType(NoType t, Void p) {
				if (t.getKind() == TypeKind.VOID) return TypeName.VOID;
				return super.visitUnknown(t, p);
			}

			@Override
			protected TypeName defaultAction(TypeMirror e, Void p) {
				throw new IllegalArgumentException("Unexpected type mirror: " + e);
			}
		}, null);
	}

	/** 返回等效于 {@code type} 的类型名称。 */
	public static TypeName get(Type type) {
		return get(type, new LinkedHashMap<>());
	}

	static TypeName get(Type type, Map<Type, TypeVariableName> map) {
		if (type instanceof Class<?>) {
			Class<?> classType = (Class<?>) type;
			if (type == void.class) return VOID;
			if (type == boolean.class) return BOOLEAN;
			if (type == byte.class) return BYTE;
			if (type == short.class) return SHORT;
			if (type == int.class) return INT;
			if (type == long.class) return LONG;
			if (type == char.class) return CHAR;
			if (type == float.class) return FLOAT;
			if (type == double.class) return DOUBLE;
			if (classType.isArray()) return ArrayTypeName.of(get(classType.getComponentType(), map));
			return ClassName.get(classType);

		} else if (type instanceof ParameterizedType) {
			return ParameterizedTypeName.get((ParameterizedType) type, map);

		} else if (type instanceof WildcardType) {
			return WildcardTypeName.get((WildcardType) type, map);

		} else if (type instanceof TypeVariable<?>) {
			return TypeVariableName.get((TypeVariable<?>) type, map);

		} else if (type instanceof GenericArrayType) {
			return ArrayTypeName.get((GenericArrayType) type, map);

		} else {
			throw new IllegalArgumentException("unexpected type: " + type);
		}
	}

	/** 将类型数组转换为类型名称列表。 */
	static List<TypeName> list(Type[] types) {
		return list(types, new LinkedHashMap<>());
	}

	static List<TypeName> list(Type[] types, Map<Type, TypeVariableName> map) {
		List<TypeName> result = new ArrayList<>(types.length);
		for (Type type : types) {
			result.add(get(type, map));
		}
		return result;
	}

	/** 返回 {@code type} 的数组组件，如果 {@code type} 不是数组则返回 null。 */
	static TypeName arrayComponent(TypeName type) {
		return type instanceof ArrayTypeName
			? ((ArrayTypeName) type).componentType
			: null;
	}

	/** 返回 {@code type} 作为数组，如果 {@code type} 不是数组则返回 null。 */
	static ArrayTypeName asArray(TypeName type) {
		return type instanceof ArrayTypeName
			? ((ArrayTypeName) type)
			: null;
	}

}
