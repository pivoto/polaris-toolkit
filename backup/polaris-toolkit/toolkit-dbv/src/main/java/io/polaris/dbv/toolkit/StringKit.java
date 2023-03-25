package io.polaris.dbv.toolkit;

import java.beans.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串常用操作工具类
 *
 * @author Qt
 * @since 1.8
 * @version 1.0, Sep 13, 2016
 */
public class StringKit {
	private static final ThreadLocal<Map<Object, Object>> dataCacheThreadLocal = new ThreadLocal<Map<Object, Object>>();
	private static final ThreadLocal<Integer> counterThreadLocal = new ThreadLocal<Integer>();
	private static final String[] CHARS_ORIGIN = {
			"&", "<", ">", "\"", "'"
	};
	private static final String[] CHARS_ENTITY = {
			"&amp;", "&lt;", "&gt;", "&quot;", "&apos;"
	};
	private static char[] hexDigits = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	private static final Pattern P1 = Pattern.compile("\\$\\{([^${}]+)\\}");
	private static final Pattern P2 = Pattern.compile("\\$([^${]+)\\$");

	private static final String FOLDER_SEPARATOR = "/";
	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
	private static final char EXTENSION_SEPARATOR = '.';

	/** 空字符串 */
    public static final String EMPTY = "";

    /** null字符串 */
    public static final String NULL = "null";

    /** 逗号 */
    public static final String COMMA = ",";

    /** 分号分 */
    public static final String SEMICOLON = ";";

    /** 冒号 */
    public static final String COLON = ":";

    /** 小于号 */
    public static final String LT = "<";

    /** 大于号 */
    public static final String GT = ">";

    /** 点号 */
    public static final String DOT = ".";

    /** 下划线 */
    public static final String UNDERLINE = "_";

    /** 分号分隔符(不区分全半角) */
    public static final String SPLIT_SEMICOLON = ";|；";

    /** 逗号分隔符(不区分全半角) */
    public static final String SPLIT_COMMA = ",|，";

    /** 索引位置找不到 */
    public static final int INDEX_NOT_FOUND = -1;

    /** 整数特殊值 */
    public static final int NUM_FLAG = -1;

    /** 长整数特殊值 */
    public static final long LONG_NUM_FLAG = -1;

    /** 换行 */
    public static final String NEW_LINE = "\n";

	/**
	 * 将字符串中的变量替换为System.getProperties()中的值<br>
	 * 变量的书写格式：${key}或$key$
	 *
	 * @param orig
	 *            原字符串
	 * @return 替换后的字符串
	 */
	public static String bindVariable(String orig) {
		orig = StringKit.bindVariable(orig, System.getProperties());
		return orig;
	}

	/**
	 * 将字符串中的变量替换为Properties中的值<br>
	 * 变量的书写格式：${key}或$key$
	 *
	 * @param orig
	 * @param param
	 * @return
	 */
	public static String bindVariable(String orig, final Properties param) {
		orig = StringKit.bindVariable(orig, param, StringKit.P1);
		orig = StringKit.bindVariable(orig, param, StringKit.P2);
		return orig;
	}

	/**
	 * 返回参数中第一个不为null或空字符串的对象，如不存在，返回null
	 *
	 * @param args
	 * @return
	 */
	public static String coalesce(final String... args) {
		String v = null;
		if (args.length > 0) {
			for (String arg : args) {
				if (arg != null && !arg.trim().equals("")) {
					v = arg;
					break;
				}
			}
		}
		return v;
	}

