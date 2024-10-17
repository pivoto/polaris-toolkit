package io.polaris.core.regex;

import java.util.regex.Pattern;

/**
 * 常用正则表达式字符串池
 *
 * @author Qt
 * @since Oct 17, 2024
 */
@SuppressWarnings({"JavadocLinkAsPlainText", "UnnecessaryUnicodeEscape"})
public interface PatternConsts {

	/**
	 * 英文字母 、数字和下划线
	 */
	Pattern IDENTIFIER = Patterns.getPattern("\\w+");
	Pattern IDENTIFIER_WHOLE = Patterns.quoteWhole(IDENTIFIER);
	/**
	 * 数字
	 */
	Pattern DIGIT = Patterns.getPattern("\\d+");
	Pattern DIGIT_WHOLE = Patterns.quoteWhole(DIGIT);

	/**
	 * 字母
	 */
	Pattern ALPHABET = Patterns.getPattern("[a-zA-Z]+");
	Pattern ALPHABET_WHOLE = Patterns.quoteWhole(ALPHABET);
	/**
	 * 中文汉字<br>
	 * 参照维基百科汉字Unicode范围(https://zh.wikipedia.org/wiki/%E6%B1%89%E5%AD%97 页面右侧)
	 */
	Pattern CHINESE = Patterns.getPattern("[\u2E80-\u2EFF\u2F00-\u2FDF\u31C0-\u31EF\u3400-\u4DBF\u4E00-\u9FFF\uF900-\uFAFF\uD840\uDC00-\uD869\uDEDF\uD869\uDF00-\uD86D\uDF3F\uD86D\uDF40-\uD86E\uDC1F\uD86E\uDC20-\uD873\uDEAF\uD87E\uDC00-\uD87E\uDE1F]+");
	Pattern CHINESE_WHOLE = Patterns.quoteWhole(CHINESE);
	/**
	 * IP v4<br>
	 * 采用分组方式便于解析地址的每一个段
	 */
	Pattern IPV4 = Patterns.getPattern("(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)");
	Pattern IPV4_WHOLE = Patterns.quoteWhole(IPV4);
	/**
	 * IP v6
	 */
	Pattern IPV6 = Patterns.getPattern("(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))");
	Pattern IPV6_WHOLE = Patterns.quoteWhole(IPV6);
	/**
	 * 货币
	 */
	Pattern MONEY = Patterns.getPattern("(\\d+(?:\\.\\d+)?)");
	Pattern MONEY_WHOLE = Patterns.quoteWhole(MONEY);
	/**
	 * 邮件，符合RFC 5322规范，正则来自：http://emailregex.com/
	 * What is the maximum length of a valid email address? https://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address/44317754
	 * 注意email 要宽松一点。比如 jetz.chong@hutool.cn、jetz-chong@ hutool.cn、jetz_chong@hutool.cn、dazhi.duan@hutool.cn 宽松一点把，都算是正常的邮箱
	 */
	Pattern EMAIL = Patterns.getPattern("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
	Pattern EMAIL_WHOLE = Patterns.quoteWhole(EMAIL);

	/**
	 * 规则同EMAIL，添加了对中文的支持
	 */
	Pattern EMAIL_WITH_CHINESE = Patterns.getPattern("(?:[a-z0-9\\u4e00-\\u9fa5!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9\\u4e00-\\u9fa5!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9\\u4e00-\\u9fa5](?:[a-z0-9\\u4e00-\\u9fa5-]*[a-z0-9\\u4e00-\\u9fa5])?\\.)+[a-z0-9\\u4e00-\\u9fa5](?:[a-z0-9\\u4e00-\\u9fa5-]*[a-z0-9\\u4e00-\\u9fa5])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9\\u4e00-\\u9fa5-]*[a-z0-9\\u4e00-\\u9fa5]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");
	Pattern EMAIL_WITH_CHINESE_WHOLE = Patterns.quoteWhole(EMAIL_WITH_CHINESE);
	/**
	 * 移动电话
	 * eg: 中国大陆： +86  180 4953 1399，2位区域码标示+11位数字
	 * 中国大陆 +86 Mainland China
	 */
	Pattern MOBILE = Patterns.getPattern("(?:0|86|\\+86)?1[3-9]\\d{9}");
	Pattern MOBILE_WHOLE = Patterns.quoteWhole(MOBILE);
	/**
	 * 中国香港移动电话
	 * eg: 中国香港： +852 5100 4810， 三位区域码+10位数字, 中国香港手机号码8位数
	 */
	Pattern MOBILE_HK = Patterns.getPattern("(?:0|852|\\+852)?\\d{8}");
	Pattern MOBILE_HK_WHOLE = Patterns.quoteWhole(MOBILE_HK);
	/**
	 * 中国台湾移动电话
	 * eg: 中国台湾： +886 09 60 000000， 三位区域码+号码以数字09开头 + 8位数字, 中国台湾手机号码10位数
	 * 中国台湾 +886 Taiwan 国际域名缩写：TW
	 */
	Pattern MOBILE_TW = Patterns.getPattern("(?:0|886|\\+886)?(?:|-)09\\d{8}");
	Pattern MOBILE_TW_WHOLE = Patterns.quoteWhole(MOBILE_TW);
	/**
	 * 中国澳门移动电话
	 * eg: 中国澳门： +853 68 00000， 三位区域码 +号码以数字6开头 + 7位数字, 中国澳门手机号码8位数
	 * 中国澳门 +853 Macao 国际域名缩写：MO
	 */
	Pattern MOBILE_MO = Patterns.getPattern("(?:0|853|\\+853)?(?:|-)6\\d{7}");
	Pattern MOBILE_MO_WHOLE = Patterns.quoteWhole(MOBILE_MO);
	/**
	 * 座机号码<br>
	 * pr#387@Gitee
	 */
	Pattern TEL = Patterns.getPattern("(010|02\\d|0[3-9]\\d{2})-?(\\d{6,8})");
	Pattern TEL_WHOLE = Patterns.quoteWhole(TEL);
	/**
	 * 座机号码+400+800电话
	 *
	 * @see <a href="https://baike.baidu.com/item/800">800</a>
	 */
	Pattern TEL_400_800 = Patterns.getPattern("0\\d{2,3}[\\- ]?[1-9]\\d{6,7}|[48]00[\\- ]?[1-9]\\d{2}[\\- ]?\\d{4}");
	Pattern TEL_400_800_WHOLE = Patterns.quoteWhole(TEL_400_800);
	/**
	 * 18位身份证号码
	 */
	Pattern CITIZEN_ID = Patterns.getPattern("[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)");
	Pattern CITIZEN_ID_WHOLE = Patterns.quoteWhole(CITIZEN_ID);
	/**
	 * 邮编，兼容港澳台
	 */
	Pattern ZIP_CODE = Patterns.getPattern("(0[1-7]|1[0-356]|2[0-7]|3[0-6]|4[0-7]|5[0-7]|6[0-7]|7[0-5]|8[0-9]|9[0-8])\\d{4}|99907[78]");
	Pattern ZIP_CODE_WHOLE = Patterns.quoteWhole(ZIP_CODE);
	/**
	 * 生日
	 */
	Pattern BIRTHDAY = Patterns.getPattern("(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?");
	Pattern BIRTHDAY_WHOLE = Patterns.quoteWhole(BIRTHDAY);
	/**
	 * URI<br>
	 * 定义见：https://www.ietf.org/rfc/rfc3986.html#appendix-B
	 */
	Pattern URI = Patterns.getPattern("(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
	Pattern URI_WHOLE = Patterns.quoteWhole(URI);
	/**
	 * URL
	 */
	Pattern URL = Patterns.getPattern("[a-zA-Z]+://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]");
	Pattern URL_WHOLE = Patterns.quoteWhole(URL);
	/**
	 * Http URL（来自：http://urlregex.com/）<br>
	 * 此正则同时支持FTP、File等协议的URL
	 */
	Pattern URL_HTTP = Patterns.getPattern("(https?|ftp|file)://[\\w-+&@#/%?=~_|!:,.;]*[\\w-+&@#/%=~_|]");
	Pattern URL_HTTP_WHOLE = Patterns.quoteWhole(URL_HTTP);
	/**
	 * 中文字、英文字母、数字和下划线
	 */
	Pattern IDENTIFIER_WITH_CHINESE = Patterns.getPattern("[\u4E00-\u9FFF\\w]+");
	Pattern IDENTIFIER_WITH_CHINESE_WHOLE = Patterns.quoteWhole(IDENTIFIER_WITH_CHINESE);
	/**
	 * UUID
	 */
	Pattern UUID = Patterns.getPattern("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	Pattern UUID_WHOLE = Patterns.quoteWhole(UUID);
	/**
	 * 不带横线的UUID
	 */
	Pattern UUID_SIMPLE = Patterns.getPattern("[0-9a-fA-F]{32}");
	Pattern UUID_SIMPLE_WHOLE = Patterns.quoteWhole(UUID_SIMPLE);
	/**
	 * MAC地址正则
	 */
	Pattern MAC_ADDRESS = Patterns.getPattern("((?:[a-fA-F0-9]{1,2}[:-]){5}[a-fA-F0-9]{1,2})|0x(\\d{12}).+ETHER");
	Pattern MAC_ADDRESS_WHOLE = Patterns.quoteWhole(MAC_ADDRESS);
	/**
	 * 16进制字符串
	 */
	Pattern HEX = Patterns.getPattern("[a-fA-F0-9]+");
	Pattern HEX_WHOLE = Patterns.quoteWhole(HEX);
	/**
	 * 时间正则
	 */
	Pattern TIME = Patterns.getPattern("\\d{1,2}[:时]\\d{1,2}([:分]\\d{1,2})?秒?");
	Pattern TIME_WHOLE = Patterns.quoteWhole(TIME);
	/**
	 * 中国车牌号码（兼容新能源车牌）
	 */
	Pattern PLATE_NUMBER = Patterns.getPattern(
			//https://gitee.com/dromara/hutool/issues/I1B77H?from=project-issue
			"(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|" +
				//https://gitee.com/dromara/hutool/issues/I1BJHE?from=project-issue
				"([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|" +
			 "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))"
	);
	Pattern PLATE_NUMBER_WHOLE = Patterns.quoteWhole(PLATE_NUMBER);

	/**
	 * 统一社会信用代码
	 * <pre>
	 * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
	 * 第二部分：机构类别代码1位 (数字或大写英文字母)
	 * 第三部分：登记管理机关行政区划码6位 (数字)
	 * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
	 * 第五部分：校验码1位 (数字或大写英文字母)
	 * </pre>
	 */
	Pattern CREDIT_CODE = Patterns.getPattern("[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}");
	Pattern CREDIT_CODE_WHOLE = Patterns.quoteWhole(CREDIT_CODE);
	/**
	 * 车架号（车辆识别代号由世界制造厂识别代号(WMI、车辆说明部分(VDS)车辆指示部分(VIS)三部分组成，共 17 位字码。）<br>
	 * 别名：车辆识别代号、车辆识别码、车架号、十七位码<br>
	 * 标准号：GB 16735-2019<br>
	 * 标准官方地址：https://openstd.samr.gov.cn/bzgk/gb/newGbInfo?hcno=E2EBF667F8C032B1EDFD6DF9C1114E02
	 * 对年产量大于或等于1 000 辆的完整车辆和/或非完整车辆制造厂：
	 * <pre>
	 *   第一部分为世界制造厂识别代号(WMI)，3位
	 *   第二部分为车辆说明部分(VDS)，     6位
	 *   第三部分为车辆指示部分(VIS)，     8位
	 * </pre>
	 * <p>
	 * 对年产量小于 1 000 辆的完整车辆和/或非完整车辆制造厂：
	 * <pre>
	 *   第一部分为世界制造广识别代号(WMI),3位;
	 *   第二部分为车辆说明部分(VDS)，6位;
	 *   第三部分的三、四、五位与第一部分的三位字码起构成世界制造厂识别代号(WMI),其余五位为车辆指示部分(VIS)，8位。
	 * </pre>
	 *
	 * <pre>
	 *   eg:LDC613P23A1305189
	 *   eg:LSJA24U62JG269225
	 *   eg:LBV5S3102ESJ25655
	 * </pre>
	 */
	Pattern CAR_VIN = Patterns.getPattern("[A-HJ-NPR-Z0-9]{8}[X0-9]([A-HJ-NPR-Z0-9]{3}\\d{5}|[A-HJ-NPR-Z0-9]{5}\\d{3})");
	Pattern CAR_VIN_WHOLE = Patterns.quoteWhole(CAR_VIN);
	/**
	 * 驾驶证  别名：驾驶证档案编号、行驶证编号
	 * eg:430101758218
	 * 12位数字字符串
	 * 仅限：中国驾驶证档案编号
	 */
	Pattern CAR_DRIVING_LICENCE = Patterns.getPattern("[0-9]{12}");
	Pattern CAR_DRIVING_LICENCE_WHOLE = Patterns.quoteWhole(CAR_DRIVING_LICENCE);
	/**
	 * 中文姓名
	 * 维吾尔族姓名里面的点是 · 输入法中文状态下，键盘左上角数字1前面的那个符号；<br>
	 * 错误字符：{@code ．.。．.}<br>
	 * 正确维吾尔族姓名：
	 * <pre>
	 * 霍加阿卜杜拉·麦提喀斯木
	 * 玛合萨提别克·哈斯木别克
	 * 阿布都热依木江·艾斯卡尔
	 * 阿卜杜尼亚孜·毛力尼亚孜
	 * </pre>
	 * <pre>
	 * ----------
	 * 错误示例：孟  伟                reason: 有空格
	 * 错误示例：连逍遥0               reason: 数字
	 * 错误示例：依帕古丽-艾则孜        reason: 特殊符号
	 * 错误示例：牙力空.买提萨力        reason: 新疆人的点不对
	 * 错误示例：王建鹏2002-3-2        reason: 有数字、特殊符号
	 * 错误示例：雷金默(雷皓添）        reason: 有括号
	 * 错误示例：翟冬:亮               reason: 有特殊符号
	 * 错误示例：李                   reason: 少于2位
	 * ----------
	 * </pre>
	 * 总结中文姓名：2-60位，只能是中文和维吾尔族的点·
	 * 放宽汉字范围：如生僻姓名 刘欣䶮yǎn
	 */
	Pattern CHINESE_NAME = Patterns.getPattern("[\u2E80-\u9FFF·]{2,60}");
	Pattern CHINESE_NAME_WHOLE = Patterns.quoteWhole(CHINESE_NAME);

}
