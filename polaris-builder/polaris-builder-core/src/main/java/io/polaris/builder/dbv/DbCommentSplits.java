package io.polaris.builder.dbv;

import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;

/**
 * @author Qt
 * @since Aug 08, 2023
 */
public class DbCommentSplits {

	/**
	 * 将表或列的注释切分为标题与备注明细两部分
	 *
	 * @param comment
	 * @return
	 */
	public static Tuple2<String, String> split(String comment) {
		String columnLabel = Strings.coalesce(comment, "" );
		String columnRemark = "";
		int columnLabelSplitIdx = columnLabel.length() - 1;
		if (columnLabelSplitIdx > 0) {
			for (char c : new char[]{'\n', '\r', '（', '(', '：', ':'}) {
				// 取最小的切分点
				int i = columnLabel.indexOf(c);
				if (i >= 0) {
					columnLabelSplitIdx = Integer.min(i, columnLabelSplitIdx);
				}
			}
			if (columnLabelSplitIdx < columnLabel.length() - 1) {
				columnRemark = columnLabel.substring(columnLabelSplitIdx + 1).trim();
				if (!columnRemark.isEmpty()) {
					char start = columnLabel.charAt(columnLabelSplitIdx);
					char end = columnRemark.charAt(columnRemark.length() - 1);
					if ((start == '(' || start == '（') && (end == ')' || end == '）')) {
						columnRemark = columnRemark.substring(0, columnRemark.length() - 1);
					}
				}
				columnLabel = columnLabel.substring(0, columnLabelSplitIdx).trim();
			}
		}
		return Tuples.of(columnLabel.trim(), columnRemark.trim());
	}

}
