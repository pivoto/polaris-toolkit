package io.polaris.annotation.processing;

import com.squareup.javapoet.TypeName;

import java.beans.Introspector;

/**
 * @author Qt
 * @since 1.8
 */
public class Utils {
	public static String decapitalize(String name) {
		return Introspector.decapitalize(name);
	}

	public static String capitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}

	public static String toGetterName(String name, TypeName typeName) {
		if (TypeName.BOOLEAN.equals(typeName)) {
			return "is" + capitalize(name);
		}
		return "get" + capitalize(name);
	}

	public static String toSetterName(String name) {
		return "set" + capitalize(name);
	}

}
