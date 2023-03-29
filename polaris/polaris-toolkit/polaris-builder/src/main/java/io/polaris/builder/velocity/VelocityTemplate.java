package io.polaris.builder.velocity;

import io.polaris.dbv.toolkit.IOKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.ConfigurationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
public class VelocityTemplate {
	private static final Logger log = LoggerFactory.getLogger("code.template");

	private static final ThreadLocal<VelocityEngine> local = ThreadLocal.withInitial(() -> {
		VelocityEngine ve = new VelocityEngine();
		Properties properties = new Properties();
		properties.setProperty(RuntimeConstants.INPUT_ENCODING, RuntimeConstants.ENCODING_DEFAULT);
		properties.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
		properties.setProperty(RuntimeConstants.RESOURCE_LOADER + ".classpath." + RuntimeConstants.RESOURCE_LOADER_CLASS,
			ClasspathResourceLoader.class.getName());
		try {
			properties.load(IOKit.getInputStream("velocity.properties", VelocityTemplate.class));
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}

		ve.setProperties(properties);
		ve.init();
		return ve;
	});
	private static VelocityEngine singleton = local.get();
	private static ToolManager toolManager = new ToolManager();

	public static BufferedReader getTemplateReader(String template) throws IOException {
		return new BufferedReader(new InputStreamReader(IOKit.getInputStream(template), Charset.defaultCharset()));
	}

	public static Context createContext() {
		toolManager.configure(ConfigurationUtils.getGenericTools());
		Context context = toolManager.createContext();
		return context;
	}

	public static void write(Context context, Writer writer, String template) throws VelocityException {
		try {
			write(context, writer, getTemplateReader(template));
		} catch (VelocityException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new VelocityException(e);
		}
	}

	public static void write(Context context, Writer writer, Reader reader) throws VelocityException {
		try {
			singleton.evaluate(context, writer, "", reader);
		} catch (VelocityException e) {
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new VelocityException(e);
		}
	}

	public static String eval(Context context, String expression) {
		StringWriter stringWriter = new StringWriter();
		write(context, stringWriter, new StringReader(expression));
		stringWriter.flush();
		return stringWriter.toString();
	}

	public static String mergeAsString(String templateName, Context context) throws VelocityException {
		return mergeAsString(templateName, Charset.defaultCharset().name(), context);
	}

	public static String mergeAsString(String templateName, String encoding, Context context) throws VelocityException {
		Writer writer = new StringWriter();
		merge(templateName, encoding, context, writer);
		return writer.toString();
	}

	public static void merge(String templateName, String encoding, Context context, Writer writer) throws VelocityException {
		local.get().mergeTemplate(templateName, encoding, context, writer);
	}


}
