package io.polaris.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author Qt
 * @since  May 08, 2024
 */
@Mojo(name = "transform", threadSafe = true, defaultPhase = LifecyclePhase.PROCESS_RESOURCES)
public class PropertiesTransformMojo extends AbstractMojo {
	/** The native encoding the files are in. */
	@Parameter(defaultValue = "${project.build.sourceEncoding}")
	public String encoding;
	/** Patterns of files to include. Default is "**\/*.properties". */
	@Parameter
	public String[] includes;
	/** Patterns of files that must be excluded. */
	@Parameter
	public String[] excludes;
	/** output directory. */
	@Parameter(defaultValue = "${project.build.outputDirectory}")
	public File outputDirectory;
	/** test output directory. */
	@Parameter(defaultValue = "${project.build.testOutputDirectory}")
	public File testOutputDirectory;

	/** properties files to duplicate. */
	@Parameter
	public DuplicateFile[] duplicateFiles;


	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.includes == null) {
			this.includes = new String[]{"**/*.properties"};
		}
		if (this.excludes == null) {
			this.excludes = new String[0];
		}
		if (this.encoding == null || this.encoding.isEmpty()) {
			this.encoding = Charset.defaultCharset().displayName();
			getLog().warn("Using platform encoding (" + this.encoding + " actually) to convert resources!");
		}
		if (getLog().isDebugEnabled()) {
			getLog().debug("Includes: " + Arrays.asList(this.includes));
			getLog().debug("Excludes: " + Arrays.asList(this.excludes));
		}
		final String incl = PropertiesKit.join(",", this.includes);
		final String excl = PropertiesKit.join(",", this.excludes);

		transform(outputDirectory, incl, excl);
		transform(testOutputDirectory, incl, excl);
		duplicate(outputDirectory);
	}

	private void transform(File dir, String incl, String excl) throws MojoExecutionException {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		try {
			for (File file : FileUtils.getFiles(dir, incl, excl)) {
				try {
					getLog().info("Converting file: " + file);
					PropertiesKit.nativeToAscii(getLog(), file, encoding);
				} catch (final IOException e) {
					throw new MojoExecutionException("Unable to convert file:" + file.getAbsolutePath(), e);
				}
			}
		} catch (final IOException e) {
			throw new MojoExecutionException("Unable to convert files: " + dir.getAbsoluteFile());
		}
	}

	private void duplicate(File dir) throws MojoExecutionException {
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		if (duplicateFiles == null || duplicateFiles.length == 0) {
			return;
		}
		for (DuplicateFile conf : duplicateFiles) {
			if (conf.src == null || conf.src.isEmpty()) {
				getLog().warn("duplicate src is required!");
				continue;
			}
			if (conf.dest == null || conf.dest.isEmpty()) {
				getLog().warn("duplicate src is required!");
				continue;
			}
			File src = new File(dir, conf.src);
			File dest = new File(dir, conf.dest);
			if (src.exists() && dest.exists()) {
				continue;
			}
			if (src.exists()) {
				try {
					getLog().info("Duplicate file: " + src + " to " + dest);
					PropertiesKit.copy(src, dest);
				} catch (IOException e) {
					throw new MojoExecutionException("Unable to duplicate file: " + src + " to " + dest, e);
				}
			} else if (dest.exists()) {
				try {
					getLog().info("Duplicate file: " + dest + " to " + src);
					PropertiesKit.copy(dest, src);
				} catch (IOException e) {
					throw new MojoExecutionException("Unable to duplicate file: " + dest + " to " + src, e);
				}
			}
		}
	}


}
