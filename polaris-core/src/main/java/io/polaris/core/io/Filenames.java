package io.polaris.core.io;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.CharConsts;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.string.Strings;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @author Qt
 */
public class Filenames {

	/**
	 * .java文件扩展名
	 */
	public static final String EXT_JAVA = ".java";
	/**
	 * .class文件扩展名
	 */
	public static final String EXT_CLASS = ".class";
	/**
	 * .jar文件扩展名
	 */
	public static final String EXT_JAR = ".jar";

	/**
	 * 类Unix路径分隔符
	 */
	public static final char UNIX_SEPARATOR = CharConsts.SLASH;
	/**
	 * Windows路径分隔符
	 */
	public static final char WINDOWS_SEPARATOR = CharConsts.BACKSLASH;

	/**
	 * Windows下文件名中的无效字符
	 */
	private static final Pattern FILE_NAME_INVALID_PATTERN_WIN = Pattern.compile("[\\\\/:*?\"<>|\r\n]");

	/**
	 * 特殊后缀
	 */
	private static final CharSequence[] SPECIAL_SUFFIX = {"tar.bz2", "tar.Z", "tar.gz", "tar.xz"};


	public static boolean isFileSeparator(char c) {
		return CharConsts.SLASH == c || CharConsts.BACKSLASH == c;
	}


	/**
	 * 根据当前路径和相对路径计算完整路径
	 *
	 * @param currentPath  当前路径
	 * @param relativePath 相对路径
	 * @return 完整路径
	 */
	public static String relativePath(String currentPath, String relativePath) {
		int idx = currentPath.lastIndexOf('/');
		if (idx != -1) {
			String dir = currentPath.substring(0, idx);
			if (!relativePath.isEmpty() && relativePath.charAt(0) != '/') {
				dir += '/';
			}
			return dir + relativePath;
		} else {
			return relativePath;
		}
	}

	/**
	 * 规范化路径，移除多余的分隔符和处理相对路径部分（如 .. 和 .）
	 *
	 * @param path 原始路径
	 * @return 规范化后的路径
	 */
	public static String normalizePath(String path) {
		if (Strings.isBlank(path)) {
			return path;
		}
		// 如果路径中不包含点、斜杠或反斜杠，则直接返回原路径
		if (path.indexOf(CharConsts.DOT) == -1
			&& path.indexOf(CharConsts.SLASH) == -1
			&& path.indexOf(CharConsts.BACKSLASH) == -1) {
			return path;
		}

		ArrayDeque<String> queue = new ArrayDeque<>();
		boolean hasRoot = false;

		// 使用 StringTokenizer 按照斜杠和反斜杠分割路径
		StringTokenizer tokenizer = new StringTokenizer(path, "/\\", true);
		if (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			// 判断是否为根目录
			if (token.equals("/") || token.equals("\\")) {
				hasRoot = true;
			} else {
				queue.addLast(token);
			}

			// 处理后续的路径片段
			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				if (token.isEmpty()) {
					continue;
				}
				// 忽略单个分隔符或点号
				if (token.length() == 1 && (
					token.charAt(0) == CharConsts.SLASH
						|| token.charAt(0) == CharConsts.BACKSLASH
						|| token.charAt(0) == CharConsts.DOT
				)) {
					continue;
				}
				// 处理父目录引用（..）
				if (token.equals("..")) {
					queue.pollLast();
				} else {
					queue.addLast(token);
				}
			}
		}

		// 构建最终的规范化路径
		StringBuilder result = new StringBuilder();
		if (hasRoot) {
			result.append("/");
		}
		Iterator<String> iterator = queue.iterator();
		if (iterator.hasNext()) {
			result.append(iterator.next());
			while (iterator.hasNext()) {
				result.append('/').append(iterator.next());
			}
		}

