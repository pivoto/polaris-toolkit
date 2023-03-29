package io.polaris.builder.code.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import io.polaris.dbv.toolkit.MapKit;
import lombok.*;

import java.util.Map;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
@SuppressWarnings("Duplicates")
@Data
@XStreamAlias("catalog")
public class CatalogDto {

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@XStreamImplicit(itemFieldName = "schema", keyFieldName = "name")
	private Map<String, SchemaDto> schemas = MapKit.newCaseInsensitiveLinkedHashMap();


	public SchemaDto getSchema(String name) {
		return schemas.get(name);
	}

	public void addSchema(SchemaDto schema) {
		schemas.put(schema.getName(), schema);
	}

}
