package io.polaris.builder.change;

/**
 * @author Qt
 * @version Mar 02, 2022
 * @since 1.8
 */
public class PackageChangeMapping {

	private String srcPackage;
	private String destPackage;

	public PackageChangeMapping(final String srcPackage, final String destPackage) {
		super();
		this.srcPackage = srcPackage;
		this.destPackage = destPackage;
	}

	public String getDestPackage() {
		return destPackage;
	}

	public String getSrcPackage() {
		return srcPackage;
	}
}
