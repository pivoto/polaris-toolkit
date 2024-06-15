package io.polaris.builder.dbv;

import io.polaris.core.string.Strings;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;

/**
 * @author Qt
 * @since  Aug 08, 2023
 */
public class DbCommentSplits {

	/**
	 * 将表或列的注释切分为标题与备注明细两部分
	 *
	 * @param comment
	 * @return
	 */
	public static Tuple2<String, String> split(String comment) {
		String columnLabel = Strings.coalesce(comment, "");
		String columnRemark = "";
		int columnLabelSplitIdx = columnLabel.indexOf('\n');
		if (columnLabelSplitIdx <= 0) {
			columnLabelSplitIdx = columnLabel.indexOf('(');
			if (columnLabelSplitIdx <= 0) {
				columnLabelSplitIdx = columnLabel.indexOf(':');
			}
			if (columnLabelSplitIdx <= 0) {
				columnLabelSplitIdx = columnLabel.indexOf('（');
			}
			if (columnLabelSplitIdx <= 0) {
				columnLabelSplitIdx = columnLabel.indexOf('：');
			}
		}
		if (columnLabelSplitIdx > 0) {
			columnRemark = columnLabelSplitIdx < columnLabel.length() - 1 ? columnLabel.substring(columnLabelSplitIdx + 1) : "";
			columnLabel = columnLabel.substring(0, columnLabelSplitIdx);
		}
		return Tuples.of(columnLabel.trim(), columnRemark.trim());
	}

}
