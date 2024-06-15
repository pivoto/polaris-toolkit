package io.polaris.bytecode.javassist;

import io.polaris.core.string.Strings;
import javassist.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qt
 * @since 1.8
 */
public class JavassistBuilder {

	private String className;
	private String superClassName = "java.lang.Object";
	private final List<String> importPackages = new ArrayList<>();
	private final List<String> interfaces = new ArrayList<>();
	private final List<FieldBuilder> fields = new ArrayList<>();
	private final List<ConstructorBuilder> constructors = new ArrayList<>();
	private final List<MethodBuilder> methods = new ArrayList<>();


	public static String getAvailableClassName(String baseName) {
		String className = baseName;
		for (int i = 0; true; i++) {
			className = baseName + i;
			try {
				Class.forName(className);
			} catch (ClassNotFoundException e) {
				break;
			}
		}
		return className;
	}

	public static JavassistBuilder newBuilder() {
		return new JavassistBuilder();
	}

	public static FieldBuilder newFieldBuilder() {
		return new FieldBuilder();
	}

	public static ConstructorBuilder newConstructorBuilder() {
		return new ConstructorBuilder();
	}

	public static MethodBuilder newMethodBuilder() {
		return new MethodBuilder();
	}

	public CtClass build() throws NotFoundException, CannotCompileException {
		return build(Thread.currentThread().getContextClassLoader());
	}

	public CtClass build(ClassLoader loader) throws NotFoundException, CannotCompileException {
		ClassPool pool = ContextClassPool.instance().getClassPool(loader);
		return build(pool);
	}

	public CtClass build(ClassPool pool) throws NotFoundException, CannotCompileException {
		CtClass ctClass = pool.makeClass(className, pool.get(superClassName));
		for (String importPackage : importPackages) {
			pool.importPackage(importPackage);
		}
		for (String anInterface : interfaces) {
			ctClass.addInterface(pool.get(anInterface));
		}
		for (FieldBuilder field : fields) {
			field.build(pool, ctClass);
		}

		for (ConstructorBuilder constructor : constructors) {
			constructor.build(pool, ctClass);
		}
		for (MethodBuilder method : methods) {
			method.build(pool, ctClass);
		}
		return ctClass;
	}


	public JavassistBuilder className(String className) {
		this.className = className;
		return this;
	}

	public JavassistBuilder superClassName(String superClassName) {
		this.superClassName = superClassName;
		return this;
	}

	public JavassistBuilder addImport(String... imports) {
		for (String s : imports) {
			importPackages.add(s);
		}
		return this;
	}

	public JavassistBuilder addImport(Iterable<String> imports) {
		for (String s : imports) {
			importPackages.add(s);
		}
		return this;
	}

	public JavassistBuilder addInterface(String... args) {
		for (String s : args) {
			interfaces.add(s);
		}
		return this;
	}

	public JavassistBuilder addInterface(Iterable<String> args) {
		for (String s : args) {
			interfaces.add(s);
		}
		return this;
	}

	public JavassistBuilder addField(FieldBuilder... args) {
		for (FieldBuilder arg : args) {
			fields.add(arg);
		}
		return this;
	}

	public JavassistBuilder addField(Iterable<FieldBuilder> args) {
		for (FieldBuilder arg : args) {
			fields.add(arg);
		}
		return this;
	}

	public JavassistBuilder addConstructor(ConstructorBuilder... args) {
		for (ConstructorBuilder arg : args) {
			constructors.add(arg);
		}
		return this;
	}


	public JavassistBuilder addConstructor(Iterable<ConstructorBuilder> args) {
		for (ConstructorBuilder arg : args) {
			constructors.add(arg);
		}
		return this;
	}

	public JavassistBuilder addMethod(MethodBuilder... args) {
		for (MethodBuilder arg : args) {
			methods.add(arg);
		}
		return this;
	}

	public JavassistBuilder addMethod(Iterable<MethodBuilder> args) {
		for (MethodBuilder arg : args) {
			methods.add(arg);
		}
		return this;
	}


	public static class MethodBuilder {
		private String src;
		private CtClass returnType;
		private String name;
		private List<String> parameterTypes = new ArrayList<>();
		private List<String> exceptionTypes = new ArrayList<>();
		private String body;
		private boolean varargs;

		public void build(ClassPool pool, CtClass ctClass) throws CannotCompileException, NotFoundException {
			if (!Strings.isBlank(src)) {
				CtMethod ctMethod = CtNewMethod.make(src, ctClass);
				ctClass.addMethod(ctMethod);
				if (varargs) {
					ctMethod.setModifiers(ctMethod.getModifiers() | Modifier.VARARGS);
				}
			} else {
				CtClass[] parameters = new CtClass[parameterTypes.size()];
				CtClass[] exceptions = new CtClass[exceptionTypes.size()];
				for (int i = 0; i < parameters.length; i++) {
					parameters[i] = pool.get(parameterTypes.get(i));
				}
				for (int i = 0; i < exceptions.length; i++) {
					exceptions[i] = pool.get(exceptionTypes.get(i));
				}
				CtMethod ctMethod = CtNewMethod.make(returnType, name, parameters, exceptions, body, ctClass);
				if (varargs) {
					ctMethod.setModifiers(ctMethod.getModifiers() | Modifier.VARARGS);
				}
				ctClass.addMethod(ctMethod);
			}
		}

