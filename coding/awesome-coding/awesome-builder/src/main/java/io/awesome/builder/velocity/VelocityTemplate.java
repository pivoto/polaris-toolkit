package io.awesome.builder.velocity;

import io.awesome.dbv.toolkit.IOKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.ToolManager;
import org.apache.velocity.tools.config.ConfigurationUtils;

import java.io.*;
import java.util.Properties;

/**
 * @author Qt
 * @version Jun 04, 2019
 */
@Slf4j
public class VelocityTemplate {

	public static final String FILE_ENCODE = "UTF8";
	private static final ThreadLocal<VelocityEngine> local = ThreadLocal.withInitial(() -> {
		VelocityEngine ve = new VelocityEngine();
		Properties properties = new Properties();
		try {
			properties.load(IOKit.getInputStream("velocity.properties", VelocityTemplate.class));
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
		properties.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
		String key = "class." + RuntimeConstants.RESOURCE_LOADER + "." + RuntimeConstants.RESOURCE_LOADER_CLASS;
		properties.setProperty(key, ClasspathResourceLoader.class.getName());

		ve.setProperties(properties);
		ve.init();
		return ve;
	});
	private static VelocityEngine singleton = local.get();
	private static ToolManager toolManager = new ToolManager();

	public static BufferedReader getTemplateReader(String template) throws IOException {
		return new BufferedReader(new InputStreamReader(IOKit.getInputStream(template), FILE_ENCODE));
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
		return mergeAsString(templateName, FILE_ENCODE, context);
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
