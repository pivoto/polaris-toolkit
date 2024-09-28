package io.polaris.builder.changer;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
@Data
public class ChangerDto {
	private Charset charset;
	private String src;
	private String dest;
	private Boolean copyAll;
	private Boolean includeFilename;
	private String extensions;
	private Set<String> namePatterns;
	private Set<String> ignorePatterns;
	private Set<String> ignoreMappingPatterns;
	private Set<String> sourcePaths;
	private Map<String, String> packageMapping;

	public void mergeFrom(ChangerDto parent) {
		if (this.charset == null) {
			this.charset = parent.charset;
		}
		if (this.charset == null) {
			this.charset = Charset.defaultCharset();
		}
		if (StringUtils.isBlank(src)) {
			this.src = parent.src;
		}
		if (StringUtils.isBlank(dest)) {
			this.dest = parent.dest;
		}
		if (StringUtils.isBlank(extensions)) {
			this.extensions = parent.extensions;
		}
		if (copyAll == null) {
			this.copyAll = parent.copyAll;
		}
		if (copyAll == null) {
			this.copyAll = true;
		}
		if (includeFilename == null) {
			this.includeFilename = parent.includeFilename;
		}
		if (includeFilename == null) {
			this.includeFilename = true;
		}
		if (namePatterns == null) {
			this.namePatterns = parent.namePatterns;
		}else{
			if (parent.namePatterns != null) {
				this.namePatterns.addAll(parent.namePatterns);
			}
		}
		if (ignoreMappingPatterns == null) {
			this.ignoreMappingPatterns = parent.ignoreMappingPatterns;
		}else{
			if (parent.ignoreMappingPatterns != null) {
				this.ignoreMappingPatterns.addAll(parent.ignoreMappingPatterns);
			}
		}
		if (ignorePatterns == null) {
			this.ignorePatterns = parent.ignorePatterns;
		}else{
			if (parent.ignorePatterns != null) {
				this.ignorePatterns.addAll(parent.ignorePatterns);
			}
		}
		if (sourcePaths == null) {
			this.sourcePaths = parent.sourcePaths;
		}else{
			if (parent.sourcePaths != null) {
				this.sourcePaths.addAll(parent.sourcePaths);
			}
		}
		if (packageMapping == null) {
			this.packageMapping = parent.packageMapping;
		}else{
			if (parent.packageMapping != null) {
				this.packageMapping.putAll(parent.packageMapping);
			}
		}
	}
}
