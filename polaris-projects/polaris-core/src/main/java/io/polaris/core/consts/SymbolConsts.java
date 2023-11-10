package io.polaris.core.consts;

/**
 * @author Qt
 * @since 1.8
 */
public interface SymbolConsts {

	/** 回车 Carriage Return */
	String CR = "\r";
	/** 换行 Linefeed */
	String LF = "\n";
	/** 回车换行 */
	String CRLF = CR + LF;
	/** 空串 */
	String EMPTY = "";
	/** 空格 */
	String SPACE = " ";
	/** 波浪符 */
	String TILDE = "~";
	/** 反引号 */
	String BACKQUOTE = "`";
	/** 感叹号 */
	String EXCLAMATION_MARK = "!";
	/** at符号 */
	String AT_MARK = "@";
	/** #井号/散列符号/pound */
	String HASH_MARK = "#";
	/** $美元符号 */
	String DOLLAR = "$";
	/** 百分号 */
	String PERCENT_MARK = "%";
	/** 脱字符/乘方符号 */
	String CARET = "^";
	/** &引用 */
	String AMPERSAND = "&";
	/** 星号 */
	String ASTERISK = "*";
	/** 连字号 */
	String HYPHEN = "-";
	String EQUALS = "=";
	/** 下划线 */
	String UNDERSCORE = "_";
	/** 点号 */
	String DOT = ".";
	/** 逗号 */
	String COMMA = ",";
	/** 分号 */
	String SEMICOLON = ";";
	/** 冒号 */
	String COLON = ":";
	/** 斜线 */
	String SLASH = "/";
	/** 双斜线 */
	String SLASH_SLASH = "//";
	/** 反斜线 */
	String BACKSLASH = "\\";
	/** 问号 */
	String QUESTION_MARK = "?";
	/** 破折号 */
	String DASH = "--";
	/** 省略号 */
	String ELLIPSIS = "...";
	/** 撇号 */
	String APOSTROPHE = "'";
	/** 竖线 */
	String VERTICAL_BAR = "|";
	/** 双线号 */
	String PARALLEL = "||";
	/** 双引号 */
	String DOUBLE_QUOTATION = "\"";
	/** 单引号 */
	String SINGLE_QUOTATION = "'";
	/** 左花括号 */
	String LEFT_BRACE = "{";
	/** 右花括号 */
	String RIGHT_BRACE = "}";
	/** 左圆括号 */
	String LEFT_PARENTHESIS = "(";
	/** 右圆括号 */
	String RIGHT_PARENTHESIS = ")";
	/** 左方括号 */
	String LEFT_SQUARE_BRACKETS = "[";
	/** 右方括号 */
	String RIGHT_SQUARE_BRACKETS = "]";
	/** 左尖括号 */
	String LEFT_ANGLE_BRACKETS = "<";
	/** 右尖括号 */
	String RIGHT_ANGLE_BRACKETS = ">";

	String MINUS = HYPHEN;
	String PLUS = "+";
	String MINUS_MINUS = MINUS + MINUS;
	String PLUS_PLUS = PLUS + PLUS;


	String BIT_OR = VERTICAL_BAR;
	String LOGIC_OR = BIT_OR + BIT_OR;
	String BIT_AND = AMPERSAND;
	String LOGIC_AND = BIT_AND + BIT_AND;
	String BIT_NOT = TILDE;
	String LOGIC_NOT = EXCLAMATION_MARK;

	String LESS_THAN = LEFT_ANGLE_BRACKETS;
	String LESS_EQUALS_THAN = LESS_THAN + EQUALS;
	String GREATER_THAN = RIGHT_ANGLE_BRACKETS;
	String GREATER_EQUALS_THAN = GREATER_THAN + EQUALS;
	String EQUALS_EQUALS = EQUALS + EQUALS;


	/** 格式化占位符 */
	String STR_FORMAT = "%s";
}
