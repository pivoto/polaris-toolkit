package io.polaris.builder.dbv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Qt
 * @since 1.8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CellMergeArea {
	private int firstRow;
	private int lastRow;
	private int firstCol;
	private int lastCol;
}
