package io.polaris.core.msg;

import java.text.ChoiceFormat;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.polaris.core.converter.Converters;
import io.polaris.core.lang.Objs;
import io.polaris.core.string.Strings;
import io.polaris.core.time.Dates;

/**
 * 消息格式化工具类
 * <p>
 * 该类提供了强大的消息格式化功能，支持参数替换、数字格式化、日期格式化等。
 * 格式化模式语法类似于java.text.MessageFormat，但提供了更多灵活性。
 * 支持以下格式：
 * <ul>
 *   <li>简单参数替换: {0}, {1}, {-1} (负数表示从末尾开始计数)</li>
 *   <li>数字格式化: {0,number}, {1,number,currency}, {2,number,percent}</li>
 *   <li>日期格式化: {0,date}, {1,time,short}, {2,date,yyyy-MM-dd}</li>
 *   <li>嵌套格式化: {0,number,{1}}, 其中第二个参数决定第一个参数的格式</li>
 *   <li>默认值: {0:-默认值}, 当参数为空时显示默认值</li>
 * </ul>
 * </p>
 *
 * @author Qt
 * @since Jul 24, 2025
 */
public class MessageFormat {
	private static final int SEG_RAW = 0;
	private static final int SEG_INDEX = 1;
	private static final int SEG_TYPE = 2;
	private static final int SEG_MODIFIER = 3; // modifier or subformat
	private static final int SEG_DEFAULT = 4;

	// Indices for type keywords
	private static final int TYPE_NULL = 0;
	private static final int TYPE_NUMBER = 1;
	private static final int TYPE_DATE = 2;
	private static final int TYPE_TIME = 3;
	private static final int TYPE_CHOICE = 4;
	private static final String[] TYPE_KEYWORDS = {"", "number", "date", "time", "choice"};

	// Indices for number modifiers
	private static final int MODIFIER_DEFAULT = 0; // common in number and date-time
	private static final int MODIFIER_CURRENCY = 1;
	private static final int MODIFIER_PERCENT = 2;
	private static final int MODIFIER_INTEGER = 3;
	private static final String[] NUMBER_MODIFIER_KEYWORDS = {"", "currency", "percent", "integer"};

	// Indices for date-time modifiers
	private static final String[] DATE_TIME_MODIFIER_KEYWORDS = {"", "short", "medium", "long", "full"};

	// Date-time style values corresponding to the date-time modifiers.
	private static final int[] DATE_TIME_MODIFIERS = {DateFormat.DEFAULT, DateFormat.SHORT, DateFormat.MEDIUM, DateFormat.LONG, DateFormat.FULL,};

	private final Locale locale;
	private final Segment segment;

	/**
	 * 使用指定的模式创建消息格式化器
	 *
	 * @param pattern 格式化模式字符串，不能为空
	 */
	public MessageFormat(String pattern) {
		this(pattern, Locale.getDefault(Locale.Category.FORMAT));
	}

	/**
	 * 使用指定的模式和区域设置创建消息格式化器
	 *
	 * @param pattern 格式化模式字符串，不能为空
	 * @param locale  区域设置，用于数字、日期等本地化格式化
	 */
	public MessageFormat(String pattern, Locale locale) {
		this.locale = locale;
		this.segment = parse(locale, pattern);
	}

	@Override
	public String toString() {
		return "MessageFormat{" +
			"locale=" + locale +
			", segment=" + segment +
			'}';
	}

	public Locale getLocale() {
		return locale;
	}

	/**
	 * 格式化消息，使用提供的参数替换模式中的占位符
	 *
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public String format(Object... arguments) {
		return segment.format(new Arguments(arguments));
	}

	/**
	 * 格式化消息，针对空占位符指定特定的默认消息
	 *
	 * @param emptyMsg  空占位符对应的默认消息
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public String formatWithEmpty(String emptyMsg, Object... arguments) {
		return segment.format(new Arguments(emptyMsg, arguments));
	}

	/**
	 * 创建一个新的消息格式化器实例
	 *
	 * @param pattern 格式化模式字符串
	 * @return 新的消息格式化器实例
	 */
	public static MessageFormat newInstance(String pattern) {
		return new MessageFormat(pattern);
	}

