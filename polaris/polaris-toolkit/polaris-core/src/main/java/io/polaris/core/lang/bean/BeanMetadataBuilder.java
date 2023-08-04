package io.polaris.core.lang.bean;

import io.polaris.core.compiler.MemoryCompiler;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.lang.Types;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.tuple.Tuple2;
import io.polaris.core.tuple.Tuples;
import com.squareup.javapoet.*;
import org.objectweb.asm.*;

import javax.lang.model.element.Modifier;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Qt
 * @since 1.8,  Aug 03, 2023
 */
class BeanMetadataBuilder {
	private static final AtomicLong seq = new AtomicLong(0);
	private static final String $$_BEAN_METADATA_$$ = "$$BeanMetadata$$";


	static Tuple2<String, Map<String, byte[]>> build(Class beanType) throws IntrospectionException {
		Map<String, byte[]> classes = new LinkedHashMap<>();
		String beanTypeDesc = org.objectweb.asm.Type.getInternalName(beanType);
		String javaClassName = beanType.getPackage().getName() + "." + beanType.getSimpleName() + $$_BEAN_METADATA_$$ + seq.incrementAndGet();
		if (javaClassName.startsWith("java.")) {
			javaClassName = "javax." + javaClassName.substring("java.".length());
		}

		String className = javaClassName.replace('.', '/');
		String interfaceName = BeanMetadata.class.getName().replace('.', '/');
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		classWriter.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className,
			null, "java/lang/Object", new String[]{interfaceName});

		classWriter.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		BeanInfo beanInfo = Introspector.getBeanInfo(beanType);

