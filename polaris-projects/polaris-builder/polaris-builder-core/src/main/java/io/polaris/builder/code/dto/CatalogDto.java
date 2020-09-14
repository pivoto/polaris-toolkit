package io.polaris.builder.code.dto;

import io.polaris.core.map.Maps;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import lombok.*;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
@SuppressWarnings("Duplicates")
@Data
@XStreamAlias("catalog")
public class CatalogDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("name")
	@XStreamAsAttribute
	private String name;

	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@XStreamImplicit(itemFieldName = "schema", keyFieldName = "name")
	private Map<String, SchemaDto> schemas = Maps.newUpperCaseLinkedHashMap();


	public SchemaDto getSchema(String name) {
		return schemas.get(name);
	}

	public void addSchema(SchemaDto schema) {
		schemas.put(schema.getName(), schema);
	}

	@Override
	public CatalogDto clone() {
		return SerializationUtils.clone(this);
	}
}
