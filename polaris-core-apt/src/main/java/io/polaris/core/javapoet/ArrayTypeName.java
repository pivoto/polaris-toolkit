package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;

import static io.polaris.core.javapoet.Util.checkNotNull;

public final class ArrayTypeName extends TypeName {
	public final TypeName componentType;

	private ArrayTypeName(TypeName componentType) {
		this(componentType, new ArrayList<>());
	}

	private ArrayTypeName(TypeName componentType, List<AnnotationSpec> annotations) {
		super(annotations);
		this.componentType = checkNotNull(componentType, "rawType == null");
	}

	@Override
	public ArrayTypeName annotated(List<AnnotationSpec> annotations) {
		return new ArrayTypeName(componentType, concatAnnotations(annotations));
	}

	@Override
	public TypeName withoutAnnotations() {
		return new ArrayTypeName(componentType);
	}

	@Override
	CodeWriter emit(CodeWriter out) throws IOException {
		return emit(out, false);
	}

	CodeWriter emit(CodeWriter out, boolean varargs) throws IOException {
		emitLeafType(out);
		return emitBrackets(out, varargs);
	}

	private CodeWriter emitLeafType(CodeWriter out) throws IOException {
		if (TypeName.asArray(componentType) != null) {
			return TypeName.asArray(componentType).emitLeafType(out);
		}
		return componentType.emit(out);
	}

	private CodeWriter emitBrackets(CodeWriter out, boolean varargs) throws IOException {
		if (isAnnotated()) {
			out.emit(" ");
			emitAnnotations(out);
		}

		if (TypeName.asArray(componentType) == null) {
			// Last bracket.
			return out.emit(varargs ? "..." : "[]");
		}
		out.emit("[]");
		return TypeName.asArray(componentType).emitBrackets(out, varargs);
	}


	/** 返回元素都是 {@code componentType} 实例的数组类型。 */
	public static ArrayTypeName of(TypeName componentType) {
		return new ArrayTypeName(componentType);
	}

	/** 返回元素都是 {@code componentType} 实例的数组类型。 */
	public static ArrayTypeName of(Type componentType) {
		return of(TypeName.get(componentType));
	}

	/** 返回与 {@code mirror} 等价的数组类型。 */
	public static ArrayTypeName get(ArrayType mirror) {
		return get(mirror, new LinkedHashMap<>());
	}

	static ArrayTypeName get(ArrayType mirror, Map<TypeParameterElement, TypeVariableName> typeVariables) {
		return new ArrayTypeName(get(mirror.getComponentType(), typeVariables));
	}

	/** 返回与 {@code type} 等价的数组类型。 */
	public static ArrayTypeName get(GenericArrayType type) {
		return get(type, new LinkedHashMap<>());
	}

	static ArrayTypeName get(GenericArrayType type, Map<Type, TypeVariableName> map) {
		return ArrayTypeName.of(get(type.getGenericComponentType(), map));
	}
}