		for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
			String key = pd.getName();
			Method writeMethod = pd.getWriteMethod();
			if (writeMethod != null) {
				Type type = writeMethod.getGenericParameterTypes()[0];
				classes.put(javaClassName + "$Type4" + key, buildInnerClass(className, "Type4" + key, type));
				classWriter.visitInnerClass(className + "$Type4" + key, className, "Type4" + key, 0);
			}
		}

		// constructor
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(1, 1);
			methodVisitor.visitEnd();
		}
		// types
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "types", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/lang/reflect/Type;>;", null);
			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/LinkedHashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String key = pd.getName();
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod != null) {
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(key);
					methodVisitor.visitTypeInsn(NEW, className + "$Type4" + key);
					methodVisitor.visitInsn(DUP);
					methodVisitor.visitVarInsn(ALOAD, 0);
					methodVisitor.visitMethodInsn(INVOKESPECIAL, className + "$Type4" + key, "<init>", "(L" + className + ";)V", false);
					methodVisitor.visitMethodInsn(INVOKEVIRTUAL, className + "$Type4" + key, "getType", "()Ljava/lang/reflect/Type;", false);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
				}
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(5, 2);
			methodVisitor.visitEnd();
		}

		// getters
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "getters", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/Function<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);
			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/LinkedHashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			int idx = 0;
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String key = pd.getName();
				Method readMethod = pd.getReadMethod();
				if (Reflects.isGetClassMethod(readMethod)) {
					continue;
				}
				if (readMethod != null) {
					String lambdaName = "lambda$getters$" + (idx++);
					methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;",
						new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
						new Object[]{
							org.objectweb.asm.Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"),
							new Handle(Opcodes.H_INVOKESTATIC, className, lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", false),
							org.objectweb.asm.Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")
						}
					);
					methodVisitor.visitVarInsn(ASTORE, 2);
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(key);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
				}
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
		// setters
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "setters", "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/BiConsumer<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);
			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/LinkedHashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			int idx = 0;
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String key = pd.getName();
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod != null) {
					String lambdaName = "lambda$setters$" + (idx++);
					methodVisitor.visitInvokeDynamicInsn("accept", "()Ljava/util/function/BiConsumer;",
						new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false),
						new Object[]{
							org.objectweb.asm.Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V"),
							new Handle(Opcodes.H_INVOKESTATIC, className,
								lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", false),
							org.objectweb.asm.Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
						}
					);
					methodVisitor.visitVarInsn(ASTORE, 2);
					methodVisitor.visitVarInsn(ALOAD, 1);
					methodVisitor.visitLdcInsn(key);
					methodVisitor.visitVarInsn(ALOAD, 2);
					methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
					methodVisitor.visitInsn(POP);
				}
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(3, 3);
			methodVisitor.visitEnd();
		}
		// getters method
		{
			int idx = 0;
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String key = pd.getName();
				Method readMethod = pd.getReadMethod();
				if (Reflects.isGetClassMethod(readMethod)) {
					continue;
				}
				if (readMethod != null) {
					String lambdaName = "lambda$getters$" + (idx++);

					MethodVisitor mv = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
						lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitTypeInsn(CHECKCAST, beanTypeDesc);
					mv.visitMethodInsn(INVOKEVIRTUAL, beanTypeDesc, readMethod.getName(), org.objectweb.asm.Type.getMethodDescriptor(readMethod), false);
					org.objectweb.asm.Type paramType = org.objectweb.asm.Type.getType(pd.getPropertyType());
					switch (paramType.getSort()) {
						case org.objectweb.asm.Type.BOOLEAN:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
							break;
						case org.objectweb.asm.Type.BYTE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
							break;
						case org.objectweb.asm.Type.CHAR:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
							break;
						case org.objectweb.asm.Type.SHORT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
							break;
						case org.objectweb.asm.Type.INT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
							break;
						case org.objectweb.asm.Type.FLOAT:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
							break;
						case org.objectweb.asm.Type.LONG:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
							break;
						case org.objectweb.asm.Type.DOUBLE:
							mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
							break;
						case org.objectweb.asm.Type.ARRAY:
						case org.objectweb.asm.Type.OBJECT:
							break;
						default:
					}
					mv.visitInsn(ARETURN);
					mv.visitMaxs(1, 1);
					mv.visitEnd();
				}
			}

		}
		// setters method
		{
			int idx = 0;
			for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
				String key = pd.getName();
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod != null) {
					String lambdaName = "lambda$setters$" + (idx++);

					MethodVisitor mv = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC,
						lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
					mv.visitCode();
					mv.visitVarInsn(ALOAD, 0);
					mv.visitTypeInsn(CHECKCAST, beanTypeDesc);
					mv.visitVarInsn(ALOAD, 1);
					org.objectweb.asm.Type paramType = org.objectweb.asm.Type.getType(pd.getPropertyType());
					switch (paramType.getSort()) {
						case org.objectweb.asm.Type.BOOLEAN:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
							break;
						case org.objectweb.asm.Type.BYTE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
							break;
						case org.objectweb.asm.Type.CHAR:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
							break;
						case org.objectweb.asm.Type.SHORT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
							break;
						case org.objectweb.asm.Type.INT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
							break;
						case org.objectweb.asm.Type.FLOAT:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
							break;
						case org.objectweb.asm.Type.LONG:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
							break;
						case org.objectweb.asm.Type.DOUBLE:
							mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
							mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
							break;
						case org.objectweb.asm.Type.ARRAY:
							mv.visitTypeInsn(CHECKCAST, paramType.getDescriptor());
							break;
						case org.objectweb.asm.Type.OBJECT:
						default:
							mv.visitTypeInsn(CHECKCAST, paramType.getInternalName());
					}
					mv.visitMethodInsn(INVOKEVIRTUAL, beanTypeDesc, writeMethod.getName(), org.objectweb.asm.Type.getMethodDescriptor(writeMethod), false);
					mv.visitInsn(RETURN);
					mv.visitMaxs(2, 2);
					mv.visitEnd();
				}
			}
		}

		classWriter.visitEnd();
		byte[] byteArray = classWriter.toByteArray();
		classes.put(javaClassName, byteArray);
		return Tuples.of(javaClassName, classes);
	}

	static String toTypeSignature(Type type) {
		String rs;
		if (type instanceof Class) {
			if (((Class<?>) type).isPrimitive()) {
				rs = org.objectweb.asm.Type.getDescriptor(Types.getWrapperClass((Class<?>) type));
			} else {
				rs = org.objectweb.asm.Type.getDescriptor((Class<?>) type);
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			StringBuilder sb = new StringBuilder();
			sb.append("L").append(parameterizedType.getRawType().getTypeName().replace('.', '/'));
			sb.append("<");
			Type[] args = parameterizedType.getActualTypeArguments();
			for (int i = 0; i < args.length; i++) {
				sb.append(toTypeSignature(args[i]));
			}
			sb.append(">;");
			rs = sb.toString();
		} else if (type instanceof WildcardType) {
			WildcardType wildcardType = (WildcardType) type;
			Type[] bounds = wildcardType.getLowerBounds();
			if (bounds.length > 0) {
				return "-" + toTypeSignature(bounds[0]);
			} else {
				Type[] upperBounds = wildcardType.getUpperBounds();
				if (upperBounds.length > 0) {
					return "+" + toTypeSignature(upperBounds[0]);
				} else {
					return toTypeSignature(Object.class);
				}
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			Type componentType = genericArrayType.getGenericComponentType();
			rs = "L" + toTypeSignature(componentType);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + type);
		}
		return rs;
	}


	static byte[] buildInnerClass(String outerName, String innerSimpleName, Type type) {

		String typeRefClassName = TypeRef.class.getName().replace('.', '/');

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		classWriter.visit(V1_8, ACC_PUBLIC + ACC_SUPER, outerName + "$" + innerSimpleName,
			"L" + typeRefClassName + "<" + toTypeSignature(type) + ">;", typeRefClassName,
			null);
		classWriter.visitInnerClass(outerName + "$" + innerSimpleName, outerName, innerSimpleName, ACC_PUBLIC);
		{
			FieldVisitor fieldVisitor = classWriter.visitField(ACC_FINAL | ACC_SYNTHETIC, "this$0", "L" + outerName + ";", null, null);
			fieldVisitor.visitEnd();
		}
		{
			MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(L" + outerName + ";)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitFieldInsn(PUTFIELD, outerName + "$" + innerSimpleName, "this$0", "L" + outerName + ";");
			methodVisitor.visitVarInsn(ALOAD, 0);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, typeRefClassName, "<init>", "()V", false);
			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(2, 2);
			methodVisitor.visitEnd();
		}
		classWriter.visitEnd();
		return classWriter.toByteArray();
	}


	static <T> Class<BeanMetadata> buildByJdkTool(Class<T> beanType) {
		try {
			String javaPackageName = beanType.getPackage().getName();
			if (javaPackageName.startsWith("java.")) {
				javaPackageName = "javax." + javaPackageName.substring("java.".length());
			} else if (javaPackageName.equals("java")) {
				javaPackageName = "javax";
			}
			ClassName className = ClassName.get(javaPackageName,
				beanType.getSimpleName() + $$_BEAN_METADATA_$$ + seq.incrementAndGet());
			TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
				.addModifiers(javax.lang.model.element.Modifier.PUBLIC)
				.addSuperinterface(ClassName.get(BeanMetadata.class))
				.addMethod(
					MethodSpec.constructorBuilder().addModifiers(javax.lang.model.element.Modifier.PUBLIC)
						.build()
				);

			{
				ParameterizedTypeName gettersType = ParameterizedTypeName.get(ClassName.get(Map.class),
					ClassName.get(String.class),
					ParameterizedTypeName.get(ClassName.get(Function.class),
						ClassName.get(Object.class),
						ClassName.get(Object.class)
					)
				);
				ParameterizedTypeName settersType = ParameterizedTypeName.get(ClassName.get(Map.class),
					ClassName.get(String.class),
					ParameterizedTypeName.get(ClassName.get(BiConsumer.class),
						ClassName.get(Object.class),
						ClassName.get(Object.class)
					)
				);
				ParameterizedTypeName typesType = ParameterizedTypeName.get(ClassName.get(Map.class),
					ClassName.get(String.class),
					ClassName.get(Type.class)
				);
				// Map<String, Function<Object, Object>> getters();
				MethodSpec.Builder gettersMethodBuilder = MethodSpec.methodBuilder("getters")
					.returns(gettersType)
					.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
				// Map<String, BiConsumer<Object, Object>> setters();
				MethodSpec.Builder settersMethodBuilder = MethodSpec.methodBuilder("setters")
					.returns(settersType)
					.addModifiers(javax.lang.model.element.Modifier.PUBLIC);
				// Map<String, Type> types();
				MethodSpec.Builder typesMethodBuilder = MethodSpec.methodBuilder("types")
					.returns(typesType)
					.addModifiers(Modifier.PUBLIC);

				BeanInfo beanInfo = Introspector.getBeanInfo(beanType);
				CodeBlock.Builder getterBuilder = CodeBlock.builder().addStatement("$T getters = new $T()",
					gettersType, ClassName.get(LinkedHashMap.class));
				CodeBlock.Builder setterBuilder = CodeBlock.builder().addStatement("$T setters = new $T()",
					settersType, ClassName.get(LinkedHashMap.class));
				CodeBlock.Builder typesBuilder = CodeBlock.builder().addStatement("$T types = new $T()",
					typesType, ClassName.get(LinkedHashMap.class));
				for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
					String key = pd.getName();
					Method writeMethod = pd.getWriteMethod();
					Method readMethod = pd.getReadMethod();
					if (Reflects.isGetClassMethod(readMethod)) {
						continue;
					}

					if (writeMethod != null) {
						CodeBlock.Builder builder = CodeBlock.builder()
							.beginControlFlow("");
						Type type = writeMethod.getGenericParameterTypes()[0];
						builder.addStatement("$T setter = (bean, o)-> (($T)bean).$L(($T)o)",
								ParameterizedTypeName.get(ClassName.get(BiConsumer.class),
									ClassName.get(Object.class),
									ClassName.get(Object.class)
								),
								TypeName.get(beanType),
								writeMethod.getName(), TypeName.get(type).box()
							)
							.addStatement("setters.put($S,setter)", key);
						setterBuilder.add(builder.endControlFlow().build());

						typesBuilder.addStatement("types.put($S, new $T<$T>(){}.getType())",
							key, ClassName.get(TypeRef.class), TypeName.get(type).box());
					}
					if (readMethod != null) {
						CodeBlock.Builder builder = CodeBlock.builder()
							.beginControlFlow("");
						builder.addStatement("$T getter = (bean) -> (($T)bean).$L()",
								ParameterizedTypeName.get(ClassName.get(Function.class),
									ClassName.get(Object.class),
									ClassName.get(Object.class)
								),
								TypeName.get(beanType),
								readMethod.getName()
							)
							.addStatement("getters.put($S,getter)", key);
						getterBuilder.add(builder.endControlFlow().build());
					}
				}

				typesMethodBuilder.addCode(typesBuilder.addStatement("return types").build());
				gettersMethodBuilder.addCode(getterBuilder.addStatement("return getters").build());
				settersMethodBuilder.addCode(setterBuilder.addStatement("return setters").build());
				classBuilder.addMethod(typesMethodBuilder.build());
				classBuilder.addMethod(gettersMethodBuilder.build());
				classBuilder.addMethod(settersMethodBuilder.build());
			}
			JavaFile javaFile = JavaFile.builder(className.packageName(), classBuilder.build()).build();
			StringBuilder sb = new StringBuilder();
			javaFile.writeTo(sb);
			///System.out.println(sb.toString());
			Class clazz = MemoryCompiler.getInstance(beanType.getClassLoader()).compile(className.canonicalName(), sb.toString());
			return clazz;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
