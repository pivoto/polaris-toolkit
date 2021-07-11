//package io.awesome.dbv;
//
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.Velocity;
//import org.apache.velocity.app.VelocityEngine;
//
//import java.io.Reader;
//import java.io.StringWriter;
//import java.io.Writer;
//
//public class VelocityHelper {
//	static final ThreadLocal<VelocityEngine> local = new ThreadLocal<VelocityEngine>();
//
//	static void init() throws Exception {
//		if (local.get() == null) {
//			VelocityEngine ve = new VelocityEngine();
//			ve.setProperty(Velocity.RESOURCE_LOADER, "class");
//			ve.setProperty("class.resource.loader.class",
//					"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//			ve.init();
//			local.set(ve);
//		}
//	}
//
//	public static VelocityEngine get() throws Exception {
//		init();
//		return local.get();
//	}
//
//	public static void eval(VelocityContext context, Writer writer, Reader reader) throws Exception {
//		get().evaluate(context, writer, "", reader);
//		// Velocity.evaluate(context, writer, "", reader);
//	}
//
//	public static String getEval(VelocityContext context, Reader reader) throws Exception {
//		Writer writer = new StringWriter();
//		eval(context, writer, reader);
//		return writer.toString();
//	}
//
//	public static void eval(VelocityContext context, Writer writer, String s) throws Exception {
//		get().evaluate(context, writer, "", s);
//		// Velocity.evaluate(context, writer, "", s);
//	}
//
//	public static String getEval(VelocityContext context, String s) throws Exception {
//		Writer writer = new StringWriter();
//		eval(context, writer, s);
//		return writer.toString();
//	}
//
//	public static void writeWithTemplate(String path, String encoding, VelocityContext context,
//			Writer writer) throws Exception {
//		get().mergeTemplate(path, encoding, context, writer);
//		// Velocity.mergeTemplate(path, encoding, context, writer);
//	}
//
//	public static String getWithTemplate(String path, String encoding, VelocityContext context)
//			throws Exception {
//		Writer writer = new StringWriter();
//		writeWithTemplate(path, encoding, context, writer);
//		return writer.toString();
//	}
//
//	public static String getTemplate(String path, VelocityContext context) throws Exception {
//		return getWithTemplate(path, "utf8", context);
//	}
//
//}
