package io.awesome.builder.bean.db;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import io.awesome.dbv.toolkit.MapKit;
import lombok.*;

import java.util.Map;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Data
@XStreamAlias("schema")
public class Schema {

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@XStreamAlias("tables")
	@XStreamImplicit(itemFieldName = "table", keyFieldName = "name")
	private Map<String, Table> tables = MapKit.newCaseInsensitiveLinkedHashMap();


	public Table getTable(String name) {
		return tables.get(name);
	}

	public void addTable(Table table) {
		tables.put(table.getName(), table);
	}


}
