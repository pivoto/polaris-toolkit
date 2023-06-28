package io.polaris.dbv.cfg;

/**
 * @author Qt
 */
public enum DatabaseType {
	oracle("/dbv/oracle.xml"),
	informix("/dbv/informix.xml"),
	mysql("/dbv/mysql.xml"),
	dameng("/dbv/dameng.xml"),
	other(null),;

	private String cfgFile;

	private DatabaseType(String cfgFile) {
		this.cfgFile = cfgFile;
	}

	public String getCfgFile() {
		return cfgFile;
	}

}
