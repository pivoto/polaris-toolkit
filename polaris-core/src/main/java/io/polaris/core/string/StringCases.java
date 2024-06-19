package io.polaris.core.string;

import java.beans.Introspector;

/**
 * @author Qt
 * @since 1.8
 */
public class StringCases {
	/**
	 * @see java.beans.Introspector#decapitalize(String)
	 */
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

	public static String camelToKebabCase(CharSequence name) {
		return toDelimiterCase(name, '-', false);
	}

	public static String camelToKebabUpperCase(CharSequence name) {
		return toDelimiterCase(name, '-', true);
	}

	public static String camelToUnderlineCase(CharSequence name) {
		return toDelimiterCase(name, '_', false);
	}

	public static String camelToUnderlineUpperCase(CharSequence name) {
		return toDelimiterCase(name, '_', true);
	}

	public static String kebabToPascalCase(CharSequence name) {
		return toCamelCase(name, '-', true);
	}

	public static String underlineToPascalCase(CharSequence name) {
		return toCamelCase(name, '_', true);
	}

	public static String kebabToCamelCase(CharSequence name) {
		return toCamelCase(name, '-', false);
	}

	public static String underlineToCamelCase(CharSequence name) {
		return toCamelCase(name, '_', false);
	}

	public static String toPascalCase(CharSequence name, char delimiter) {
		return toCamelCase(name, delimiter, true);
	}

	public static String toCamelCase(CharSequence name, char delimiter) {
		return toCamelCase(name, delimiter, false);
	}

	public static String toDelimiterCase(CharSequence name, char delimiter) {
		return toDelimiterCase(name, delimiter, false);
	}

	public static String toDelimiterCase(CharSequence name, char delimiter, boolean upper) {
		if (name == null) {
			return null;
		}
		int len = name.length();
		StringBuilder sb = new StringBuilder(len);
		boolean isBound = true;
		if (upper) {
			for (int i = 0; i < len; i++) {
				char c = name.charAt(i);
				if (c == delimiter) {
					if (!isBound) {
						sb.append(delimiter);
						isBound = true;
					}
				} else if (Character.isUpperCase(c)) {
					if (!isBound) {
						sb.append(delimiter);
					}
					sb.append(c);
					isBound = false;
				} else {
					sb.append(Character.toUpperCase(c));
					isBound = false;
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				char c = name.charAt(i);
				if (c == delimiter) {
					if (!isBound) {
						sb.append(delimiter);
						isBound = true;
					}
				} else if (Character.isUpperCase(c)) {
					if (!isBound) {
						sb.append(delimiter);
					}
					sb.append(Character.toLowerCase(c));
					isBound = false;
				} else {
					sb.append(c);
					isBound = false;
				}
			}
		}
		return sb.toString();
	}

	public static String toCamelCase(CharSequence name, char delimiter, boolean upperFirst) {
		if (name == null) {
			return null;
		}
		int len = name.length();
		StringBuilder sb = new StringBuilder(len);
		int start = 0;
		char first = name.charAt(start);
		if (upperFirst && first != delimiter) {
			sb.append(Character.toUpperCase(first));
			start = 1;
		}
		boolean upperCase = false;
		for (int i = start; i < len; i++) {
			char c = name.charAt(i);
			if (c == delimiter) {
				upperCase = true;
			} else if (upperCase) {
				sb.append(Character.toUpperCase(c));
				upperCase = false;
			} else {
				sb.append(Character.toLowerCase(c));
			}
		}
		return sb.toString();
	}

}