		return result.toString();
	}

	/**
	 * 比较两个路径是否相等，忽略大小写并规范化路径后进行比较
	 *
	 * @param path1 第一个路径
	 * @param path2 第二个路径
	 * @return 如果两个路径规范化后相等则返回true，否则返回false
	 */
	public static boolean pathEqualsIgnoreCase(String path1, String path2) {
		if (path1 == null && path2 == null) {
			return true;
		}
		if (path1 == null || path2 == null) {
			return false;
		}
		return normalizePath(path1).equalsIgnoreCase(normalizePath(path2));
	}

	/**
	 * 比较两个路径是否相等，规范化路径后进行比较
	 *
	 * @param path1 第一个路径
	 * @param path2 第二个路径
	 * @return 如果两个路径规范化后相等则返回true，否则返回false
	 */
	public static boolean pathEquals(String path1, String path2) {
		if (path1 == null && path2 == null) {
			return true;
		}
		if (path1 == null || path2 == null) {
			return false;
		}
		return normalizePath(path1).equals(normalizePath(path2));
	}

	/**
	 * 返回文件名
	 *
	 * @param file 文件
	 * @return 文件名
	 */
	public static String getName(File file) {
		return (null != file) ? file.getName() : null;
	}

	/**
	 * 返回文件名<br>
	 * <pre>
	 * "d:/test/aaa" 返回 "aaa"
	 * "/test/aaa.jpg" 返回 "aaa.jpg"
	 * </pre>
	 *
	 * @param filePath 文件
	 * @return 文件名
	 */
	public static String getName(String filePath) {
		if (null == filePath) {
			return null;
		}
		int len = filePath.length();
		if (0 == len) {
			return filePath;
		}
		if (isFileSeparator(filePath.charAt(len - 1))) {
			// 以分隔符结尾的去掉结尾分隔符
			len--;
		}

		int begin = 0;
		char c;
		for (int i = len - 1; i > -1; i--) {
			c = filePath.charAt(i);
			if (isFileSeparator(c)) {
				// 查找最后一个路径分隔符（/或者\）
				begin = i + 1;
				break;
			}
		}

		return filePath.substring(begin, len);
	}

	/**
	 * 获取文件后缀名，扩展名不带“.”
	 *
	 * @param file 文件
	 * @return 扩展名
	 * @see #extName(File)
	 */
	public static String getSuffix(File file) {
		return extName(file);
	}

	/**
	 * 获得文件后缀名，扩展名不带“.”
	 *
	 * @param fileName 文件名
	 * @return 扩展名
	 * @see #extName(String)
	 */
	public static String getSuffix(String fileName) {
		return extName(fileName);
	}

	/**
	 * 返回主文件名
	 *
	 * @param file 文件
	 * @return 主文件名
	 * @see #mainName(File)
	 */
	public static String getPrefix(File file) {
		return mainName(file);
	}

	/**
	 * 返回主文件名
	 *
	 * @param fileName 完整文件名
	 * @return 主文件名
	 * @see #mainName(String)
	 */
	public static String getPrefix(String fileName) {
		return mainName(fileName);
	}

	/**
	 * 返回主文件名
	 *
	 * @param file 文件
	 * @return 主文件名
	 */
	public static String mainName(File file) {
		if (file.isDirectory()) {
			return file.getName();
		}
		return mainName(file.getName());
	}

	/**
	 * 返回主文件名
	 *
	 * @param fileName 完整文件名
	 * @return 主文件名
	 */
	public static String mainName(String fileName) {
		if (null == fileName) {
			return null;
		}
		int len = fileName.length();
		if (0 == len) {
			return fileName;
		}

		//issue#2642，多级扩展名的主文件名
		for (final CharSequence specialSuffix : SPECIAL_SUFFIX) {
			if (fileName.endsWith("." + specialSuffix)) {
				return fileName.substring(0, len - specialSuffix.length() - 1);
			}
		}

		if (isFileSeparator(fileName.charAt(len - 1))) {
			len--;
		}

		int begin = 0;
		int end = len;
		char c;
		for (int i = len - 1; i >= 0; i--) {
			c = fileName.charAt(i);
			if (len == end && CharConsts.DOT == c) {
				// 查找最后一个文件名和扩展名的分隔符：.
				end = i;
			}
			// 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
			if (isFileSeparator(c)) {
				begin = i + 1;
				break;
			}
		}

		return fileName.substring(begin, end);
	}

	/**
	 * 获取文件扩展名（后缀名），扩展名不带“.”
	 *
	 * @param file 文件
	 * @return 扩展名
	 */
	public static String extName(File file) {
		if (null == file) {
			return null;
		}
		if (file.isDirectory()) {
			return null;
		}
		return extName(file.getName());
	}

	/**
	 * 获得文件的扩展名（后缀名），扩展名不带“.”
	 *
	 * @param fileName 文件名
	 * @return 扩展名
	 */
	public static String extName(String fileName) {
		if (fileName == null) {
			return null;
		}
		final int index = fileName.lastIndexOf(CharConsts.DOT);
		if (index == -1) {
			return SymbolConsts.EMPTY;
		} else {
			final int secondToLastIndex = fileName.substring(0, index).lastIndexOf(SymbolConsts.DOT);
			final String substr = fileName.substring(secondToLastIndex == -1 ? index : secondToLastIndex + 1);
			if (Strings.containsAny(substr, SPECIAL_SUFFIX)) {
				return substr;
			}

			final String ext = fileName.substring(index + 1);
			// 扩展名中不能包含路径相关的符号
			return Strings.containsAny(ext, UNIX_SEPARATOR, WINDOWS_SEPARATOR) ? SymbolConsts.EMPTY : ext;
		}
	}

	/**
	 * 清除文件名中的在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
	 *
	 * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
	 * @return 清理后的文件名
	 */
	public static String cleanInvalid(String fileName) {
		return Strings.isBlank(fileName) ? fileName : FILE_NAME_INVALID_PATTERN_WIN.matcher(fileName).replaceAll("");
	}

	/**
	 * 文件名中是否包含在Windows下不支持的非法字符，包括： \ / : * ? " &lt; &gt; |
	 *
	 * @param fileName 文件名（必须不包括路径，否则路径符将被替换）
	 * @return 是否包含非法字符
	 */
	public static boolean containsInvalid(String fileName) {
		return (false == Strings.isBlank(fileName)) && FILE_NAME_INVALID_PATTERN_WIN.matcher(fileName).find();
	}

	/**
	 * 根据文件名检查文件类型，忽略大小写
	 *
	 * @param fileName 文件名，例如hutool.png
	 * @param extNames 被检查的扩展名数组，同一文件类型可能有多种扩展名，扩展名不带“.”
	 * @return 是否是指定扩展名的类型
	 */
	public static boolean isType(String fileName, String... extNames) {
		String extName = extName(fileName);
		return Iterables.isMatchAny(extNames, x -> extName.equalsIgnoreCase(x));
	}
	// -------------------------------------------------------------------------------------------- name end
}
