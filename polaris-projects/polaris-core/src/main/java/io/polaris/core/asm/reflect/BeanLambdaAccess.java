package io.polaris.core.asm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.polaris.core.asm.AsmUtils;
import io.polaris.core.err.InvocationException;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.reflect.SerializableFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * @author Qt
 * @since 1.8,  Apr 11, 2024
 */
public abstract class BeanLambdaAccess<T> {
	private static final AccessPool<Class, BeanLambdaAccess> pool = new AccessPool<>();
	private static ILogger log = ILoggers.of(BeanLambdaAccess.class);


	private Map<String, BeanPropertyInfo> properties;
	private Map<String, Function<Object, Object>> propertyGetters;
	private Map<String, BiConsumer<Object, Object>> propertySetters;
	private Map<String, Function<Object, Object>> fieldGetters;
	private Map<String, BiConsumer<Object, Object>> fieldSetters;
	private Map<String, Function<Object, Object>> staticFieldGetters;
	private Map<String, BiConsumer<Object, Object>> staticFieldSetters;

	protected BeanLambdaAccess() {
		propertyGetters = buildPropertyGetters();
		propertySetters = buildPropertySetters();
		fieldGetters = buildFieldGetters();
		fieldSetters = buildFieldSetters();
		staticFieldGetters = buildStaticFieldGetters();
		staticFieldSetters = buildStaticFieldSetters();
	}


	// region to be inherited

	protected Map<String, Function<Object, Object>> buildPropertyGetters() {
		return Collections.emptyMap();
	}

	protected Map<String, BiConsumer<Object, Object>> buildPropertySetters() {
		return Collections.emptyMap();
	}

	protected Map<String, Function<Object, Object>> buildFieldGetters() {
		return Collections.emptyMap();
	}

	protected Map<String, BiConsumer<Object, Object>> buildFieldSetters() {
		return Collections.emptyMap();
	}

	protected Map<String, Function<Object, Object>> buildStaticFieldGetters() {
		return Collections.emptyMap();
	}

	protected Map<String, BiConsumer<Object, Object>> buildStaticFieldSetters() {
		return Collections.emptyMap();
	}

	// endregion

	// region info


	public Map<String, BeanPropertyInfo> properties() {
		return Collections.unmodifiableMap(properties);
	}

	public boolean containsAny(String property) {
		return properties.containsKey(property);
	}

	public Set<String> allPropertyNames() {
		return Collections.unmodifiableSet(properties.keySet());
	}

