package io.polaris.core.annotation.processing;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import javax.tools.Diagnostic;

import io.polaris.core.annotation.Access;
import io.polaris.core.javapoet.*;

/**
 * @author Qt
 * @see CodeBlock 代码块生成语句的占位符说明
 * @since 1.8
 */
@SupportedAnnotationTypes(value = {"io.polaris.core.annotation.Access"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AccessProcessor extends BaseProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		processDeeply(roundEnv);
		return true;
	}

	private void processDeeply(RoundEnvironment roundEnv) {
		Set<? extends Element> rootElements = roundEnv.getRootElements();
		Map<TypeElement, Access> targets = new LinkedHashMap<>();
		ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {
			@Override
			public Void scan(Element element, Void p) {
				if (element instanceof TypeElement) {
					if (element.getKind() == ElementKind.CLASS) {
						Access access = AnnotationProcessorUtils.getAnnotation(env.getElementUtils(), element, Access.class);
						if (access != null) {
							targets.put((TypeElement) element, access);
						}
					}
				}
				return super.scan(element, p);
			}
		};
		for (Element element : rootElements) {
			scanner.scan(element);
		}

		targets.forEach((key, access) -> {
			AccessBeanInfo beanInfo = new AccessBeanInfo(key, access);
			if (beanInfo.isAccessFluent()) {
				generateFluentClass(beanInfo);
			}
			if (beanInfo.isAccessFields()) {
				generateFieldsClass(beanInfo);
			}
			if (beanInfo.isAccessGetters()) {
				generateGettersClass(beanInfo);
			}
			if (beanInfo.isAccessSetters()) {
				generateSettersClass(beanInfo);
			}
			if (beanInfo.isAccessMap()) {
				generateMapClass(beanInfo);
			}
		});
	}

	private void processDirectly(RoundEnvironment roundEnv) {
		Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Access.class);
		set.forEach(element -> {
			if (!(element instanceof TypeElement)) {
				return;
			}
			AccessBeanInfo beanInfo = new AccessBeanInfo((TypeElement) element, null);
			if (beanInfo.isAccessFluent()) {
				generateFluentClass(beanInfo);
			}
			if (beanInfo.isAccessFields()) {
				generateFieldsClass(beanInfo);
			}
			if (beanInfo.isAccessGetters()) {
				generateGettersClass(beanInfo);
			}
			if (beanInfo.isAccessSetters()) {
				generateSettersClass(beanInfo);
			}
			if (beanInfo.isAccessMap()) {
				generateMapClass(beanInfo);
			}
		});
	}

	private void generateFieldsClass(AccessBeanInfo beanInfo) {
		ClassName className = beanInfo.getFieldsClassName();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC);

		for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
			if (field.isAccessField()) {
				classBuilder.addField(
					FieldSpec.builder(ClassName.get(String.class), field.getFieldName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$S", field.getFieldName())
						.build()
				);
			}
		}

		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
		}
	}

	private void generateGettersClass(AccessBeanInfo beanInfo) {
		ClassName className = beanInfo.getGettersClassName();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC);
		for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
			if (field.isAccessGetter()) {
				classBuilder.addField(
					FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Function.class),
								beanInfo.getBeanTypeName(), field.getTypeName().box()
							), field.getGetterName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$L::$L", beanInfo.getBeanTypeName(), field.getGetterName())
						.build()
				);
			}
		}
		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
		}
	}

	private void generateSettersClass(AccessBeanInfo beanInfo) {
		ClassName className = beanInfo.getSettersClassName();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC);

		for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
			if (field.isAccessSetter()) {
				classBuilder.addField(
					FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(BiConsumer.class),
								beanInfo.getBeanTypeName(), field.getTypeName().box()
							), field.getSetterName(), Modifier.PUBLIC,
							Modifier.STATIC, Modifier.FINAL)
						.initializer("$L::$L", beanInfo.getBeanTypeName(), field.getSetterName())
						.build()
				);
			}
		}

		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
		}
	}

	private void generateMapClass(AccessBeanInfo beanInfo) {
		ClassName className = beanInfo.getMapClassName();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC)
			.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Object.class)))
			.superclass(ParameterizedTypeName.get(ClassName.get(AbstractMap.class), ClassName.get(String.class), ClassName.get(Object.class)))
			.addField(
				FieldSpec.builder(ParameterizedTypeName.get(
						ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(Type.class)),
					"types", Modifier.STATIC, Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addField(
				FieldSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addField(
				FieldSpec.builder(ParameterizedTypeName.get(
						ClassName.get(Map.class), ClassName.get(String.class),
						ParameterizedTypeName.get(ClassName.get(Supplier.class), ClassName.get(Object.class))
					),
					"getters", Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addField(
				FieldSpec.builder(ParameterizedTypeName.get(
						ClassName.get(Map.class), ClassName.get(String.class),
						ParameterizedTypeName.get(ClassName.get(Consumer.class), ClassName.get(Object.class))
					),
					"setters", Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addField(
				FieldSpec.builder(ParameterizedTypeName.get(
						ClassName.get(BiFunction.class), ClassName.get(Type.class), ClassName.get(Object.class), ClassName.get(Object.class)),
					"converter", Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addMethod(
				MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL).build())
					.addStatement("this(bean, (t,o)->$T.convert(t,o))", ClassName.get("io.polaris.core.converter", "Converters"))
					.build()
			)
			.addMethod(
				MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL).build())
					.addParameter(ParameterSpec.builder(ParameterizedTypeName.get(
						ClassName.get(BiFunction.class), ClassName.get(Type.class), ClassName.get(Object.class), ClassName.get(Object.class)), "converter", Modifier.FINAL).build())
					.addStatement("this.bean = bean")
					.addStatement("this.converter = converter")
					.addStatement("this.getters = new $T()", TypeName.get(HashMap.class))
					.addStatement("this.setters = new $T()", TypeName.get(HashMap.class))
					.addStatement("this.init()")
					.build()
			)
			.addMethod(MethodSpec.methodBuilder("of")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL).build())
				.addStatement("return new $T(bean)", className)
				.build()
			)
			.addMethod(MethodSpec.methodBuilder("get")
				.returns(beanInfo.getBeanTypeName())
				.addModifiers(Modifier.PUBLIC)
				.addStatement("return this.bean")
				.build()
			);
		{
			// static init
			CodeBlock.Builder staticInit = CodeBlock.builder()
				.addStatement("types = new $T()", TypeName.get(HashMap.class));
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				boolean accessGetter = field.isAccessGetter();
				boolean accessSetter = field.isAccessSetter();
				if (!accessGetter && !accessSetter) {
					// ignore field
					continue;
				}
				staticInit.add(CodeBlock.builder()
					.beginControlFlow("try")
					.addStatement("$T t = $T.class.getDeclaredField($S).getGenericType()", ClassName.get(Type.class),
						field.getDeclaredClassName(), field.getFieldName())
					.addStatement("types.put($S, t)", field.getFieldName())
					.nextControlFlow("catch($T ignored)", ClassName.get(Exception.class))
					.endControlFlow()
					.build());
			}
			classBuilder.addStaticBlock(staticInit.build());
		}
		{
			// init
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("init")
				.addModifiers(Modifier.PRIVATE);
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				CodeBlock.Builder getterSetterCode = CodeBlock.builder().beginControlFlow("");
				boolean accessGetter = field.isAccessGetter();
				boolean accessSetter = field.isAccessSetter();
				if (!accessGetter && !accessSetter) {
					// ignore field
					continue;
				}
				if (accessGetter) {
					getterSetterCode
						.addStatement("$T getter = () -> this.bean.$L()",
							ParameterizedTypeName.get(ClassName.get(Supplier.class),
								ClassName.get(Object.class)
							),
							field.getGetterName()
						)
						.addStatement("this.getters.put($S,getter)", field.getFieldName());
				}
				if (accessSetter) {
					getterSetterCode.addStatement("$T setter = o-> this.bean.$L(($T)o)",
							ParameterizedTypeName.get(ClassName.get(Consumer.class),
								ClassName.get(Object.class)
							),
							field.getSetterName(), field.getTypeName().box()
						)
						.addStatement("this.setters.put($S,setter)", field.getFieldName());
				}
				methodBuilder
					.addCode(getterSetterCode.endControlFlow().build())
				;
			}
			classBuilder.addMethod(methodBuilder.build());
		}

		{
			// getType
			classBuilder.addMethod(MethodSpec.methodBuilder("getType")
				.addModifiers(Modifier.STATIC, Modifier.PUBLIC)
				.returns(ClassName.get(Type.class))
				.addParameter(ParameterSpec.builder(ClassName.get(String.class), "key").build())
				.addStatement("return types.get(key)").build());
		}
		{
			// get
			classBuilder.addMethod(MethodSpec.methodBuilder("get")
				.addModifiers(Modifier.PUBLIC)
				.returns(ClassName.get(Object.class))
				.addParameter(ParameterSpec.builder(ClassName.get(Object.class), "key").build())
				.addCode(CodeBlock.builder()
					.beginControlFlow("if (!(key instanceof String))")
					.addStatement("return null")
					.endControlFlow()
					.build())
				.addStatement("$T supplier = this.getters.get((String) key)", ParameterizedTypeName.get(ClassName.get(Supplier.class),
					ClassName.get(Object.class)
				))
				.addCode(CodeBlock.builder()
					.beginControlFlow("if (supplier == null)")
					.addStatement("return null")
					.endControlFlow()
					.build())
				.addStatement("return supplier.get()")
				.build());
		}
		{
			// put
			classBuilder.addMethod(MethodSpec.methodBuilder("put")
				.addModifiers(Modifier.PUBLIC)
				.returns(ClassName.get(Object.class))
				.addParameter(ParameterSpec.builder(ClassName.get(String.class), "key").build())
				.addParameter(ParameterSpec.builder(ClassName.get(Object.class), "value").build())
				.addStatement("Object old = this.get(key)")
				.addStatement("$T consumer = this.setters.get(key)", ParameterizedTypeName.get(ClassName.get(Consumer.class),
					ClassName.get(Object.class)
				))
				.addCode(CodeBlock.builder()
					.beginControlFlow("if (consumer != null)")
					.addStatement("value = this.converter.apply(types.get(key), value)")
					.addStatement("this.setters.get(key).accept(value)")
					.endControlFlow()
					.build())
				.addStatement("return old")
				.build());
		}
		{
			// putAll
			classBuilder.addMethod(MethodSpec.methodBuilder("putAll")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.VOID)
				.addParameter(
					ParameterSpec.builder(
						ParameterizedTypeName.get(ClassName.get(Map.class),
							WildcardTypeName.subtypeOf(ClassName.get(String.class)), WildcardTypeName.subtypeOf(ClassName.get(Object.class)))
						, "m").build()
				)
				.addStatement("m.forEach((k, v) -> put(k,v))")
				.build());
		}
		{
			// size
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("size")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.INT);

			methodBuilder.addStatement("return this.getters.size()");
			classBuilder.addMethod(methodBuilder.build());
		}
		{
			// isEmpty
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("isEmpty")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.BOOLEAN);

			methodBuilder.addStatement("return this.getters.isEmpty()");
			classBuilder.addMethod(methodBuilder.build());
		}
		{
			// containsKey
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("containsKey")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.BOOLEAN)
				.addParameter(ParameterSpec.builder(ClassName.get(Object.class), "key").build());

			methodBuilder.addStatement("return this.getters.containsKey(key)");
			classBuilder.addMethod(methodBuilder.build());
		}
		{
			// keySet
			classBuilder.addMethod(MethodSpec.methodBuilder("keySet")
				.addModifiers(Modifier.PUBLIC)
				.returns(ParameterizedTypeName.get(ClassName.get(Set.class), ClassName.get(String.class)))
				.addStatement("return this.getters.keySet()")
				.build());
		}

		{
			// values
			classBuilder.addMethod(MethodSpec.methodBuilder("values")
				.addModifiers(Modifier.PUBLIC)
				.returns(
					ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Object.class))
				)
				.addStatement("return super.values()")
				.build());
		}

		{
			// entrySet
			classBuilder.addMethod(MethodSpec.methodBuilder("entrySet")
				.addModifiers(Modifier.PUBLIC)
				.returns(
					ParameterizedTypeName.get(ClassName.get(Set.class),
						ParameterizedTypeName.get(ClassName.get(Map.Entry.class), ClassName.get(String.class), ClassName.get(Object.class))
					)
				)
				.addStatement("$T set = new $T()"
					, ParameterizedTypeName.get(ClassName.get(Set.class),
						ParameterizedTypeName.get(ClassName.get(Map.Entry.class), ClassName.get(String.class), ClassName.get(Object.class))
					)
					, ParameterizedTypeName.get(ClassName.get(HashSet.class),
						ParameterizedTypeName.get(ClassName.get(Map.Entry.class), ClassName.get(String.class), ClassName.get(Object.class))
					))
				.beginControlFlow("for($T e: getters.entrySet())",
					ParameterizedTypeName.get(
						ClassName.get(Map.Entry.class), ClassName.get(String.class),
						ParameterizedTypeName.get(ClassName.get(Supplier.class), ClassName.get(Object.class))
					))
				.addStatement("String key = e.getKey()")
				.addStatement("set.add(new $T(key, e.getValue().get()))",
					ParameterizedTypeName.get(ClassName.get(AbstractMap.SimpleEntry.class),
						ClassName.get(String.class), ClassName.get(Object.class)))
				.endControlFlow()
				.addStatement("return set")
				.build());
		}
		{
			// containsValue
			classBuilder.addMethod(MethodSpec.methodBuilder("containsValue")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.BOOLEAN)
				.addParameter(ParameterSpec.builder(ClassName.get(Object.class), "value").build())
				.addStatement("return super.containsValue(value)")
				.build());
		}
		{
			// remove
			classBuilder.addMethod(MethodSpec.methodBuilder("remove")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.get(Object.class))
				.addParameter(ParameterSpec.builder(ClassName.get(Object.class), "key").build())
				.addStatement("throw new $T()", ClassName.get(UnsupportedOperationException.class))
				.build());
		}
		{
			// clear
			classBuilder.addMethod(MethodSpec.methodBuilder("clear")
				.addModifiers(Modifier.PUBLIC)
				.returns(TypeName.VOID)
				.addStatement("throw new $T()", ClassName.get(UnsupportedOperationException.class))
				.build());
		}

		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
		}
	}

	private void generateFluentClass(AccessBeanInfo beanInfo) {
		ClassName className = beanInfo.getFluentClassName();
		TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
			.addModifiers(Modifier.PUBLIC)
			.addField(
				FieldSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL, Modifier.PRIVATE).build()
			)
			.addMethod(
				MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
					.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL).build())
					.addStatement("this.bean = bean;").build()
			)
			.addMethod(MethodSpec.methodBuilder("of")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "bean", Modifier.FINAL).build())
				.addStatement("return new $T(bean)", className)
				.build()
			)
			.addMethod(MethodSpec.methodBuilder("get")
				.returns(beanInfo.getBeanTypeName())
				.addModifiers(Modifier.PUBLIC)
				.addStatement("return this.bean")
				.build()
			);

		{
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("copy")
				.addModifiers(Modifier.PUBLIC, Modifier.STATIC)
				.returns(TypeName.VOID)
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "orig", Modifier.FINAL).build())
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "dest", Modifier.FINAL).build());

			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				if (field.isAccessGetter() && field.isAccessSetter()) {
					methodBuilder.addStatement("dest.$L(orig.$L())", field.getSetterName(), field.getGetterName());
				}
			}
			classBuilder.addMethod(methodBuilder.build());
		}
		{
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("from")
				.addModifiers(Modifier.PUBLIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "orig", Modifier.FINAL).build());
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				if (field.isAccessGetter() && field.isAccessSetter()) {
					methodBuilder.addStatement("this.bean.$L(orig.$L())", field.getSetterName(), field.getGetterName());
				}
			}
			classBuilder.addMethod(methodBuilder.addStatement("return this").build());
		}
		{
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("from")
				.addModifiers(Modifier.PUBLIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(className, "orig", Modifier.FINAL).build());
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				if (field.isAccessGetter() && field.isAccessSetter()) {
					methodBuilder.addStatement("this.bean.$L(orig.$L())", field.getSetterName(), field.getFieldName());
				}
			}
			classBuilder.addMethod(methodBuilder.addStatement("return this").build());
		}
		{
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("to")
				.addModifiers(Modifier.PUBLIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(beanInfo.getBeanTypeName(), "dest", Modifier.FINAL).build());
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				if (field.isAccessGetter() && field.isAccessSetter()) {
					methodBuilder.addStatement("dest.$L(this.bean.$L())", field.getSetterName(), field.getGetterName());
				}
			}
			classBuilder.addMethod(methodBuilder.addStatement("return this").build());
		}
		{
			MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("to")
				.addModifiers(Modifier.PUBLIC)
				.returns(className)
				.addParameter(ParameterSpec.builder(className, "dest", Modifier.FINAL).build());
			for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
				if (field.isAccessGetter() && field.isAccessSetter()) {
					methodBuilder.addStatement("dest.$L(this.bean.$L())", field.getFieldName(), field.getGetterName());
				}
			}
			classBuilder.addMethod(methodBuilder.addStatement("return this").build());
		}

		for (AccessBeanInfo.FieldInfo field : beanInfo.getFields()) {
			if (field.isAccessGetter()) {
				classBuilder.addMethod(
					MethodSpec.methodBuilder(field.getFieldName())
						.addModifiers(Modifier.PUBLIC)
						.returns(field.getTypeName())
						.addStatement("return this.bean.$L()", field.getGetterName())
						.build()
				);
			}
			if (field.isAccessSetter()) {
				classBuilder.addMethod(
					MethodSpec.methodBuilder(field.getFieldName())
						.addModifiers(Modifier.PUBLIC)
						.returns(className)
						.addParameter(ParameterSpec.builder(field.getTypeName(), field.getFieldName(), Modifier.FINAL).build())
						.addStatement("this.bean.$L($L)", field.getSetterName(), field.getFieldName())
						.addStatement("return this")
						.build()
				);
			}
		}

		JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
		try {
			javaFile.writeTo(filer);
		} catch (IOException t) {
			messager.printMessage(Diagnostic.Kind.ERROR, t.toString());
		}
	}

}