	public static boolean containsWhitespace(final CharSequence str) {
		if (!StringKit.hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static String toUnicodeString(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] > 0 && chs[i] < 127 && chs[i] != '\\') {
				sb.append(chs[i]);
			} else {
				String str = Integer.toString(chs[i], 16);
				sb.append("\\u").append("0000".substring(0, 4 - str.length()))
						.append(str);
			}
		}
		return sb.toString();
	}

	public static String fromUnicodeString(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == '\\' && i + 5 < chs.length
					&& Character.toLowerCase(chs[i + 1]) == 'u') {
				try {
					short sht = (short) Integer.parseInt(new String(new char[] {
							chs[i + 2], chs[i + 3], chs[i + 4], chs[i + 5] }),
							16);
					sb.append((char) sht);
					i += 5;
				} catch (NumberFormatException e) {
					sb.append(chs[i]);
				}
			} else {
				sb.append(chs[i]);
			}
		}
		return sb.toString();
	}

	public static String decodeXml(final String fieldValue) {
		String fv = fieldValue;
		for (int i = 0; i < StringKit.CHARS_ENTITY.length; ++i) {
			fv = fv.replaceAll(StringKit.CHARS_ENTITY[i], StringKit.CHARS_ORIGIN[i]);
		}
		return fv;
	}

	public static String encodeXml(final String fieldValue) {
		String fv = fieldValue;
		for (int i = 0; i < StringKit.CHARS_ORIGIN.length; ++i) {
			fv = fv.replaceAll(StringKit.CHARS_ORIGIN[i], StringKit.CHARS_ENTITY[i]);
		}
		return fv;
	}

	public static String formatInstanceName(final Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		String name = clazz.getSimpleName();
		char ch0 = name.charAt(0);
		if (name.length() == 1) {
			sb.append(Character.toLowerCase(ch0));
		} else {
			char ch1 = name.charAt(1);
			if (Character.isUpperCase(ch1)) {
				sb.append(name);
			} else {
				sb.append(Character.toLowerCase(ch0));
				sb.append(name.substring(1));
			}
		}
		return sb.toString();
	}

	public static String formatNameAsXmlStyle(final String name) {
		char[] chs = name.toCharArray();
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				sb.append(Character.toLowerCase(chs[idx]));
				idx++;
				break;
			} else {
				idx++;
			}
		}
		while (idx < chs.length) {
			if (Character.isUpperCase(chs[idx])) {
				sb.append('-').append(Character.toLowerCase(chs[idx]));
			} else {
				sb.append(chs[idx]);
			}
			idx++;
		}
		return sb.toString();
	}

	public static String formatNameAsJavaStyle(final String name) {
		char[] chs = name.toLowerCase().toCharArray();
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				sb.append(chs[idx]);
				idx++;
				break;
			} else {
				idx++;
			}
		}
		boolean upper = false;
		while (idx < chs.length) {
			if (chs[idx] != '_' && chs[idx] != '-') {
				if (upper) {
					sb.append(Character.toUpperCase(chs[idx]));
					upper = false;
				} else {
					sb.append(chs[idx]);
				}
			} else {
				upper = true;
			}
			idx++;
		}
		return sb.toString();
	}

	public static String formatMaxLength(final String text, final int maxLength) {
		if (text.length() <= maxLength / 2) {
			return text;
		}
		char[] chs = text.toCharArray();
		StringBuilder sb = new StringBuilder();
		int lengthb = 0;
		int length = 0;
		for (char ch : chs) {
			lengthb++;
			if (ch > 255 || ch < 1) {
				lengthb++;
			}
			if (lengthb > maxLength) {
				break;
			}
			sb.append(ch);
			length++;
		}
		if (length < chs.length) {
			sb.append("...");
		}
		return sb.toString();
	}

	public static String formatMinLength(final String text, final int minLength) {
		if (text.length() >= minLength) {
			return text;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(text);
		for (int i = 0; i < minLength - text.length(); i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	public static byte[] fromHex(final String str) throws IllegalArgumentException {
//		Assert.hasText(str);
		if (str.length() % 2 != 0) {
			throw new IllegalArgumentException();
		}
		char[] chs = str.toCharArray();
		byte[] b = new byte[chs.length / 2];
		for (int i = 0; i < b.length; i++) {

			int h = Arrays.binarySearch(StringKit.hexDigits, Character.toLowerCase(chs[i * 2]));
			if (h < 0) {
				throw new IllegalArgumentException();
			}
			int l = Arrays
					.binarySearch(StringKit.hexDigits, Character.toLowerCase(chs[i * 2 + 1]));
			if (l < 0) {
				throw new IllegalArgumentException();
			}
			b[i] = (byte) ((h << 4) | l);
		}
		return b;
	}

	public static Object fromXml(final String str) {
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
		XMLDecoder d = new XMLDecoder(in);
		Object o = d.readObject();
		d.close();
		return o;
	}

	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		path = path.replace(StringKit.WINDOWS_FOLDER_SEPARATOR, StringKit.FOLDER_SEPARATOR);
		int separatorIndex = path.lastIndexOf(StringKit.FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	public static String getFilenameExtension(final String path) {
		if (path == null) {
			return null;
		}
		int extIndex = path.lastIndexOf(StringKit.EXTENSION_SEPARATOR);
		if (extIndex == -1) {
			return null;
		}
		int folderIndex = path.lastIndexOf(StringKit.FOLDER_SEPARATOR);
		if (folderIndex > extIndex) {
			return null;
		}
		return path.substring(extIndex + 1);
	}

	public static boolean hasLength(final CharSequence str) {
		return str != null && str.length() != 0;
	}

	public static boolean hasText(final CharSequence str) {
		if (!StringKit.hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmpty(final String str) {
		return "".equals(StringKit.trimToEmpty(str));
	}

	public static boolean isNotEmpty(final String str) {
		return null != StringKit.trimToNull(str);
	}

	public static String lpad(final String str, final int length) {
		return StringKit.lpad(str, length, ' ');
	}

	public static String lpad(final String str, final int length, final char pad) {
		if (str.length() >= length) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length - str.length(); i++) {
			sb.append(pad);
		}
		sb.append(str);
		return sb.toString();
	}

	public static String rpad(final String str, final int length) {
		return StringKit.rpad(str, length, ' ');
	}

	public static String rpad(final String str, final int length, final char pad) {
		if (str.length() >= length) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		for (int i = 0; i < length - str.length(); i++) {
			sb.append(pad);
		}
		return sb.toString();
	}

	public static String toHex(final byte l) {
		return StringKit.toHex(l, 2);
	}

	public static String toHex(final byte[] b) {
		char str[] = new char[b.length * 2];
		int k = 0;
		for (int i = 0; i < b.length; i++) {
			byte byte0 = b[i];
			str[k++] = StringKit.hexDigits[byte0 >>> 4 & 0xf];
			str[k++] = StringKit.hexDigits[byte0 & 0xf];
		}
		return new String(str);
	}

	public static String toHex(final int l) {
		return StringKit.toHex(l, 8);
	}

	public static String toHex(final long l) {
		return StringKit.toHex(l, 16);
	}

	public static String toHex(final long l, final int length) {
		char[] ch = new char[length];
		int chPos = ch.length - 1;
		long tmp = l;
		while (chPos >= 0) {
			ch[chPos--] = StringKit.hexDigits[(int) (tmp & 0xF)];
			tmp >>>= 4L;
		}
		return new String(ch, 0, length);
	}

	public static String toHex(final short l) {
		return StringKit.toHex(l, 4);
	}

	public static String toString(final Object obj) {
		String str = "null";
		if (obj != null) {
			Class<?> clazz = obj.getClass();
			if (clazz.isArray()) {
				int length = Array.getLength(obj);
				if (length == 0) {
					str = "[]";
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("[").append(StringKit.toString(Array.get(obj, 0)));
					for (int i = 1; i < length; i++) {
						sb.append(", ").append(StringKit.toString(Array.get(obj, i)));
					}
					sb.append("]");
					str = sb.toString();
				}
			} else {
				return obj.toString();
			}
		}
		return str;
	}

	public static String toXml(final Object o) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder e = new XMLEncoder(out);
		e.writeObject(o);
		e.flush();
		e.close();
		return new String(out.toByteArray());
	}

	@SuppressWarnings("unchecked")
	public static Object trim(final Object o) {
		if (o == null) {
			return null;
		}
		if (o.getClass() == String.class) {
			return StringKit.trim((String) o);
		}

		Map<Object, Object> map = StringKit.dataCacheThreadLocal.get();
		if (map == null) {
			map = new ConcurrentHashMap<Object, Object>();
			StringKit.dataCacheThreadLocal.set(map);
		} else if (map.containsKey(o)) {
			return o;
		}
		Integer counter = counterThreadLocal.get();
		if (counter == null) {
			counterThreadLocal.set(Integer.valueOf(0));
		} else {
			counterThreadLocal.set(counter + 1);
		}
		map.put(o, o);
		try {
			if (o instanceof Collection) {
				Collection<Object> collections = (Collection<Object>) o;
				Object[] array = collections.toArray();
				collections.clear();
				for (int i = 0; i < array.length; i++) {
					collections.add(StringKit.trim(array[i]));
				}
			} else if (o.getClass().isArray()) {
				int length = Array.getLength(o);
				for (int i = 0; i < length; i++) {
					Object e = Array.get(o, i);
					Array.set(o, i, StringKit.trim(e));
				}
			} else {
				BeanInfo info = Introspector.getBeanInfo(o.getClass(), Object.class);
				PropertyDescriptor[] pds = info.getPropertyDescriptors();
				for (PropertyDescriptor pd : pds) {
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod != null && readMethod != null) {
						Object v = readMethod.invoke(o);
						if (v != null) {
							if (pd.getPropertyType() == String.class) {
								writeMethod.invoke(o, StringKit.trim((String) v));
							} else {
								writeMethod.invoke(o, StringKit.trim(v));
							}
						}
					}
				}
			}
		} catch (Exception e) {
		} finally {
			if (counter == null) {
				dataCacheThreadLocal.remove();
				counterThreadLocal.remove();
			}
		}
		return o;
	}

	public static String trim(final String str) {
		if (str == null) {
			return null;
		}
		return str.trim();
	}

	public static String trimLeft(final String str) {
		if (!StringKit.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimLeftCharacter(final String str, final char leadingCharacter) {
		if (!StringKit.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimRight(final String str) {
		if (!StringKit.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String trimRightCharacter(final String str, final char leadingCharacter) {
		if (!StringKit.hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && sb.charAt(0) == leadingCharacter) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	public static String trimToEmpty(final String o) {
		if (o == null) {
			return "";
		}
		if (o.getClass() == String.class) {
			return o.trim();
		} else {
			return o.toString().trim();
		}
	}

	public static String trimToNull(final String o) {
		if (o == null) {
			return null;
		}
		String str;
		if (o.getClass() == String.class) {
			str = o.trim();
		} else {
			str = o.toString().trim();
		}
		return "".equals(str) ? null : str;
	}

	public static String uuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

	private static String bindVariable(final String orig, final Properties param,
			final Pattern pattern) {
		StringBuilder sb = new StringBuilder();
		Matcher m = pattern.matcher(orig);
		int start = 0;
		int end = 0;
		int idx = 0;
		while (m.find(idx)) {
			String attr = m.group(1);
			String val = param.getProperty(attr);
			if (val == null) {
				val = "";
			}
			end = m.start();
			sb.append(orig.substring(start, end)).append(val);

			start = m.end();
			idx = m.end();
		}
		sb.append(orig.substring(idx));
		String dest = sb.toString();
		return dest;
	}

	 /**
     * 将字符串按逗号分隔，转成字符串集合<br>
     *
     *
     *
     * @param str
     * @return 当字符串为空时，返回空集合
     */
    public static List<String> toStrList(String str) {
        return toStrList(str, COMMA);
    }

    /**
     * 将字符串按分隔符分隔，转成字符串集合
     *
     * @param str
     * @param split
     * @return 当字符串为空时，返回空集合
     */
    public static List<String> toStrList(String str, String split) {
        if (isEmpty(str)) {
            return new ArrayList<String>();
        }
        return new ArrayList<String>(Arrays.asList(str.split(split)));
    }

    /**
     * 将字符串按分隔符分隔，转成字符串trim后的集合
     *
     * @param str
     * @param split
     * @return
     */
    public static List<String> toTrimList(String str, String split) {
        return toTrimList(str, split, false);
    }

    /**
     * 将字符串按分隔符分隔，转成字符串trim后的集合，支持去除空白字符
     *
     * @param str
     * @param split
     * @param excludeBlank 是否除排空白字符
     * @return
     */
    public static List<String> toTrimList(String str, String split, boolean excludeBlank) {
        if (isEmpty(str)) {
            return new ArrayList<String>();
        }
        List<String> strList = new LinkedList<String>();
        String[] strArr = str.split(split);
        for (int i = 0; i < strArr.length; i++) {
            String tempStr = strArr[i];
            if (excludeBlank) {
                if (isEmpty(tempStr)) {
                    continue;
                }
            }
            strList.add(strArr[i].trim());
        }
        return strList;
    }

    /**
     * 将字符串按逗号分隔，转成Long集合
     *
     * @param str
     * @return 当字符串为空时，返回空集合
     */
    public static List<Long> toLongList(String str) {
        return toLongList(str, COMMA);
    }

    /**
     * 将字符串按逗号分隔，转成整数集合
     *
     * @param str
     * @return 当字符串为空时，返回空集合
     */
    public static List<Integer> toIntList(String str) {
        return toIntList(str, COMMA);
    }

    /**
     * 将字符串按逗号分隔，转成整数集合（注意：不能转换为整数的会被过滤掉）
     *
     * @param str
     * @param split
     * @return
     */
    public static List<Long> toLongList(String str, String split) {
        if (isEmpty(str)) {
            return new ArrayList<Long>();
        }
        List<Long> longList = new LinkedList<Long>();
        String[] strArr = str.split(split);
        for (int i = 0; i < strArr.length; i++) {
        	Long num = getLong(strArr[i]);
            if (num != null) {
            	longList.add(num);
            }
        }
        return longList;
    }

    /**
     * 将字符串按逗号分隔，转成整数集合（注意：不能转换为整数的会被过滤掉）
     *
     * @param str
     * @param split
     * @return
     */
    public static List<Integer> toIntList(String str, String split) {
        if (isEmpty(str)) {
            return new ArrayList<Integer>();
        }
        List<Integer> intList = new LinkedList<Integer>();
        String[] strArr = str.split(split);
        for (int i = 0; i < strArr.length; i++) {
            Integer num = getInteger(strArr[i]);
            if (num != null) {
                intList.add(num);
            }
        }
        return intList;
    }

    /**
     * 将字符串转换为整形形式，无法转换则返回null
     *
     * @param str
     * @return
     */
    public static Integer getInteger(String str) {
    	if (isEmpty(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 将字符串转换为Long形式，无法转换则返回null
     *
     * @param str
     * @return
     */
    public static Long getLong(String str) {
    	if (isEmpty(str)) {
            return null;
        }
        try {
            return Long.parseLong(str);
        } catch (Exception ex) {
            return null;
        }
    }


}
