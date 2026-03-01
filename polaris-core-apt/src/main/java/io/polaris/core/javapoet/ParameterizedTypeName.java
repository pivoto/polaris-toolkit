package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.polaris.core.javapoet.Util.checkArgument;
import static io.polaris.core.javapoet.Util.checkNotNull;

public final class ParameterizedTypeName extends TypeName {
	private final ParameterizedTypeName enclosingType;
	public final ClassName rawType;
	public final List<TypeName> typeArguments;

	ParameterizedTypeName(ParameterizedTypeName enclosingType, ClassName rawType,
		List<TypeName> typeArguments) {
		this(enclosingType, rawType, typeArguments, new ArrayList<>());
	}

	private ParameterizedTypeName(ParameterizedTypeName enclosingType, ClassName rawType,
		List<TypeName> typeArguments, List<AnnotationSpec> annotations) {
		super(annotations);
		this.rawType = checkNotNull(rawType, "rawType == null").annotated(annotations);
		this.enclosingType = enclosingType;
		this.typeArguments = Util.immutableList(typeArguments);

		checkArgument(!this.typeArguments.isEmpty() || enclosingType != null,
			"no type arguments: %s", rawType);
		for (TypeName typeArgument : this.typeArguments) {
			checkArgument(!typeArgument.isPrimitive() && typeArgument != VOID,
				"invalid type parameter: %s", typeArgument);
		}
	}

	@Override
	public ParameterizedTypeName annotated(List<AnnotationSpec> annotations) {
		return new ParameterizedTypeName(
			enclosingType, rawType, typeArguments, concatAnnotations(annotations));
	}

	@Override
	public TypeName withoutAnnotations() {
		return new ParameterizedTypeName(
			enclosingType, rawType.withoutAnnotations(), typeArguments, new ArrayList<>());
	}

	@Override
	CodeWriter emit(CodeWriter out) throws IOException {
		if (enclosingType != null) {
			enclosingType.emit(out);
			out.emit(".");
			if (isAnnotated()) {
				out.emit(" ");
				emitAnnotations(out);
			}
			out.emit(rawType.simpleName());
		} else {
			rawType.emit(out);
		}
		if (!typeArguments.isEmpty()) {
			out.emitAndIndent("<");
			boolean firstParameter = true;
			for (TypeName parameter : typeArguments) {
				if (!firstParameter) out.emitAndIndent(", ");
				parameter.emit(out);
				firstParameter = false;
			}
			out.emitAndIndent(">");
		}
		return out;
	}

	/**
	 * 返回一个新的 {@link ParameterizedTypeName} 实例，指定的 {@code name} 作为嵌套类。
	 */
	public ParameterizedTypeName nestedClass(String name) {
		checkNotNull(name, "name == null");
		return new ParameterizedTypeName(this, rawType.nestedClass(name), new ArrayList<>(),
			new ArrayList<>());
	}

	/**
	 * 返回一个新的 {@link ParameterizedTypeName} 实例，指定的 {@code name} 作为嵌套类，
	 * 并带有指定的 {@code typeArguments}。
	 */
	public ParameterizedTypeName nestedClass(String name, List<TypeName> typeArguments) {
		checkNotNull(name, "name == null");
		return new ParameterizedTypeName(this, rawType.nestedClass(name), typeArguments,
			new ArrayList<>());
	}

	/** 返回一个参数化类型，将 {@code typeArguments} 应用于 {@code rawType}。 */
	public static ParameterizedTypeName get(ClassName rawType, TypeName... typeArguments) {
		return new ParameterizedTypeName(null, rawType, Arrays.asList(typeArguments));
	}

	/** 返回一个参数化类型，将 {@code typeArguments} 应用于 {@code rawType}。 */
	public static ParameterizedTypeName get(Class<?> rawType, Type... typeArguments) {
		return new ParameterizedTypeName(null, ClassName.get(rawType), list(typeArguments));
	}

	/** 返回与 {@code type} 等效的参数化类型。 */
	public static ParameterizedTypeName get(ParameterizedType type) {
		return get(type, new LinkedHashMap<>());
	}

	/** 返回与 {@code type} 等效的参数化类型。 */
	static ParameterizedTypeName get(ParameterizedType type, Map<Type, TypeVariableName> map) {
		ClassName rawType = ClassName.get((Class<?>) type.getRawType());
		ParameterizedType ownerType = (type.getOwnerType() instanceof ParameterizedType)
			&& !Modifier.isStatic(((Class<?>) type.getRawType()).getModifiers())
			? (ParameterizedType) type.getOwnerType() : null;
		List<TypeName> typeArguments = TypeName.list(type.getActualTypeArguments(), map);
		return (ownerType != null)
			? get(ownerType, map).nestedClass(rawType.simpleName(), typeArguments)
			: new ParameterizedTypeName(null, rawType, typeArguments);
	}
}