	/**
	 * 使用指定区域设置创建一个新的消息格式化器实例
	 *
	 * @param locale  区域设置
	 * @param pattern 格式化模式字符串
	 * @return 新的消息格式化器实例
	 */
	public static MessageFormat newInstance(Locale locale, String pattern) {
		return new MessageFormat(pattern, locale);
	}

	/**
	 * 使用指定模式和参数直接格式化消息
	 *
	 * @param pattern   格式化模式字符串
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public static String format(String pattern, Object... arguments) {
		return new MessageFormat(pattern).format(arguments);
	}

	/**
	 * 使用指定区域设置、模式和参数直接格式化消息
	 *
	 * @param locale    区域设置
	 * @param pattern   格式化模式字符串
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public static String format(Locale locale, String pattern, Object... arguments) {
		return new MessageFormat(pattern, locale).format(arguments);
	}

	/**
	 * 使用指定模式、空占位符默认消息和参数直接格式化消息
	 *
	 * @param pattern   格式化模式字符串
	 * @param emptyMsg  空占位符使用的默认消息
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public static String formatWithEmpty(String pattern, String emptyMsg, Object... arguments) {
		return new MessageFormat(pattern).formatWithEmpty(emptyMsg, arguments);
	}

	/**
	 * 使用指定区域设置、模式、空占位符默认消息和参数直接格式化消息
	 *
	 * @param locale    区域设置
	 * @param pattern   格式化模式字符串
	 * @param emptyMsg  空占位符使用的默认消息
	 * @param arguments 用于替换占位符的参数数组
	 * @return 格式化后的消息字符串
	 */
	public static String formatWithEmpty(Locale locale, String pattern, String emptyMsg, Object... arguments) {
		return new MessageFormat(pattern, locale).formatWithEmpty(emptyMsg, arguments);
	}


	/**
	 * 在关键字列表中查找指定字符串的索引位置
	 * 查找过程首先进行精确匹配，如果没有匹配则尝试去除空格并转为小写后再次匹配
	 *
	 * @param s    待查找的字符串
	 * @param list 关键字列表
	 * @return 如果找到匹配的关键字，返回其在列表中的索引；否则返回-1
	 */
	private static int findKeyword(String s, String[] list) {
		for (int i = 0; i < list.length; ++i) {
			if (s.equals(list[i])) return i;
		}

		// Try trimmed lowercase.
		String ls = s.trim().toLowerCase(Locale.ROOT);
		if (!ls.equals(s)) {
			for (int i = 0; i < list.length; ++i) {
				if (ls.equals(list[i])) return i;
			}
		}
		return -1;
	}


