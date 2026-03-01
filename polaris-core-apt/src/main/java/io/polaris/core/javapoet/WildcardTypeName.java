package io.polaris.core.javapoet;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

import static io.polaris.core.javapoet.Util.checkArgument;

public final class WildcardTypeName extends TypeName {
	public final List<TypeName> upperBounds;
	public final List<TypeName> lowerBounds;

	private WildcardTypeName(List<TypeName> upperBounds, List<TypeName> lowerBounds) {
		this(upperBounds, lowerBounds, new ArrayList<>());
	}

	private WildcardTypeName(List<TypeName> upperBounds, List<TypeName> lowerBounds,
		List<AnnotationSpec> annotations) {
		super(annotations);
		this.upperBounds = Util.immutableList(upperBounds);
		this.lowerBounds = Util.immutableList(lowerBounds);

		checkArgument(this.upperBounds.size() == 1, "unexpected extends bounds: %s", upperBounds);
		for (TypeName upperBound : this.upperBounds) {
			checkArgument(!upperBound.isPrimitive() && upperBound != VOID,
				"invalid upper bound: %s", upperBound);
		}
		for (TypeName lowerBound : this.lowerBounds) {
			checkArgument(!lowerBound.isPrimitive() && lowerBound != VOID,
				"invalid lower bound: %s", lowerBound);
		}
	}

	@Override
	public WildcardTypeName annotated(List<AnnotationSpec> annotations) {
		return new WildcardTypeName(upperBounds, lowerBounds, concatAnnotations(annotations));
	}

	@Override
	public TypeName withoutAnnotations() {
		return new WildcardTypeName(upperBounds, lowerBounds);
	}

	@Override
	CodeWriter emit(CodeWriter out) throws IOException {
		if (lowerBounds.size() == 1) {
			return out.emit("? super $T", lowerBounds.get(0));
		}
		return upperBounds.get(0).equals(TypeName.OBJECT)
			? out.emit("?")
			: out.emit("? extends $T", upperBounds.get(0));
	}
	/**
	 * 返回表示扩展 {@code bound} 的未知类型的类型。例如，如果
	 * {@code bound} 是 {@code CharSequence.class}，则返回 {@code ? extends CharSequence}。如果
	 * {@code bound} 是 {@code Object.class}，则返回 {@code ?}，这是 {@code
	 * ? extends Object} 的简写形式。
	 */
	public static WildcardTypeName subtypeOf(TypeName upperBound) {
		return new WildcardTypeName(Collections.singletonList(upperBound), Collections.emptyList());
	}

	public static WildcardTypeName subtypeOf(Type upperBound) {
		return subtypeOf(TypeName.get(upperBound));
	}

	/**
	 * 返回表示 {@code bound} 的未知超类型的类型。例如，如果 {@code
	 * bound} 是 {@code String.class}，则返回 {@code ? super String}。
	 */
	public static WildcardTypeName supertypeOf(TypeName lowerBound) {
		return new WildcardTypeName(Collections.singletonList(OBJECT),
			Collections.singletonList(lowerBound));
	}

	public static WildcardTypeName supertypeOf(Type lowerBound) {
		return supertypeOf(TypeName.get(lowerBound));
	}

	public static TypeName get(javax.lang.model.type.WildcardType mirror) {
		return get(mirror, new LinkedHashMap<>());
	}

	static TypeName get(
		javax.lang.model.type.WildcardType mirror,
		Map<TypeParameterElement, TypeVariableName> typeVariables) {
		TypeMirror extendsBound = mirror.getExtendsBound();
		if (extendsBound == null) {
			TypeMirror superBound = mirror.getSuperBound();
			if (superBound == null) {
				return subtypeOf(Object.class);
			} else {
				return supertypeOf(TypeName.get(superBound, typeVariables));
			}
		} else {
			return subtypeOf(TypeName.get(extendsBound, typeVariables));
		}
	}

	public static TypeName get(WildcardType wildcardName) {
		return get(wildcardName, new LinkedHashMap<>());
	}

	static TypeName get(WildcardType wildcardName, Map<Type, TypeVariableName> map) {
		return new WildcardTypeName(
			list(wildcardName.getUpperBounds(), map),
			list(wildcardName.getLowerBounds(), map));
	}
}