		public MethodBuilder src(String src) {
			this.src = src;
			return this;
		}

		public MethodBuilder varargs() {
			this.varargs = true;
			return this;
		}

		public MethodBuilder varargs(boolean val) {
			this.varargs = val;
			return this;
		}

		public MethodBuilder returnType(CtClass returnType) {
			this.returnType = returnType;
			return this;
		}

		public MethodBuilder name(String name) {
			this.name = name;
			return this;
		}

		public MethodBuilder addParameter(String... types) {
			for (String type : types) {
				parameterTypes.add(type);
			}
			return this;
		}

		public MethodBuilder clearParameters() {
			parameterTypes.clear();
			return this;
		}

		public MethodBuilder addException(String... types) {
			for (String type : types) {
				exceptionTypes.add(type);
			}
			return this;
		}

		public MethodBuilder clearExceptions() {
			exceptionTypes.clear();
			return this;
		}

		public MethodBuilder body(String body) {
			this.body = body;
			return this;
		}
	}

	public static class FieldBuilder {
		private String src;
		private String name;
		private String type;
		private int modifier;
		private boolean withSetter;
		private boolean withGetter;

		public void build(ClassPool pool, CtClass ctClass) throws CannotCompileException, NotFoundException {
			CtField ctField;
			if (!Strings.isBlank(src)) {
				ctField = CtField.make(src, ctClass);
				ctField.getType();
			} else {
				CtClass fieldType = pool.get(type);
				ctField = new CtField(fieldType, name, ctClass);
				ctField.setModifiers(modifier);
			}
			ctClass.addField(ctField);

			CtClass fieldType = ctField.getType();
			String name = ctField.getName();
			if (withSetter) {
				CtMethod setter = CtNewMethod.setter("set" + Strings.capitalize(name), ctField);
				ctClass.addMethod(setter);
			}
			if (withGetter) {
				if (fieldType.getSimpleName().equals("boolean")) {
					CtMethod getter = CtNewMethod.getter("is" + Strings.capitalize(name), ctField);
					ctClass.addMethod(getter);
				} else {
					CtMethod getter = CtNewMethod.getter("get" + Strings.capitalize(name), ctField);
					ctClass.addMethod(getter);
				}
			}
		}

		public FieldBuilder withSetter() {
			this.withSetter = true;
			return this;
		}

		public FieldBuilder withGetter() {
			this.withGetter = true;
			return this;
		}

		public FieldBuilder withSetter(boolean withSetter) {
			this.withSetter = withSetter;
			return this;
		}

		public FieldBuilder withGetter(boolean withGetter) {
			this.withGetter = withGetter;
			return this;
		}

		public FieldBuilder src(String val) {
			src = val;
			return this;
		}

		public FieldBuilder name(String val) {
			name = val;
			return this;
		}

		public FieldBuilder type(String val) {
			type = val;
			return this;
		}

		public FieldBuilder modifier(int val) {
			modifier = val;
			return this;
		}

	}

	public static class ConstructorBuilder {
		private String src;
		private List<String> parameterTypes = new ArrayList<>();
		private List<String> exceptionTypes = new ArrayList<>();
		private String body;


		public void build(ClassPool pool, CtClass ctClass) throws CannotCompileException, NotFoundException {
			if (!Strings.isBlank(src)) {
				CtConstructor constructor = CtNewConstructor.make(src, ctClass);
				ctClass.addConstructor(constructor);
			} else {
				CtClass[] parameters = new CtClass[parameterTypes.size()];
				CtClass[] exceptions = new CtClass[exceptionTypes.size()];
				for (int i = 0; i < parameters.length; i++) {
					parameters[i] = pool.get(parameterTypes.get(i));
				}
				for (int i = 0; i < exceptions.length; i++) {
					exceptions[i] = pool.get(exceptionTypes.get(i));
				}
				CtConstructor constructor = CtNewConstructor.make(parameters, exceptions, body, ctClass);
				ctClass.addConstructor(constructor);
			}
		}

		public ConstructorBuilder src(String val) {
			src = val;
			return this;
		}

		public ConstructorBuilder addParameter(String... types) {
			for (String type : types) {
				parameterTypes.add(type);
			}
			return this;
		}

		public ConstructorBuilder clearParameters() {
			parameterTypes.clear();
			return this;
		}

		public ConstructorBuilder addException(String... types) {
			for (String type : types) {
				exceptionTypes.add(type);
			}
			return this;
		}

		public ConstructorBuilder clearExceptions() {
			exceptionTypes.clear();
			return this;
		}

		public ConstructorBuilder body(String body) {
			this.body = body;
			return this;
		}
	}

}
