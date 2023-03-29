package io.polaris.builder.code.dto;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import io.polaris.dbv.toolkit.MapKit;
import lombok.*;

import java.util.Map;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Data
@XStreamAlias("schema")
public class SchemaDto {

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@XStreamAlias("tables")
	@XStreamImplicit(itemFieldName = "table", keyFieldName = "name")
	private Map<String, TableDto> tables = MapKit.newCaseInsensitiveLinkedHashMap();


	public TableDto getTable(String name) {
		return tables.get(name);
	}

	public void addTable(TableDto table) {
		tables.put(table.getName(), table);
	}


}
