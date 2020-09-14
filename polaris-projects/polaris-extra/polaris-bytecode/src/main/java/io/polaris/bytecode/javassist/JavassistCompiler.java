//package io.polaris.bytecode.javassist;
//
//import javassist.CannotCompileException;
//import javassist.CtClass;
//import javassist.NotFoundException;
//
//import java.util.Arrays;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * @author Qt
// * @since 1.8
// */
//public class JavassistCompiler {
//
//	private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\s+([\\w\\.\\*]+);\n");
//
//	private static final Pattern EXTENDS_PATTERN = Pattern.compile("\\s+extends\\s+([\\w\\.]+)[^\\{]*\\{\n");
//
//	private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("\\s+implements\\s+([\\w\\.]+)\\s*\\{\n");
//
//	private static final Pattern METHODS_PATTERN = Pattern.compile("\n(private|public|protected)\\s+");
//
//	private static final Pattern FIELD_PATTERN = Pattern.compile("[^\n]+=[^\n]+;");
//
//	public String getSimpleClassName(String qualifiedName) {
//		if (null == qualifiedName) {
//			return null;
//		}
//		int i = qualifiedName.lastIndexOf('.');
//		return i < 0 ? qualifiedName : qualifiedName.substring(i + 1);
//	}
//
//	public Class<?> doCompile(ClassLoader classLoader, String name, String source) throws ClassNotFoundException {
//		JavassistBuilder builder = JavassistBuilder.newBuilder();
//		builder.className(name);
//		// process imported classes
//		Matcher matcher = IMPORT_PATTERN.matcher(source);
//		while (matcher.find()) {
//			builder.addImport(matcher.group(1).trim());
//		}
//
//		// process extended super class
//		matcher = EXTENDS_PATTERN.matcher(source);
//		if (matcher.find()) {
//			builder.superClassName(matcher.group(1).trim());
//		}
//
//		// process implemented interfaces
//		matcher = IMPLEMENTS_PATTERN.matcher(source);
//		if (matcher.find()) {
//			String[] ifaces = matcher.group(1).trim().split("\\,");
//			Arrays.stream(ifaces).forEach(i -> builder.addInterface(i.trim()));
//		}
//
//		// process constructors, fields, methods
//		String body = source.substring(source.indexOf('{') + 1, source.length() - 1);
//		String[] methods = METHODS_PATTERN.split(body);
//		String className = getSimpleClassName(name);
//		Arrays.stream(methods).map(String::trim).filter(m -> !m.isEmpty()).forEach(method -> {
//			if (method.startsWith(className)) {
//				builder.addConstructor(
//					JavassistBuilder.newConstructorBuilder()
//						.src("public " + method)
//				);
//			} else if (FIELD_PATTERN.matcher(method).matches()) {
//				builder.addField(
//					JavassistBuilder.newFieldBuilder()
//						.src("private " + method)
//				);
//			} else {
//				builder.addMethod(
//					JavassistBuilder.newMethodBuilder()
//						.src("public " + method)
//				);
//			}
//		});
//
//		// compile
//		try {
//			CtClass cls = builder.build(classLoader);
//			Class<?> c = cls.toClass();
//			return c;
//		} catch (NotFoundException | CannotCompileException e) {
//			throw new ClassNotFoundException(name, e);
//		}
//	}
//
//}
