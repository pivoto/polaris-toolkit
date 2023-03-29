package io.polaris.builder.code.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
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
@XStreamAlias("tables")
public class Tables {

	@ToString.Exclude
	@XStreamImplicit(itemFieldName = "catalog", keyFieldName = "name")
	Map<String, CatalogDto> catalogs = MapKit.newCaseInsensitiveLinkedHashMap();

	public CatalogDto getCatalog(String name) {
		return catalogs.get(name);
	}

	public void addCatalog(CatalogDto catalog) {
		catalogs.put(catalog.getName(), catalog);
	}

}