	public java.lang.reflect.Type propertyGenericType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyGenericType();
	}

	public java.lang.reflect.Type propertyType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyType();
	}

	// endregion

	// region setter

	public boolean containsSetter(String property) {
		return propertySetters.containsKey(property);
	}

	public Set<String> setterPropertyNames() {
		return Collections.unmodifiableSet(propertySetters.keySet());
	}

	public BiConsumer<Object, Object> getSetter(String property) {
		return propertySetters.get(property);
	}

	public void setProperty(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getSetter(property);
		if (consumer == null) {
			throw new IllegalArgumentException("Method not found");
		}
		consumer.accept(o, val);
	}


	public void setPropertyOrNoop(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getSetter(property);
		if (consumer == null) {
			return;
		}
		consumer.accept(o, val);
	}

	// endregion

	// region getter

	public boolean containsGetter(String property) {
		return propertyGetters.containsKey(property);
	}

	public Set<String> getterPropertyNames() {
		return Collections.unmodifiableSet(propertyGetters.keySet());
	}


	public Function<Object, Object> getGetter(String property) {
		return propertyGetters.get(property);
	}

	public Object getProperty(Object o, String property) {
		Function<Object, Object> function = getGetter(property);
		if (function == null) {
			throw new IllegalArgumentException("Method not found");
		}
		return function.apply(o);
	}

	public Object getPropertyOrNoop(Object o, String property) {
		Function<Object, Object> function = getGetter(property);
		if (function == null) {
			return null;
		}
		return function.apply(o);
	}

	// endregion

	// region normal field

	public boolean containsField(String property) {
		return fieldGetters.containsKey(property);
	}

	public Set<String> fieldNames() {
		return Collections.unmodifiableSet(fieldGetters.keySet());
	}

	public BiConsumer<Object, Object> getFieldSetter(String property) {
		return fieldSetters.get(property);
	}

	public Function<Object, Object> getFieldGetter(String property) {
		return fieldGetters.get(property);
	}

	public void setField(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getFieldSetter(property);
		if (consumer == null) {
			throw new IllegalArgumentException("Field not found");
		}
		consumer.accept(o, val);
	}

	public Object getField(Object o, String property) {
		Function<Object, Object> function = getFieldGetter(property);
		if (function == null) {
			throw new IllegalArgumentException("Field not found");
		}
		return function.apply(o);
	}

	public void setFieldOrNoop(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getFieldSetter(property);
		if (consumer == null) {
			return;
		}
		consumer.accept(o, val);
	}

	public Object getFieldOrNoop(Object o, String property) {
		Function<Object, Object> function = getFieldGetter(property);
		if (function == null) {
			return null;
		}
		return function.apply(o);
	}

	// endregion

	// region static field

	public boolean containsStaticField(String property) {
		return staticFieldGetters.containsKey(property);
	}

	public Set<String> staticFieldNames() {
		return Collections.unmodifiableSet(staticFieldGetters.keySet());
	}

	public BiConsumer<Object, Object> getStaticFieldSetter(String property) {
		return staticFieldSetters.get(property);
	}

	public Function<Object, Object> getStaticFieldGetter(String property) {
		return staticFieldGetters.get(property);
	}

	public void setStaticField(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getStaticFieldSetter(property);
		if (consumer == null) {
			throw new IllegalArgumentException("Field not found");
		}
		consumer.accept(o, val);
	}

	public Object getStaticField(Object o, String property) {
		Function<Object, Object> function = getStaticFieldGetter(property);
		if (function == null) {
			throw new IllegalArgumentException("Field not found");
		}
		return function.apply(o);
	}

	public void setStaticFieldOrNoop(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getStaticFieldSetter(property);
		if (consumer == null) {
			return;
		}
		consumer.accept(o, val);
	}

	public Object getStaticFieldOrNoop(Object o, String property) {
		Function<Object, Object> function = getStaticFieldGetter(property);
		if (function == null) {
			return null;
		}
		return function.apply(o);
	}

	// endregion


	public static <T> BeanLambdaAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, BeanLambdaAccess::create);
	}

	public static <T> BeanLambdaAccess<T> create(Class<T> type) {
		Map<String, BeanPropertyInfo> propertyInfoMap = BeanPropertyInfo.mapOf(type);
		List<BeanPropertyInfo> setters = new ArrayList<>();
		List<BeanPropertyInfo> getters = new ArrayList<>();
		List<BeanPropertyInfo> fields = new ArrayList<>();
		List<BeanPropertyInfo> staticFields = new ArrayList<>();

		for (Map.Entry<String, BeanPropertyInfo> entry : propertyInfoMap.entrySet()) {
			BeanPropertyInfo beanPropertyInfo = entry.getValue();
			if (beanPropertyInfo.getWriteMethod() != null) {
				setters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getReadMethod() != null) {
				getters.add(beanPropertyInfo);
			}
			if (beanPropertyInfo.getField() != null) {
				if (Modifier.isStatic(beanPropertyInfo.getField().getModifiers())) {
					staticFields.add(beanPropertyInfo);
				} else {
					fields.add(beanPropertyInfo);
				}
			}
		}

		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanLambdaAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type, setters, getters, fields, staticFields);
			}
		}
		BeanLambdaAccess<T> access;
		try {
			access = (BeanLambdaAccess<T>) accessClass.newInstance();
			access.properties = propertyInfoMap;
		} catch (Throwable t) {
			throw new IllegalStateException("实例化构造器访问类失败: " + accessClassName, t);
		}
		return access;
	}

	private static <T> Class buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, List<BeanPropertyInfo> setters, List<BeanPropertyInfo> getters
		, List<BeanPropertyInfo> fields, List<BeanPropertyInfo> staticFields) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		String classNameInternal = type.getName().replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superclassNameInternal = BeanLambdaAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superclassNameInternal + "<L" + accessClassNameInternal + ";>;",
			superclassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		insertSelfConstructor(cw, superclassNameInternal);
		insertSetterInvokers(cw, accessClassNameInternal, type, setters);
		insertGetterInvokers(cw, accessClassNameInternal, type, getters);
		insertFieldSetterInvokers(cw, accessClassNameInternal, type, fields);
		insertFieldGetterInvokers(cw, accessClassNameInternal, type, fields);
		insertStaticFieldSetterInvokers(cw, accessClassNameInternal, type, staticFields);
		insertStaticFieldGetterInvokers(cw, accessClassNameInternal, type, staticFields);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}

	private static void insertSelfConstructor(ClassWriter cw, String superclassNameInternal) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, superclassNameInternal, "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}

	private static <T> void insertSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> setters) {
		// ignore
		if (setters.isEmpty()) {
			return;
		}

		SerializableFunction<BeanLambdaAccess, Map<String, BiConsumer<Object, Object>>> buildPropertySetters = BeanLambdaAccess::buildPropertySetters;
		final String lambdaPrefixOfMethods = "lambda$" + buildPropertySetters.serialized().getImplMethodName() + "$";

		// 生成lambda方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildPropertySetters.serialized().getImplMethodName()
				, "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/BiConsumer<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);

			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);

			for (int i = 0; i < setters.size(); i++) {
				BeanPropertyInfo info = setters.get(i);
				Method method = info.getWriteMethod();
				String name = info.getPropertyName();
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;

				// lambda var
				methodVisitor.visitInvokeDynamicInsn("accept", "()Ljava/util/function/BiConsumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
						, new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", false)
						, Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
					});
				methodVisitor.visitVarInsn(ASTORE, 2);

				// put(name,...)
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(name);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();

		}

		// 生成lambda方法实现
		{
			for (int i = 0; i < setters.size(); i++) {
				BeanPropertyInfo info = setters.get(i);
				Method method = info.getWriteMethod();
				String name = info.getPropertyName();
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;

				boolean hasThrows = method.getExceptionTypes().length > 0;
				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
				mv.visitCode();
				Label labelStart = new Label();
				Label labelEnd = new Label();
				Label labelCatch = new Label();
				if (hasThrows) {
					mv.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
					mv.visitLabel(labelStart);
				}

				mv.visitVarInsn(ALOAD, 0);
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(method.getModifiers())) {
					mv.visitInsn(POP);
				}

				mv.visitVarInsn(ALOAD, 1);
				Class<?> parameterType = parameterTypes[0];
				Type paramType = Type.getType(parameterType);
				AsmUtils.autoUnBoxing(mv, paramType);

				Class<?> declaringClass = method.getDeclaringClass();
				boolean isInterface = declaringClass.isInterface();
				int invokeOpcode;
				if (isInterface) {
					invokeOpcode = INVOKEINTERFACE;
				} else if (Modifier.isStatic(method.getModifiers())) {
					invokeOpcode = INVOKESTATIC;
				} else {
					invokeOpcode = INVOKEVIRTUAL;
				}

				mv.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
				Class<?> returnType = method.getReturnType();
				AsmUtils.autoBoxing(mv, returnType);

				if (hasThrows) {
					mv.visitLabel(labelEnd);
				}
				mv.visitInsn(RETURN);
				if (hasThrows) {
					mv.visitLabel(labelCatch);
					mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
					mv.visitVarInsn(ASTORE, 2);
					mv.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
					mv.visitInsn(ATHROW);
				}

				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
		}
	}

	private static <T> void insertGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> getters) {
		// ignore
		if (getters.isEmpty()) {
			return;
		}
		SerializableFunction<BeanLambdaAccess, Map<String, Function<Object, Object>>> buildPropertyGetters = BeanLambdaAccess::buildPropertyGetters;
		final String lambdaPrefixOfMethods = "lambda$" + buildPropertyGetters.serialized().getImplMethodName() + "$";

		// 生成lambda方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, buildPropertyGetters.serialized().getImplMethodName()
				, "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/Function<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);

			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);


			for (int i = 0; i < getters.size(); i++) {
				BeanPropertyInfo info = getters.get(i);
				Method method = info.getReadMethod();
				String name = info.getPropertyName();
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;

				// lambda var
				methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")});
				methodVisitor.visitVarInsn(ASTORE, 2);

				// put(name,...)
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(name);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
			}

			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成lambda方法实现
		{
			for (int i = 0; i < getters.size(); i++) {
				BeanPropertyInfo info = getters.get(i);
				Method method = info.getReadMethod();
				String name = info.getPropertyName();
				String methodName = method.getName();
				Class<?>[] parameterTypes = method.getParameterTypes();
				String lambdaName = lambdaPrefixOfMethods + methodName + "$" + i;

				boolean hasThrows = method.getExceptionTypes().length > 0;
				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);

				mv.visitCode();
				Label labelStart = new Label();
				Label labelEnd = new Label();
				Label labelCatch = new Label();
				if (hasThrows) {
					mv.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
					mv.visitLabel(labelStart);
				}

				mv.visitVarInsn(ALOAD, 0);
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(method.getModifiers())) {
					mv.visitInsn(POP);
				}

				Class<?> declaringClass = method.getDeclaringClass();
				boolean isInterface = declaringClass.isInterface();
				int invokeOpcode;
				if (isInterface) {
					invokeOpcode = INVOKEINTERFACE;
				} else if (Modifier.isStatic(method.getModifiers())) {
					invokeOpcode = INVOKESTATIC;
				} else {
					invokeOpcode = INVOKEVIRTUAL;
				}

				mv.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
				Class<?> returnType = method.getReturnType();
				AsmUtils.autoBoxingForReturn(mv, returnType);

				if (hasThrows) {
					mv.visitLabel(labelEnd);
				}
				mv.visitInsn(ARETURN);
				if (hasThrows) {
					mv.visitLabel(labelCatch);
					mv.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
					mv.visitVarInsn(ASTORE, 2);
					mv.visitTypeInsn(NEW, Type.getInternalName(InvocationException.class));
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(InvocationException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
					mv.visitInsn(ATHROW);
				}
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
		}

	}

	private static <T> void insertFieldSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}

		SerializableFunction<BeanLambdaAccess, Map<String, BiConsumer<Object, Object>>> buildFieldSetters = BeanLambdaAccess::buildFieldSetters;

		_insertFieldSetterInvokers(cw, accessClassNameInternal, type, fields, buildFieldSetters);
	}

	private static <T> void insertFieldGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}
		SerializableFunction<BeanLambdaAccess, Map<String, Function<Object, Object>>> buildFieldGetters = BeanLambdaAccess::buildFieldGetters;
		_insertFieldGetterInvokers(cw, accessClassNameInternal, type, fields, buildFieldGetters);
	}

	private static <T> void insertStaticFieldSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> staticFields) {
		// ignore
		if (staticFields.isEmpty()) {
			return;
		}
		SerializableFunction<BeanLambdaAccess, Map<String, BiConsumer<Object, Object>>> buildStaticFieldSetters = BeanLambdaAccess::buildStaticFieldSetters;

		_insertFieldSetterInvokers(cw, accessClassNameInternal, type, staticFields, buildStaticFieldSetters);
	}

	private static <T> void insertStaticFieldGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> staticFields) {
		// ignore
		if (staticFields.isEmpty()) {
			return;
		}
		SerializableFunction<BeanLambdaAccess, Map<String, Function<Object, Object>>> buildStaticFieldGetters = BeanLambdaAccess::buildStaticFieldGetters;

		_insertFieldGetterInvokers(cw, accessClassNameInternal, type, staticFields, buildStaticFieldGetters);
	}

	private static <T> void _insertFieldSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields, SerializableFunction<BeanLambdaAccess, Map<String, BiConsumer<Object, Object>>> func) {
		final String lambdaPrefixOfMethods = "lambda$" + func.serialized().getImplMethodName() + "$";
		// 生成lambda方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, func.serialized().getImplMethodName(), "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/BiConsumer<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);

			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);
			for (int i = 0; i < fields.size(); i++) {
				BeanPropertyInfo info = fields.get(i);
				Field field = info.getField();
				String name = field.getName();
				Class<?> fieldType = field.getType();
				String lambdaName = lambdaPrefixOfMethods + name + "$" + i;

				// lambda var
				methodVisitor.visitInvokeDynamicInsn("accept", "()Ljava/util/function/BiConsumer;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{
						Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
						, new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", false)
						, Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)V")
					});
				methodVisitor.visitVarInsn(ASTORE, 2);

				// put(name,...)
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(name);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成lambda方法实现
		{
			for (int i = 0; i < fields.size(); i++) {
				BeanPropertyInfo info = fields.get(i);
				Field field = info.getField();
				String name = field.getName();
				String lambdaName = lambdaPrefixOfMethods + name + "$" + i;

				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, lambdaName, "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					mv.visitInsn(POP);
				}
				mv.visitVarInsn(ALOAD, 1);
				Type fieldType = Type.getType(field.getType());
				AsmUtils.autoUnBoxing(mv, fieldType);
				if (Modifier.isStatic(field.getModifiers())) {
					mv.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), name, fieldType.getDescriptor());
				} else {
					mv.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), name, fieldType.getDescriptor());
				}
				mv.visitInsn(RETURN);
				mv.visitMaxs(2, 2);
				mv.visitEnd();
			}
		}
	}

	private static <T> void _insertFieldGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields, SerializableFunction<BeanLambdaAccess, Map<String, Function<Object, Object>>> func) {
		final String lambdaPrefixOfMethods = "lambda$" + func.serialized().getImplMethodName() + "$";

		// 生成lambda方法列表
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, func.serialized().getImplMethodName()
				, "()Ljava/util/Map;", "()Ljava/util/Map<Ljava/lang/String;Ljava/util/function/Function<Ljava/lang/Object;Ljava/lang/Object;>;>;", null);

			methodVisitor.visitCode();
			methodVisitor.visitTypeInsn(NEW, "java/util/HashMap");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashMap", "<init>", "()V", false);
			methodVisitor.visitVarInsn(ASTORE, 1);
			for (int i = 0; i < fields.size(); i++) {
				BeanPropertyInfo info = fields.get(i);
				Field field = info.getField();
				String name = field.getName();
				String lambdaName = lambdaPrefixOfMethods + name + "$" + i;

				// lambda var
				methodVisitor.visitInvokeDynamicInsn("apply", "()Ljava/util/function/Function;", new Handle(Opcodes.H_INVOKESTATIC, "java/lang/invoke/LambdaMetafactory", "metafactory", "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;", false)
					, new Object[]{Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;"), new Handle(Opcodes.H_INVOKESTATIC, accessClassNameInternal, lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", false), Type.getType("(Ljava/lang/Object;)Ljava/lang/Object;")});
				methodVisitor.visitVarInsn(ASTORE, 2);
				// put(name,...)
				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitLdcInsn(name);
				methodVisitor.visitVarInsn(ALOAD, 2);
				methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
				methodVisitor.visitInsn(POP);
			}
			methodVisitor.visitVarInsn(ALOAD, 1);
			methodVisitor.visitInsn(ARETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}

		// 生成lambda方法实现
		{
			for (int i = 0; i < fields.size(); i++) {
				BeanPropertyInfo info = fields.get(i);
				Field field = info.getField();
				String name = field.getName();
				String lambdaName = lambdaPrefixOfMethods + name + "$" + i;

				MethodVisitor mv = cw.visitMethod(ACC_PRIVATE | ACC_STATIC | ACC_SYNTHETIC, lambdaName, "(Ljava/lang/Object;)Ljava/lang/Object;", null, null);
				mv.visitCode();
				mv.visitVarInsn(ALOAD, 0);
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					mv.visitInsn(POP);
					mv.visitFieldInsn(GETSTATIC, Type.getInternalName(field.getDeclaringClass()), name, Type.getDescriptor(field.getType()));
				} else {
					mv.visitFieldInsn(GETFIELD, Type.getInternalName(type), name, Type.getDescriptor(field.getType()));
				}
				Type fieldType = Type.getType(field.getType());
				AsmUtils.autoBoxing(mv, fieldType);
				mv.visitInsn(ARETURN);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
		}
	}


}
