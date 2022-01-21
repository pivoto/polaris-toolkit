package io.polaris.builder.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import io.polaris.builder.bean.db.Catalog;
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
	Map<String, Catalog> catalogs = MapKit.newCaseInsensitiveLinkedHashMap();

	public Catalog getCatalog(String name) {
		return catalogs.get(name);
	}

	public void addCatalog(Catalog catalog) {
		catalogs.put(catalog.getName(), catalog);
	}

}