	/**
	 * 根据类型和修饰符字符串创建相应的格式化器
	 *
	 * @param locale      区域设置
	 * @param typeStr     类型字符串(number,date,time,choice等)
	 * @param modifierStr 修饰符字符串(currency,percent,short等)
	 * @return 相应的格式化器实例，如果无法创建则返回null
	 */
	private static Format makeFormat(Locale locale, String typeStr, String modifierStr) {
		Format newFormat = null;
		if (Strings.isNotEmpty(typeStr)) {
			int type = findKeyword(typeStr, TYPE_KEYWORDS);
			switch (type) {
				case TYPE_NULL:
					// Type "" is allowed. e.g., "{0,}", "{0,,}", and "{0,,#}"
					// are treated as "{0}".
					break;
				case TYPE_NUMBER:
					if (Strings.isNotEmpty(modifierStr)) {
						int mod = findKeyword(modifierStr, NUMBER_MODIFIER_KEYWORDS);
						switch (mod) {
							case MODIFIER_DEFAULT:
								newFormat = NumberFormat.getInstance(locale);
								break;
							case MODIFIER_CURRENCY:
								newFormat = NumberFormat.getCurrencyInstance(locale);
								break;
							case MODIFIER_PERCENT:
								newFormat = NumberFormat.getPercentInstance(locale);
								break;
							case MODIFIER_INTEGER:
								newFormat = NumberFormat.getIntegerInstance(locale);
								break;
							default: // DecimalFormat pattern
								try {
									newFormat = new DecimalFormat(modifierStr, DecimalFormatSymbols.getInstance(locale));
								} catch (Exception ignored) {
								}
								break;
						}
					} else {
						newFormat = NumberFormat.getInstance(locale);
					}
					break;

				case TYPE_DATE:
				case TYPE_TIME:
					if (Strings.isNotEmpty(modifierStr)) {
						int mod = findKeyword(modifierStr, DATE_TIME_MODIFIER_KEYWORDS);
						if (mod >= 0 && mod < DATE_TIME_MODIFIER_KEYWORDS.length) {
							if (type == TYPE_DATE) {
								newFormat = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[mod], locale);
							} else {
								newFormat = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[mod], locale);
							}
						} else {
							// SimpleDateFormat pattern
							try {
								newFormat = new SimpleDateFormat(modifierStr, locale);
							} catch (Exception ignored) {
							}
						}
					}
					break;
				case TYPE_CHOICE:
					if (Strings.isNotEmpty(modifierStr)) {
						try {
							// ChoiceFormat pattern
							newFormat = new ChoiceFormat(modifierStr);
						} catch (Exception ignored) {
						}
					}
					break;
				default:
			}
		}
		return newFormat;
	}

	private static SimpleFormat makeSimpleFormat(Locale locale, int position, StringBuilder index, StringBuilder type, StringBuilder modifier, StringBuilder defaults) {
		SimpleFormat format = new SimpleFormat();
		format.locale = locale;
		format.position = position;
		format.index = Objs.defaultIfNull(index, StringBuilder::toString, "");
		if (type != null) {
			format.format = makeFormat(locale, type.toString(), Objs.defaultIfNull(modifier, StringBuilder::toString, ""));
		}
		if (defaults != null) {
			format.defaults = defaults.toString();
		}
		return format;
	}

	private static Segment makeSegment(StringBuilder raw, SegmentFormat[] formats) {
		Segment segment = new Segment();
		segment.raw = Objs.defaultIfNull(raw, StringBuilder::toString, "");
		segment.formats = formats;
		return segment;
	}

	private static NestedFormat makeNestedFormat(Locale locale, int position, StringBuilder[] segments, SegmentFormat[][] formats) {
		NestedFormat format = new NestedFormat();
		format.locale = locale;
		format.position = position;
		if (segments[SEG_INDEX] != null || formats[SEG_INDEX] != null) {
			format.index = makeSegment(segments[SEG_INDEX], formats[SEG_INDEX]);
		}
		if (segments[SEG_TYPE] != null || formats[SEG_TYPE] != null) {
			format.type = makeSegment(segments[SEG_TYPE], formats[SEG_TYPE]);
		}
		if (segments[SEG_MODIFIER] != null || formats[SEG_MODIFIER] != null) {
			format.modifier = makeSegment(segments[SEG_MODIFIER], formats[SEG_MODIFIER]);
		}
		if (segments[SEG_DEFAULT] != null || formats[SEG_DEFAULT] != null) {
			format.defaults = makeSegment(segments[SEG_DEFAULT], formats[SEG_DEFAULT]);
		}
		return format;
	}


	private static void addSegmentChar(StringBuilder[] segments, int part, char c) {
		if (segments[part] == null) {
			segments[part] = new StringBuilder();
		}
		segments[part].append(c);
	}

	private static void addSegmentFormat(SegmentFormat[][] formats, int part, SegmentFormat format) {
		if (formats[part] == null) {
			formats[part] = new SegmentFormat[1];
			formats[part][0] = format;
		} else {
			SegmentFormat[] newFormats = new SegmentFormat[formats[part].length + 1];
			System.arraycopy(formats[part], 0, newFormats, 0, formats[part].length);
			newFormats[formats[part].length] = format;
			formats[part] = newFormats;
		}
	}

	/**
	 * 解析格式化模式字符串，生成对应的段落结构
	 *
	 * @param locale  区域设置
	 * @param pattern 格式化模式字符串
	 * @return 解析后的段落结构
	 * @throws IllegalArgumentException 当模式中的大括号不匹配时抛出
	 */
	private static Segment parse(Locale locale, String pattern) {
		StringBuilder[] segments = new StringBuilder[5];
		segments[SEG_RAW] = new StringBuilder();
		SegmentFormat[][] formats = new SegmentFormat[5][];
		int part = 0;

		Deque<ParsedPart> stack = new ArrayDeque<>();

		boolean inQuote = false;
		int braceStack = 0;

		for (int i = 0; i < pattern.length(); ++i) {
			char ch = pattern.charAt(i);

			if (part == SEG_RAW) {
				if (ch == '\'') {
					// 双重单引号
					if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '\'') {
						i++;
						addSegmentChar(segments, part, ch);
					} else {
						inQuote = !inQuote;
					}
				} else if (ch == '{' && !inQuote) {
					// 左括号匹配
					part = SEG_INDEX;
					braceStack++;
				} else {
					addSegmentChar(segments, part, ch);
				}
			} else {
				if (inQuote) {
					addSegmentChar(segments, part, ch);
					if (ch == '\'') {
						inQuote = false;
					}
				} else {
					switch (ch) {
						case ',':
							if (part < SEG_MODIFIER) {
								part++;
							} else {
								addSegmentChar(segments, part, ch);
							}
							break;
						case ':':
							if (part == SEG_DEFAULT) {
								addSegmentChar(segments, part, ch);
							} else if (i + 1 < pattern.length() && pattern.charAt(i + 1) == '-') {
								i++;
								part = SEG_DEFAULT;
							} else {
								addSegmentChar(segments, part, ch);
							}
							break;
						case '{':
							braceStack++;
							if (braceStack > 1) {
								stack.offerLast(new ParsedPart(segments, formats, part));
								segments = new StringBuilder[5];
								formats = new SegmentFormat[5][];
								part = SEG_INDEX;
							}
							break;
						case '}':
							if (braceStack > 0) {
								braceStack--;
								if (braceStack == 0) {
									part = SEG_RAW;
									if (formats[SEG_INDEX] != null || formats[SEG_TYPE] != null || formats[SEG_MODIFIER] != null || formats[SEG_DEFAULT] != null) {
										NestedFormat nestedFormat = makeNestedFormat(locale, segments[part].length(), segments, formats);
										addSegmentFormat(formats, part, nestedFormat);
									} else {
										SimpleFormat simpleFormat = makeSimpleFormat(locale, segments[part].length(), segments[SEG_INDEX], segments[SEG_TYPE], segments[SEG_MODIFIER], segments[SEG_DEFAULT]);
										addSegmentFormat(formats, part, simpleFormat);
									}
									segments[SEG_INDEX] = null;
									segments[SEG_TYPE] = null;
									segments[SEG_MODIFIER] = null;
									segments[SEG_DEFAULT] = null;
									formats[SEG_INDEX] = null;
									formats[SEG_TYPE] = null;
									formats[SEG_MODIFIER] = null;
									formats[SEG_DEFAULT] = null;
								} else {
									ParsedPart parsedPart = stack.pollLast();
									assert parsedPart != null;
									StringBuilder[] segments0 = segments;
									SegmentFormat[][] formats0 = formats;
									int part0 = part;
									segments = parsedPart.segments;
									formats = parsedPart.formats;
									part = parsedPart.part;

									if (segments[part] == null) {
										segments[part] = new StringBuilder();
									}
									if (formats0[SEG_INDEX] != null || formats0[SEG_TYPE] != null || formats0[SEG_MODIFIER] != null || formats0[SEG_DEFAULT] != null) {
										NestedFormat nestedFormat = makeNestedFormat(locale, segments[part].length(), segments0, formats0);
										addSegmentFormat(formats, part, nestedFormat);
									} else {
										SimpleFormat simpleFormat = makeSimpleFormat(locale, segments[part].length(), segments0[SEG_INDEX], segments0[SEG_TYPE], segments0[SEG_MODIFIER], segments0[SEG_DEFAULT]);
										addSegmentFormat(formats, part, simpleFormat);
									}

								}
							} else {
								addSegmentChar(segments, part, ch);
							}
							break;
						case ' ':
							// Skip any leading space chars for SEG_TYPE.
							if (part != SEG_TYPE || segments[SEG_TYPE] != null && segments[SEG_TYPE].length() > 0) {
								addSegmentChar(segments, part, ch);
							}
							break;
						case '\'':
							inQuote = true;
							// fall through, so we keep quotes in other parts
						default:
							addSegmentChar(segments, part, ch);
							break;
					}
				}
			}
		}

		if (braceStack > 0) {
			throw new IllegalArgumentException("Mismatched braces");
		}
		if (part != SEG_RAW) {
			throw new IllegalArgumentException("Mismatched braces");
		}

		return makeSegment(segments[SEG_RAW], formats[SEG_RAW]);
	}


	private static class ParsedPart {
		final StringBuilder[] segments;
		final SegmentFormat[][] formats;
		final int part;

		public ParsedPart(StringBuilder[] segments, SegmentFormat[][] formats, int part) {
			this.segments = segments;
			this.formats = formats;
			this.part = part;
		}
	}

	/**
	 * 参数容器类，用于管理和获取格式化参数
	 */
	private static class Arguments {
		private final Object[] args;
		private final BitSet bitSet;
		private final String emptyMsg;
		private final AtomicBoolean readEmptyMsg = new AtomicBoolean(false);

		public Arguments(Object[] args) {
			this(null, args);
		}

		public Arguments(String emptyMsg, Object[] args) {
			this.args = args;
			this.emptyMsg = emptyMsg;
			this.bitSet = new BitSet();
		}

		public Object get(String index) {
			boolean empty = Strings.isEmpty(index);
			if (args == null || args.length == 0) {
				return empty ? emptyMsg : null;
			}
			if (!empty) {
				try {
					int i = Integer.parseInt(index);
					if (i < 0) {
						i = args.length + i;
					}
					if (i >= 0 && i < args.length) {
						bitSet.set(i);
						return args[i];
					}
				} catch (NumberFormatException ignored) {
				}
			}
			for (int i = 0; i < args.length; i++) {
				Object param = args[i];
				if (param instanceof Map) {
					//noinspection rawtypes
					Object v = ((Map) param).get(index);
					if (v != null) {
						bitSet.set(i);
						return v;
					}
				}
			}
			if (empty) {
				if (emptyMsg != null) {
					if (readEmptyMsg.compareAndSet(false, true)) {
						return emptyMsg;
					}
				}
				for (int i = 0; i < args.length; i++) {
					if (bitSet.get(i)) {
						continue;
					}
					return args[i];
				}
				return emptyMsg;
			}
			return null;
		}

		public boolean isRead(int index) {
			return bitSet.get(index);
		}
	}

	/**
	 * 格式化器接口，定义了格式化消息的基本方法
	 * 所有格式化相关的类都需要实现此接口
	 */
	private interface Formatter {
		/**
		 * 使用提供的参数格式化消息
		 *
		 * @param arguments 包含格式化参数的对象
		 * @return 格式化后的字符串结果
		 */
		String format(Arguments arguments);
	}

	/**
	 * 消息段落类，表示格式化消息的一个片段
	 * 包含原始文本和嵌套的格式化器数组
	 */
	private static class Segment implements Formatter {
		String raw;
		SegmentFormat[] formats;

		@Override
		public String format(Arguments arguments) {
			if (formats == null) {
				return Objs.defaultIfNull(raw, "null");
			}
			StringBuilder sb = new StringBuilder();
			int pos = 0;
			for (SegmentFormat format : formats) {
				String str = format.format(arguments);
				if (pos < format.position) {
					sb.append(raw, pos, format.position);
					pos = format.position;
				}
				sb.append(str);
			}
			if (pos < raw.length()) {
				sb.append(raw, pos, raw.length());
			}
			return sb.toString();
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Segment{").append("raw='").append(raw).append('\'');
			if (formats != null && formats.length > 0) {
				sb.append(", formats=").append(Arrays.toString(formats));
			}
			sb.append('}');
			return sb.toString();
		}
	}

	/**
	 * 段落格式化器抽象类，作为所有具体格式化器的基类
	 * 包含位置信息和区域设置信息
	 */
	private static abstract class SegmentFormat implements Formatter {
		/**
		 * 格式化器在原始字符串中的位置
		 */
		int position;
		/**
		 * 用于本地化格式化的区域设置
		 */
		Locale locale;
	}

	/**
	 * 简单格式化器类，处理简单的参数替换和格式化
	 * 支持数字、日期等类型的自动格式化
	 */
	private static class SimpleFormat extends SegmentFormat {
		/**
		 * 参数索引
		 */
		String index;
		/**
		 * 特定格式化器（如NumberFormat, DateFormat等）
		 */
		Format format;
		/**
		 * 默认值，当参数为空时使用
		 */
		String defaults;

		/**
		 * 使用提供的参数格式化消息
		 *
		 * @param arguments 包含格式化参数的对象
		 * @return 格式化后的字符串结果
		 */
		@Override
		public String format(Arguments arguments) {
			Object obj = arguments.get(index);
			if (obj == null) {
				return Objs.defaultIfNull(defaults, "null");
			}
			if (format != null) {
				if (format instanceof NumberFormat) {
					if (!(obj instanceof Number)) {
						obj = Converters.convert(Number.class, obj);
					}
				} else if (format instanceof DateFormat) {
					if (!(obj instanceof Date)) {
						obj = Converters.convert(Date.class, obj);
					}
				}
				return format.format(obj);
			}
			if (obj instanceof Number) {
				return NumberFormat.getInstance(locale).format(obj);
			}
			if (obj instanceof Date) {
				return Dates.formatDefault((Date) obj);
			}
			if (obj instanceof String) {
				return (String) obj;
			}
			return Objs.defaultIfNull(obj.toString(), "null");
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("SimpleFormat{").append("index=").append(index);
			sb.append(", position=").append(position);
			if (format != null) {
				sb.append(", format=").append(format);
			}
			if (defaults != null) {
				sb.append(", default=").append(defaults);
			}
			sb.append('}');
			return sb.toString();
		}
	}

	/**
	 * 嵌套格式化器类，处理复杂的嵌套格式化逻辑
	 * 支持动态格式类型和修饰符
	 */
	private static class NestedFormat extends SegmentFormat {
		/**
		 * 索引段落，用于确定要格式化的参数位置
		 */
		Segment index;
		/**
		 * 类型段落，用于确定格式化类型（如number,date等）
		 */
		Segment type;
		/**
		 * 修饰符段落，用于确定格式化修饰符（如currency,short等）
		 */
		Segment modifier;
		/**
		 * 默认值段落，当参数为空时使用
		 */
		Segment defaults;

		/**
		 * 使用提供的参数格式化消息
		 *
		 * @param arguments 包含格式化参数的对象
		 * @return 格式化后的字符串结果
		 */
		@Override
		public String format(Arguments arguments) {
			String indexStr = null;
			if (index != null) {
				indexStr = index.format(arguments);
			}
			String defaultStr = null;
			if (defaults != null) {
				defaultStr = defaults.format(arguments);
			}
			if (indexStr == null) {
				return Objs.defaultIfNull(defaultStr, "null");
			}
			Object obj = arguments.get(indexStr);
			if (obj == null) {
				return Objs.defaultIfNull(defaultStr, "null");
			}
			String typeStr = null;
			String modifierStr = null;
			if (type != null) {
				typeStr = type.format(arguments);
			}
			if (modifier != null) {
				modifierStr = modifier.format(arguments);
			}
			Format format = null;
			if (typeStr != null) {
				format = makeFormat(locale, typeStr, modifierStr);
			}
			if (format != null) {
				return format.format(obj);
			}
			if (obj instanceof Number) {
				return NumberFormat.getInstance(locale).format(obj);
			}
			if (obj instanceof Date) {
				return Dates.formatDefault((Date) obj);
			}
			if (obj instanceof String) {
				return (String) obj;
			}
			return Objs.defaultIfNull(obj.toString(), "null");
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("NestedFormat{").append("index=").append(index);
			sb.append(", position=").append(position);
			if (type != null) {
				sb.append(", type=").append(type);
			}
			if (modifier != null) {
				sb.append(", modifier=").append(modifier);
			}
			if (defaults != null) {
				sb.append(", default=").append(defaults);
			}
			sb.append('}');
			return sb.toString();
		}
	}
}
