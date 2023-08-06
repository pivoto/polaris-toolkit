package io.polaris.core.asm;

import io.polaris.core.lang.Types;
import com.squareup.javapoet.*;

import java.lang.reflect.*;
import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Aug 05, 2023
 */
public class AsmTypeSignatures {


	public static String toAsmTypeSignature(Type type) {
		if (type instanceof Class) {
			if (((Class<?>) type).isPrimitive()) {
				return org.objectweb.asm.Type.getDescriptor(Types.getWrapperClass((Class<?>) type));
			} else {
				return org.objectweb.asm.Type.getDescriptor((Class<?>) type);
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			StringBuilder sb = new StringBuilder();
			sb.append("L").append(parameterizedType.getRawType().getTypeName().replace('.', '/'));
			sb.append("<");
			Type[] args = parameterizedType.getActualTypeArguments();
			for (int i = 0; i < args.length; i++) {
				sb.append(toAsmTypeSignature(args[i]));
			}
			sb.append(">;");
			return sb.toString();
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] lowerBounds = wildcardType.getLowerBounds();
			if (lowerBounds.length > 0) {
				return "-" + toAsmTypeSignature(lowerBounds[0]);
			} else {
				Type[] upperBounds = wildcardType.getUpperBounds();
				if (upperBounds.length > 0) {
					return "+" + toAsmTypeSignature(upperBounds[0]);
				} else {
					return toAsmTypeSignature(Object.class);
				}
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			return "[" + toAsmTypeSignature(componentType);
		} else if (type instanceof TypeVariable) {
			throw new IllegalArgumentException("Unsupported type: " + type);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
	}

	public static String toAsmTypeSignature(TypeName typeName) {
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
				sb.append(toAsmTypeSignature(typeArgument));
			}
			sb.append(">;");
			return sb.toString();
		} else if (typeName instanceof WildcardTypeName) {
			List<TypeName> lowerBounds = ((WildcardTypeName) typeName).lowerBounds;
			if (lowerBounds != null && lowerBounds.size() > 0) {
				return "-" + toAsmTypeSignature(lowerBounds.get(0));
			}
			List<TypeName> upperBounds = ((WildcardTypeName) typeName).upperBounds;
			if (upperBounds != null && upperBounds.size() > 0) {
				return "+" + toAsmTypeSignature(upperBounds.get(0));
			}
			return org.objectweb.asm.Type.getDescriptor(Object.class);
		} else if (typeName instanceof ArrayTypeName) {
			TypeName componentType = ((ArrayTypeName) typeName).componentType;
			return "[" + toAsmTypeSignature(componentType);
		} else if (typeName instanceof TypeVariableName) {
			throw new IllegalArgumentException("Unsupported typeName: " + typeName);
		} else {
			throw new IllegalArgumentException("Unsupported typeName: " + typeName);
		}
	}
}
