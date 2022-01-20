package io.polaris.dbv.cfg;

/**
 * @author Qt
 */
public enum DatabaseType {
	oracle("/oracle.xml"),
	informix("/informix.xml"),
	mysql("/mysql.xml"),
	dameng("/dameng.xml"),
	other(null),;

	private String cfgFile;

	private DatabaseType(String cfgFile) {
		this.cfgFile = cfgFile;
	}

	public String getCfgFile() {
		return cfgFile;
	}

}
