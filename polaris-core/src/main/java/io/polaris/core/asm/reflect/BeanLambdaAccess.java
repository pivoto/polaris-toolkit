package io.polaris.core.asm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.SerializableFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * ASM Bean Lambda 访问器
 * <p>基于 Lambda 表达式的 ASM Bean 访问器，提供函数式接口访问方式</p>
 *
 * @author Qt
 * @since Apr 11, 2024
 * @param <T> Bean 类型
 */
@SuppressWarnings("rawtypes")
public abstract class BeanLambdaAccess<T> {
	private static final Logger log = Loggers.of(BeanLambdaAccess.class);
	/**
	 * Lambda 访问器缓存池
	 */
	private static final AccessClassPool<Class, BeanLambdaAccess> pool = new AccessClassPool<>();


	/**
	 * 属性信息映射表
	 */
	private Map<String, BeanPropertyInfo> properties;
	/**
	 * 属性 Getter 函数映射表
	 */
	private Map<String, Function<Object, Object>> propertyGetters;
	/**
	 * 属性 Setter 消费者映射表
	 */
	private Map<String, BiConsumer<Object, Object>> propertySetters;
	/**
	 * 字段 Getter 函数映射表
	 */
	private Map<String, Function<Object, Object>> fieldGetters;
	/**
	 * 字段 Setter 消费者映射表
	 */
	private Map<String, BiConsumer<Object, Object>> fieldSetters;

	/**
	 * 默认构造函数
	 * <p>初始化所有只读属性映射表</p>
	 */
	protected BeanLambdaAccess() {
		// 全部为只读属性
		propertyGetters = Collections.unmodifiableMap(buildPropertyGetters());
		propertySetters = Collections.unmodifiableMap(buildPropertySetters());
		fieldGetters = Collections.unmodifiableMap(buildFieldGetters());
		fieldSetters = Collections.unmodifiableMap(buildFieldSetters());
	}


	// region to be inherited

	/**
	 * 构建属性 Getter 映射表（子类实现）
	 *
	 * @return Getter 函数映射表
	 */
	protected Map<String, Function<Object, Object>> buildPropertyGetters() {
		return Collections.emptyMap();
	}

	/**
	 * 构建属性 Setter 映射表（子类实现）
	 *
	 * @return Setter 消费者映射表
	 */
	protected Map<String, BiConsumer<Object, Object>> buildPropertySetters() {
		return Collections.emptyMap();
	}

	/**
	 * 构建字段 Getter 映射表（子类实现）
	 *
	 * @return Getter 函数映射表
	 */
	protected Map<String, Function<Object, Object>> buildFieldGetters() {
		return Collections.emptyMap();
	}

	/**
	 * 构建字段 Setter 映射表（子类实现）
	 *
	 * @return Setter 消费者映射表
	 */
	protected Map<String, BiConsumer<Object, Object>> buildFieldSetters() {
		return Collections.emptyMap();
	}

	// endregion

	// region info


	public Map<String, BeanPropertyInfo> properties() {
		return properties;
	}

	/**
	 * 判断是否包含指定属性
	 *
	 * @param property 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsAny(String property) {
		return properties.containsKey(property);
	}

	/**
	 * 获取所有属性名称集合
	 *
	 * @return 属性名称集合
	 */
	public Set<String> allPropertyNames() {
		return properties.keySet();
	}

