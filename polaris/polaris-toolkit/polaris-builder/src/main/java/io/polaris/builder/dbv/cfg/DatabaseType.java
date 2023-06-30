package io.polaris.builder.dbv.cfg;

/**
 * @author Qt
 */
public enum DatabaseType {
	oracle("/META-INF/dbv/oracle.xml"),
	informix("/META-INF/dbv/informix.xml"),
	mysql("/META-INF/dbv/mysql.xml"),
	dameng("/META-INF/dbv/dameng.xml"),
	other(null),;

	private String cfgFile;

	private DatabaseType(String cfgFile) {
		this.cfgFile = cfgFile;
	}

	public String getCfgFile() {
		return cfgFile;
	}

}
