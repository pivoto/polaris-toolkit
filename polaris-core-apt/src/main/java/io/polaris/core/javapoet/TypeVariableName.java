package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import static io.polaris.core.javapoet.Util.checkArgument;
import static io.polaris.core.javapoet.Util.checkNotNull;

public final class TypeVariableName extends TypeName {
	public final String name;
	public final List<TypeName> bounds;

	private TypeVariableName(String name, List<TypeName> bounds) {
		this(name, bounds, new ArrayList<>());
	}

	private TypeVariableName(String name, List<TypeName> bounds, List<AnnotationSpec> annotations) {
		super(annotations);
		this.name = checkNotNull(name, "name == null");
		this.bounds = bounds;

		for (TypeName bound : this.bounds) {
			checkArgument(!bound.isPrimitive() && bound != VOID, "invalid bound: %s", bound);
		}
	}

	@Override
	public TypeVariableName annotated(List<AnnotationSpec> annotations) {
		return new TypeVariableName(name, bounds, annotations);
	}

	@Override
	public TypeName withoutAnnotations() {
		return new TypeVariableName(name, bounds);
	}

	public TypeVariableName withBounds(Type... bounds) {
		return withBounds(TypeName.list(bounds));
	}

	public TypeVariableName withBounds(TypeName... bounds) {
		return withBounds(Arrays.asList(bounds));
	}

	public TypeVariableName withBounds(List<? extends TypeName> bounds) {
		ArrayList<TypeName> newBounds = new ArrayList<>();
		newBounds.addAll(this.bounds);
		newBounds.addAll(bounds);
		return new TypeVariableName(name, newBounds, annotations);
	}

	private static TypeVariableName of(String name, List<TypeName> bounds) {
		// Strip java.lang.Object from bounds if it is present.
		List<TypeName> boundsNoObject = new ArrayList<>(bounds);
		boundsNoObject.remove(OBJECT);
		return new TypeVariableName(name, Collections.unmodifiableList(boundsNoObject));
	}

	@Override
	CodeWriter emit(CodeWriter out) throws IOException {
		emitAnnotations(out);
		return out.emitAndIndent(name);
	}

	/** 返回没有边界的名为 {@code name} 的类型变量。 */
	public static TypeVariableName get(String name) {
		return TypeVariableName.of(name, Collections.emptyList());
	}

	/** 返回带有 {@code bounds} 边界的名为 {@code name} 的类型变量。 */
	public static TypeVariableName get(String name, TypeName... bounds) {
		return TypeVariableName.of(name, Arrays.asList(bounds));
	}

	/** 返回带有 {@code bounds} 边界的名为 {@code name} 的类型变量。 */
	public static TypeVariableName get(String name, Type... bounds) {
		return TypeVariableName.of(name, TypeName.list(bounds));
	}

	/** 返回等效于 {@code mirror} 的类型变量。 */
	public static TypeVariableName get(TypeVariable mirror) {
		return get((TypeParameterElement) mirror.asElement());
	}

	/**
	 * 为给定的 TypeMirror 创建 TypeVariableName。此形式在内部使用以避免
	 * 在类似 {@code Enum<E extends Enum<E>>} 的情况下出现无限递归。当我们遇到这样的
	 * 情况时，我们将创建一个没有边界的 TypeVariableName 并将其添加到 {@code typeVariables}
	 * 映射中，然后再查找边界。然后，如果我们在构造边界时再次遇到此 TypeVariable，
	 * 我们可以直接从映射中返回它。并且，将条目放入 {@code variables} 的代码将确保
	 * 在返回之前填充边界。
	 */
	static TypeVariableName get(
		TypeVariable mirror, Map<TypeParameterElement, TypeVariableName> typeVariables) {
		TypeParameterElement element = (TypeParameterElement) mirror.asElement();
		TypeVariableName typeVariableName = typeVariables.get(element);
		if (typeVariableName == null) {
			// 由于边界字段是公共的，我们需要使其成为不可修改列表。但我们控制
			// 包装它的列表，这意味着我们可以在返回之前更改它。
			List<TypeName> bounds = new ArrayList<>();
			List<TypeName> visibleBounds = Collections.unmodifiableList(bounds);
			typeVariableName = new TypeVariableName(element.getSimpleName().toString(), visibleBounds);
			typeVariables.put(element, typeVariableName);
			for (TypeMirror typeMirror : element.getBounds()) {
				bounds.add(TypeName.get(typeMirror, typeVariables));
			}
			bounds.remove(OBJECT);
		}
		return typeVariableName;
	}

	/** 返回等效于 {@code element} 的类型变量。 */
	public static TypeVariableName get(TypeParameterElement element) {
		String name = element.getSimpleName().toString();
		List<? extends TypeMirror> boundsMirrors = element.getBounds();

		List<TypeName> boundsTypeNames = new ArrayList<>();
		for (TypeMirror typeMirror : boundsMirrors) {
			boundsTypeNames.add(TypeName.get(typeMirror));
		}

		return TypeVariableName.of(name, boundsTypeNames);
	}

	/** 返回等效于 {@code type} 的类型变量。 */
	public static TypeVariableName get(java.lang.reflect.TypeVariable<?> type) {
		return get(type, new LinkedHashMap<>());
	}

	/** @see #get(java.lang.reflect.TypeVariable, Map) */
	static TypeVariableName get(java.lang.reflect.TypeVariable<?> type,
		Map<Type, TypeVariableName> map) {
		TypeVariableName result = map.get(type);
		if (result == null) {
			List<TypeName> bounds = new ArrayList<>();
			List<TypeName> visibleBounds = Collections.unmodifiableList(bounds);
			result = new TypeVariableName(type.getName(), visibleBounds);
			map.put(type, result);
			for (Type bound : type.getBounds()) {
				bounds.add(TypeName.get(bound, map));
			}
			bounds.remove(OBJECT);
		}
		return result;
	}
}