	/**
	 * 获取属性的泛型类型
	 *
	 * @param property 属性名
	 * @return 属性的泛型类型，如果属性不存在返回 null
	 */
	public java.lang.reflect.Type propertyGenericType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyGenericType();
	}

	/**
	 * 获取属性的原始类型
	 *
	 * @param property 属性名
	 * @return 属性的 Class 类型，如果属性不存在返回 null
	 */
	public java.lang.reflect.Type propertyType(String property) {
		BeanPropertyInfo info = this.properties.get(property);
		return info == null ? null : info.getPropertyType();
	}

	// endregion



	// region property or field

	/**
	 * 判断是否包含指定属性的 setter 或字段
	 *
	 * @param key 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsSetterOrField(String key) {
		BeanPropertyInfo info = properties.get(key);
		if (info != null) {
			return info.getField() != null || info.getWriteMethod() != null;
		}
		return false;
	}

	/**
	 * 判断是否包含指定属性的 getter 或字段
	 *
	 * @param key 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsGetterOrField(String key) {
		BeanPropertyInfo info = properties.get(key);
		if (info != null) {
			return info.getField() != null || info.getReadMethod() != null;
		}
		return false;
	}

	/**
	 * 设置属性或字段的值
	 * <p>优先使用 setter 方法，其次使用字段访问</p>
	 *
	 * @param o 目标对象
	 * @param key 属性名
	 * @param val 新值
	 * @return 如果成功设置返回 true，否则返回 false
	 */
	public boolean setPropertyOrField(Object o, String key, Object val){
		BiConsumer<Object, Object> setter = getSetter(key);
		if (setter != null) {
			setter.accept(o,val);
			return true;
		}
		setter = getFieldSetter(key);
		if (setter != null) {
			setter.accept(o,val);
			return true;
		}
		return false;
	}

	/**
	 * 设置属性或字段的值（带类型转换）
	 * <p>使用提供的转换器进行类型转换后设置</p>
	 *
	 * @param o 目标对象
	 * @param key 属性名
	 * @param val 新值
	 * @param converter 类型转换器
	 * @return 如果成功设置返回 true，否则返回 false
	 */
	public boolean setPropertyOrField(Object o, String key, Object val, BiFunction<java.lang.reflect.Type, Object, Object> converter){
		BeanPropertyInfo info = this.properties.get(key);
		if (info == null) {
			return false;
		}
		java.lang.reflect.Type type = info.getPropertyGenericType();
		if (info.getField() != null) {
			setField(o,key,converter.apply(type,val));
			return true;
		}
		if (info.getWriteMethod() != null) {
			setProperty(o,key,converter.apply(type,val));
			return true;
		}
		return false;
	}

	// endregion

	// region setter

	/**
	 * 判断是否包含指定属性的 setter 方法
	 *
	 * @param property 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsSetter(String property) {
		return propertySetters.containsKey(property);
	}

	/**
	 * 获取所有 Setter 方法的消费者映射表
	 *
	 * @return Setter 消费者映射表
	 */
	public Map<String, BiConsumer<Object, Object>> propertySetters() {
		return propertySetters;
	}

	/**
	 * 获取所有可设置属性的名称集合
	 *
	 * @return 属性名称集合
	 */
	public Set<String> setterPropertyNames() {
		return propertySetters.keySet();
	}

	/**
	 * 获取指定属性的 Setter 函数
	 *
	 * @param property 属性名
	 * @return Setter 消费者，如果不存在返回 null
	 */
	public BiConsumer<Object, Object> getSetter(String property) {
		return propertySetters.get(property);
	}

	/**
	 * 设置属性值
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @param val 新值
	 * @throws IllegalArgumentException 当 setter 方法不存在时抛出
	 */
	public void setProperty(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getSetter(property);
		if (consumer == null) {
			throw new IllegalArgumentException("Method not found");
		}
		consumer.accept(o, val);
	}


	/**
	 * 设置属性值（如果不存在则忽略）
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @param val 新值
	 */
	public void setPropertyOrNoop(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getSetter(property);
		if (consumer == null) {
			return;
		}
		consumer.accept(o, val);
	}

	// endregion

	// region getter

	/**
	 * 判断是否包含指定属性的 getter 方法
	 *
	 * @param property 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsGetter(String property) {
		return propertyGetters.containsKey(property);
	}

	/**
	 * 获取所有 Getter 函数的映射表
	 *
	 * @return Getter 函数映射表
	 */
	public Map<String, Function<Object, Object>> propertyGetters() {
		return propertyGetters;
	}

	/**
	 * 获取所有可读取属性的名称集合
	 *
	 * @return 属性名称集合
	 */
	public Set<String> getterPropertyNames() {
		return propertyGetters.keySet();
	}

	/**
	 * 获取指定属性的 Getter 函数
	 *
	 * @param property 属性名
	 * @return Getter 函数，如果不存在返回 null
	 */
	public Function<Object, Object> getGetter(String property) {
		return propertyGetters.get(property);
	}

	/**
	 * 获取属性值
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @return 属性值
	 * @throws IllegalArgumentException 当 getter 方法不存在时抛出
	 */
	public Object getProperty(Object o, String property) {
		Function<Object, Object> function = getGetter(property);
		if (function == null) {
			throw new IllegalArgumentException("Method not found");
		}
		return function.apply(o);
	}

	/**
	 * 获取属性值（如果不存在则返回 null）
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @return 属性值，如果不存在返回 null
	 */
	public Object getPropertyOrNoop(Object o, String property) {
		Function<Object, Object> function = getGetter(property);
		if (function == null) {
			return null;
		}
		return function.apply(o);
	}

	// endregion

	// region field

	/**
	 * 判断是否包含指定字段
	 *
	 * @param property 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsField(String property) {
		return fieldGetters.containsKey(property);
	}

	/**
	 * 获取所有字段 Getter 函数的映射表
	 *
	 * @return Getter 函数映射表
	 */
	public Map<String, Function<Object, Object>> fieldGetters() {
		return fieldGetters;
	}

	/**
	 * 获取所有字段 Setter 消费者的映射表
	 *
	 * @return Setter 消费者映射表
	 */
	public Map<String, BiConsumer<Object, Object>> fieldSetters() {
		return fieldSetters;
	}

	/**
	 * 获取所有字段名称集合
	 *
	 * @return 字段名称集合
	 */
	public Set<String> fieldNames() {
		return fieldGetters.keySet();
	}

	/**
	 * 获取指定字段的 Setter 函数
	 *
	 * @param property 字段名
	 * @return Setter 消费者，如果不存在返回 null
	 */
	public BiConsumer<Object, Object> getFieldSetter(String property) {
		return fieldSetters.get(property);
	}

	/**
	 * 获取指定字段的 Getter 函数
	 *
	 * @param property 字段名
	 * @return Getter 函数，如果不存在返回 null
	 */
	public Function<Object, Object> getFieldGetter(String property) {
		return fieldGetters.get(property);
	}

	/**
	 * 设置字段值
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @param val 新值
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public void setField(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getFieldSetter(property);
		if (consumer == null) {
			throw new IllegalArgumentException("Field not found");
		}
		consumer.accept(o, val);
	}

	/**
	 * 获取字段值
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @return 字段值
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public Object getField(Object o, String property) {
		Function<Object, Object> function = getFieldGetter(property);
		if (function == null) {
			throw new IllegalArgumentException("Field not found");
		}
		return function.apply(o);
	}

	/**
	 * 设置字段值（如果不存在则忽略）
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @param val 新值
	 */
	public void setFieldOrNoop(Object o, String property, Object val) {
		BiConsumer<Object, Object> consumer = getFieldSetter(property);
		if (consumer == null) {
			return;
		}
		consumer.accept(o, val);
	}

	/**
	 * 获取字段值（如果不存在则返回 null）
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @return 字段值，如果不存在返回 null
	 */
	public Object getFieldOrNoop(Object o, String property) {
		Function<Object, Object> function = getFieldGetter(property);
		if (function == null) {
			return null;
		}
		return function.apply(o);
	}

	// endregion

	/**
	 * 获取或创建 Bean Lambda 访问器实例
	 * <p>从缓存池中获取，如果不存在则创建</p>
	 *
	 * @param type Bean 类型
	 * @return Bean Lambda 访问器实例
	 * @param <T> Bean 类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> BeanLambdaAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, BeanLambdaAccess::create);
	}

	/**
	 * 创建 Bean Lambda 访问器实例
	 * <p>通过 ASM 动态生成字节码类并初始化</p>
	 *
	 * @param type Bean 类型
	 * @return Bean Lambda 访问器实例
	 * @param <T> Bean 类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> BeanLambdaAccess<T> create(Class<T> type) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(type);
		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanLambdaAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type, classification);
			}
		}
		BeanLambdaAccess<T> access;
		try {
			access = (BeanLambdaAccess<T>) accessClass.newInstance();
			// 全部设为只读属性
			access.properties = Collections.unmodifiableMap(classification.properties);
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
		return access;
	}

	/**
	 * 构建访问类字节码
	 * <p>使用 ASM 动态生成 Bean Lambda 访问器的子类字节码</p>
	 *
	 * @param loader 访问类加载器
	 * @param accessClassName 访问类名称
	 * @param type Bean 类型
	 * @param classification 属性分类信息
	 * @return 生成的 Class 对象
	 * @param <T> Bean 类型
	 */
	private static <T> Class buildAccessClass(AccessClassLoader loader
		, String accessClassName, Class<T> type
		, BeanPropertyInfo.Classification classification) {
		String accessClassNameInternal = accessClassName.replace('.', '/');
		String classNameInternal = type.getName().replace('.', '/');

		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		String superClassNameInternal = BeanLambdaAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertSetterInvokers(cw, accessClassNameInternal, type, classification.setters);
		insertGetterInvokers(cw, accessClassNameInternal, type, classification.getters);
		insertFieldSetterInvokers(cw, accessClassNameInternal, type, classification.fields);
		insertFieldGetterInvokers(cw, accessClassNameInternal, type, classification.fields);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}

	/**
	 * 插入 Setter 方法调用代码
	 * <p>生成通过 Lambda 表达式调用 setter 方法的字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param accessClassNameInternal 访问类内部名
	 * @param type Bean 类型
	 * @param setters Setter 方法列表
	 * @param <T> Bean 类型
	 */
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
					mv.visitVarInsn(ASTORE, 2);
					mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
					mv.visitInsn(ATHROW);
				}

				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
		}
	}

	/**
	 * 插入 Getter 方法调用代码
	 * <p>生成通过 Lambda 表达式调用 getter 方法的字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param accessClassNameInternal 访问类内部名
	 * @param type Bean 类型
	 * @param getters Getter 方法列表
	 * @param <T> Bean 类型
	 */
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
					mv.visitVarInsn(ASTORE, 2);
					mv.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
					mv.visitInsn(DUP);
					mv.visitVarInsn(ALOAD, 2);
					mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
					mv.visitInsn(ATHROW);
				}
				mv.visitMaxs(0, 0);
				mv.visitEnd();
			}
		}

	}

	/**
	 * 插入字段 Setter 方法调用代码
	 * <p>生成通过 Lambda 表达式访问字段 setter 的字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param accessClassNameInternal 访问类内部名
	 * @param type Bean 类型
	 * @param fields 字段列表
	 * @param <T> Bean 类型
	 */
	private static <T> void insertFieldSetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}

		SerializableFunction<BeanLambdaAccess, Map<String, BiConsumer<Object, Object>>> buildFieldSetters = BeanLambdaAccess::buildFieldSetters;

		_insertFieldSetterInvokers(cw, accessClassNameInternal, type, fields, buildFieldSetters);
	}

	/**
	 * 插入字段 Getter 方法调用代码
	 * <p>生成通过 Lambda 表达式访问字段 getter 的字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param accessClassNameInternal 访问类内部名
	 * @param type Bean 类型
	 * @param fields 字段列表
	 * @param <T> Bean 类型
	 */
	private static <T> void insertFieldGetterInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}
		SerializableFunction<BeanLambdaAccess, Map<String, Function<Object, Object>>> buildFieldGetters = BeanLambdaAccess::buildFieldGetters;
		_insertFieldGetterInvokers(cw, accessClassNameInternal, type, fields, buildFieldGetters);
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
