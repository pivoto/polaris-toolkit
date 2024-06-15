package io.polaris.core.string;

/**
 * @author Qt
 * @since 1.8
 */
public class Unicodes {

	public static String toUnicode(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] > 0 && chs[i] < 127) {
				sb.append(chs[i]);
			} else {
				String str = Integer.toString(chs[i], 16);
				sb.append("\\u").append("0000", 0, 4 - str.length()).append(str);
			}
		}
		return sb.toString();
	}

	public static String fromUnicode(String s) {
		StringBuilder sb = new StringBuilder();
		char[] chs = s.toCharArray();
		for (int i = 0; i < chs.length; i++) {
			if (chs[i] == '\\' && i + 5 < chs.length
				&& Character.toLowerCase(chs[i + 1]) == 'u') {
				try {
					short sht = (short) Integer.parseInt(new String(new char[]{
							chs[i + 2], chs[i + 3], chs[i + 4], chs[i + 5]}),
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
}
