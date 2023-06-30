package io.polaris.builder.code.dto;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import io.polaris.core.map.Maps;
import lombok.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 */
@SuppressWarnings("ALL")
@Data
@XStreamAlias("schema")
public class SchemaDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@XStreamAlias("tables")
	@XStreamImplicit(itemFieldName = "table", keyFieldName = "name")
	private Map<String, TableDto> tables = Maps.newUpperCaseLinkedHashMap();


	public TableDto getTable(String name) {
		return tables.get(name);
	}

	public void addTable(TableDto table) {
		tables.put(table.getName(), table);
	}


	@Override
	public SchemaDto clone() {
		return SerializationUtils.clone(this);
	}
}
