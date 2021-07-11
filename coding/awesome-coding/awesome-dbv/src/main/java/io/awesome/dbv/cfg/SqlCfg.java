package io.awesome.dbv.cfg;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Data;

/**
 * @author Qt
 */
@Data
@XStreamAlias("sql")
public class SqlCfg {

	@XStreamAsAttribute
	@XStreamAlias("id")
	private String id;
	@XStreamAlias("value")
	private String sql;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

}
