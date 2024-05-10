package io.polaris.core.jdbc.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.MetaObject;
import lombok.Getter;

/**
 * @author Qt
 * @since  Dec 28, 2023
 */
@Getter
public class BeanMapping<T> {

	private MetaObject<T> metaObject;
	private List<BeanPropertyMapping> columns;
	private List<BeanCompositeMapping<?>> composites;
	private boolean caseInsensitive = true;
	private boolean caseCamel = true;
	private int caseModel;

	public BeanMapping() {
	}

	public BeanMapping(Class<T> javaType) {
		this.javaType(JavaType.of(javaType));
	}

	public BeanMapping(JavaType<T> javaType) {
		this.javaType(javaType);
	}

	public boolean isValid() {
		return metaObject != null;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void prepare() {
		caseModel = MetaObject.buildCaseModel(caseInsensitive, caseCamel);
		if (columns != null) {
			for (Iterator<BeanPropertyMapping> it = columns.iterator(); it.hasNext(); ) {
				BeanPropertyMapping col = it.next();
				if (!col.isValid()) {
					it.remove();
				}
			}
		}
		if (composites != null) {
			for (Iterator<BeanCompositeMapping<?>> it = composites.iterator(); it.hasNext(); ) {
				BeanCompositeMapping<?> composite = it.next();
				if (!composite.isValid()) {
					it.remove();
				} else {
					MetaObject compositeMetaObject = metaObject.getProperty(caseModel, composite.getProperty());
					if (compositeMetaObject == null) {
						it.remove();
					} else {
						BeanMapping compositeMapping = composite.getMapping();
						compositeMapping.metaObject(compositeMetaObject)
							.caseInsensitive(caseInsensitive).caseCamel(caseCamel)
							.prepare();
					}
				}
			}
		}
	}

	public BeanMapping<T> caseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
		return this;
	}

	public BeanMapping<T> caseCamel(boolean caseCamel) {
		this.caseCamel = caseCamel;
		return this;
	}

	public BeanMapping<T> metaObject(MetaObject<T> metaObject) {
		this.metaObject = metaObject;
		return this;
	}

	public BeanMapping<T> javaType(JavaType<T> javaType) {
		this.metaObject = MetaObject.of(javaType);
		return this;
	}

	public BeanMapping<T> column(BeanPropertyMapping column) {
		if (this.columns == null) {
			this.columns = new ArrayList<>();
		}
		this.columns.add(column);
		return this;
	}

	public BeanMapping<T> columns(List<BeanPropertyMapping> columns) {
		this.columns = columns;
		return this;
	}

	public BeanMapping<T> composite(BeanCompositeMapping<?> composite) {
		if (this.composites == null) {
			this.composites = new ArrayList<>();
		}
		this.composites.add(composite);
		return this;
	}

	public BeanMapping<T> composites(List<BeanCompositeMapping<?>> composites) {
		this.composites = composites;
		return this;
	}

}
