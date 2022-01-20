package io.polaris.dbv.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Index {
	private String tableCatalog;
	private String tableSchema;
	private String tableName;
	private String indexName;
	private String columnNames;
	private String ascOrDesc;
	/**
	 * 索引值是否可以不唯一
	 */
	private boolean nonUnique = true;
	private boolean unique = false;
	private String isUnique;

}
