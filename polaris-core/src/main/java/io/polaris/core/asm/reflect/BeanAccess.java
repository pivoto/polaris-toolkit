package io.polaris.core.asm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import io.polaris.core.asm.internal.AsmUtils;
import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import io.polaris.core.reflect.SerializableConsumerWithArgs4;
import io.polaris.core.reflect.SerializableTriFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * ASM Bean 访问器
 * <p>通过 ASM 字节码生成技术，为 Java Bean 提供高性能的反射访问能力</p>
 * <p>支持通过索引快速调用 getter/setter 方法和访问字段</p>
 *
 * @author Qt
 * @since Apr 11, 2024
 * @param <T> Bean 类型
 */
public abstract class BeanAccess<T> {
	private static final Logger log = Loggers.of(BeanAccess.class);
	/**
	 * 访问类缓存池
	 */
	@SuppressWarnings({"rawtypes"})
	private static final AccessClassPool<Class<?>, BeanAccess> pool = new AccessClassPool<>();

	/**
	 * 属性信息映射表
	 */
	private Map<String, BeanPropertyInfo> properties;
	/**
	 * Setter 方法索引映射表
	 */
	private Map<String, Integer> setterIndices;
	/**
	 * Getter 方法索引映射表
	 */
	private Map<String, Integer> getterIndices;
	/**
	 * 字段索引映射表
	 */
	private Map<String, Integer> fieldIndices;

	/**
	 * 默认构造函数
	 */
	protected BeanAccess() {
	}

	// region to be inherited

	/**
	 * 通过索引获取属性值（子类实现）
	 *
	 * @param o 目标对象
	 * @param methodIndex 方法索引
	 * @return 属性值
	 * @throws IllegalArgumentException 当方法不存在时抛出
	 */
	public Object getIndexProperty(Object o, int methodIndex) {
		throw new IllegalArgumentException("Method not found");
	}

	/**
	 * 通过索引设置属性值（子类实现）
	 *
	 * @param o 目标对象
	 * @param methodIndex 方法索引
	 * @param val 新值
	 * @throws IllegalArgumentException 当方法不存在时抛出
	 */
	public void setIndexProperty(Object o, int methodIndex, Object val) {
		throw new IllegalArgumentException("Method not found");
	}

	/**
	 * 通过索引获取字段值（子类实现）
	 *
	 * @param o 目标对象
	 * @param fieldIndex 字段索引
	 * @return 字段值
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public Object getIndexField(Object o, int fieldIndex) {
		throw new IllegalArgumentException("Field not found");
	}

	/**
	 * 通过索引设置字段值（子类实现）
	 *
	 * @param o 目标对象
	 * @param methodIndex 方法索引
	 * @param val 新值
	 * @throws IllegalArgumentException 当字段不存在时抛出
	 */
	public void setIndexField(Object o, int methodIndex, Object val) {
		throw new IllegalArgumentException("Field not found");
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
	 * 获取属性或字段的值
	 * <p>优先使用 getter 方法，其次使用字段访问</p>
	 *
	 * @param o 目标对象
	 * @param key 属性名
	 * @return 属性值，如果不存在返回 null
	 */
	public Object getPropertyOrField(Object o, String key) {
		int i = getGetterIndex(key);
		if (i >= 0) {
			return getIndexProperty(o, i);
		}
		i = getFieldIndex(key);
		if (i >= 0) {
			return getIndexField(o, i);
		}
		return null;
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
	public boolean setPropertyOrField(Object o, String key, Object val) {
		int i = getSetterIndex(key);
		if (i >= 0) {
			setIndexProperty(o, i, val);
			return true;
		}
		i = getFieldIndex(key);
		if (i >= 0) {
			setIndexField(o, i, val);
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
	 * @param converter 类型转换器，接受泛型类型和值，返回转换后的值
	 * @return 如果成功设置返回 true，否则返回 false
	 */
	public boolean setPropertyOrField(Object o, String key, Object val, BiFunction<java.lang.reflect.Type, Object, Object> converter) {
		BeanPropertyInfo info = this.properties.get(key);
		if (info == null) {
			return false;
		}
		java.lang.reflect.Type type = info.getPropertyGenericType();
		if (info.getField() != null) {
			setField(o, key, converter.apply(type, val));
			return true;
		}
		if (info.getWriteMethod() != null) {
			setProperty(o, key, converter.apply(type, val));
			return true;
		}
		return false;
	}

	// endregion

	// region Setter 方法相关

	/**
	 * 判断是否包含指定属性的 setter 方法
	 *
	 * @param property 属性名
	 * @return 如果存在返回 true，否则返回 false
	 */
	public boolean containsSetter(String property) {
		return setterIndices.containsKey(property);
	}

	/**
	 * 获取所有 Setter 方法的索引映射表
	 *
	 * @return Setter 方法索引映射表
	 */
	public Map<String, Integer> setterIndices() {
		return setterIndices;
	}

	/**
	 * 获取所有可设置属性的名称集合
	 *
	 * @return 属性名称集合
	 */
	public Set<String> setterPropertyNames() {
		return setterIndices.keySet();
	}

	/**
	 * 获取指定属性的 Setter 方法索引
	 *
	 * @param property 属性名
	 * @return 方法索引，如果不存在返回 -1
	 */
	public int getSetterIndex(String property) {
		Integer index = setterIndices.get(property);
		return index == null ? -1 : index;
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
		int index = getSetterIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Method not found");
		}
		setIndexProperty(o, index, val);
	}

	/**
	 * 设置属性值（如果不存在则忽略）
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @param val 新值
	 */
	public void setPropertyOrNoop(Object o, String property, Object val) {
		int index = getSetterIndex(property);
		if (index == -1) {
			return;
		}
		setIndexProperty(o, index, val);
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
		return getterIndices.containsKey(property);
	}

	/**
	 * 获取所有 Getter 方法的索引映射表
	 *
	 * @return Getter 方法索引映射表
	 */
	public Map<String, Integer> getterIndices() {
		return getterIndices;
	}

	/**
	 * 获取所有可读取属性的名称集合
	 *
	 * @return 属性名称集合
	 */
	public Set<String> getterPropertyNames() {
		return getterIndices.keySet();
	}

	/**
	 * 获取指定属性的 Getter 方法索引
	 *
	 * @param property 属性名
	 * @return 方法索引，如果不存在返回 -1
	 */
	public int getGetterIndex(String property) {
		Integer index = getterIndices.get(property);
		return index == null ? -1 : index;
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
		int index = getGetterIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Method not found");
		}
		return getIndexProperty(o, index);
	}

	/**
	 * 获取属性值（如果不存在则返回 null）
	 *
	 * @param o 目标对象
	 * @param property 属性名
	 * @return 属性值，如果不存在返回 null
	 */
	public Object getPropertyOrNoop(Object o, String property) {
		int index = getSetterIndex(property);
		if (index == -1) {
			return null;
		}
		return getIndexProperty(o, index);
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
		return fieldIndices.containsKey(property);
	}

	/**
	 * 获取所有字段的索引映射表
	 *
	 * @return 字段索引映射表
	 */
	public Map<String, Integer> fieldIndices() {
		return fieldIndices;
	}

	/**
	 * 获取所有字段名称集合
	 *
	 * @return 字段名称集合
	 */
	public Set<String> fieldNames() {
		return fieldIndices.keySet();
	}

	/**
	 * 获取指定字段的索引
	 *
	 * @param property 字段名
	 * @return 字段索引，如果不存在返回 -1
	 */
	public int getFieldIndex(String property) {
		Integer index = fieldIndices.get(property);
		return index == null ? -1 : index;
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
		int index = getFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		setIndexField(o, index, val);
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
		int index = getFieldIndex(property);
		if (index == -1) {
			throw new IllegalArgumentException("Field not found");
		}
		return getIndexField(o, index);
	}

	/**
	 * 设置字段值（如果不存在则忽略）
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @param val 新值
	 */
	public void setFieldOrNoop(Object o, String property, Object val) {
		int index = getFieldIndex(property);
		if (index == -1) {
			return;
		}
		setIndexField(o, index, val);
	}

	/**
	 * 获取字段值（如果不存在则返回 null）
	 *
	 * @param o 目标对象
	 * @param property 字段名
	 * @return 字段值，如果不存在返回 null
	 */
	public Object getFieldOrNoop(Object o, String property) {
		int index = getFieldIndex(property);
		if (index == -1) {
			return null;
		}
		return getIndexField(o, index);
	}

	// endregion

	/**
	 * 获取或创建 Bean 访问器实例
	 * <p>从缓存池中获取，如果不存在则创建</p>
	 *
	 * @param type Bean 类型
	 * @return Bean 访问器实例
	 * @param <T> Bean 类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> BeanAccess<T> get(Class<T> type) {
		return pool.computeIfAbsent(type, BeanAccess::create);
	}

	/**
	 * 创建 Bean 访问器实例
	 * <p>通过 ASM 动态生成字节码类并初始化</p>
	 *
	 * @param type Bean 类型
	 * @return Bean 访问器实例
	 * @param <T> Bean 类型
	 */
	@SuppressWarnings({"unchecked"})
	public static <T> BeanAccess<T> create(Class<T> type) {
		BeanPropertyInfo.Classification classification = BeanPropertyInfo.classify(type);
		String accessClassName = AccessClassLoader.buildAccessClassName(type, BeanAccess.class);
		Class accessClass;
		AccessClassLoader loader = AccessClassLoader.get(type);
		synchronized (loader) {
			accessClass = loader.loadAccessClass(accessClassName);
			if (accessClass == null) {
				accessClass = buildAccessClass(loader, accessClassName, type, classification);
			}
		}
		BeanAccess<T> access;
		try {
			access = (BeanAccess<T>) accessClass.newInstance();
			// 全部设为只读属性
			access.properties = Collections.unmodifiableMap(classification.properties);
			{
				Map<String, Integer> setterIndices = new HashMap<>();
				for (int i = 0; i < classification.setters.size(); i++) {
					setterIndices.put(classification.setters.get(i).getPropertyName(), i);
				}
				access.setterIndices = Collections.unmodifiableMap(setterIndices);
			}
			{
				Map<String, Integer> getterIndices = new HashMap<>();
				for (int i = 0; i < classification.getters.size(); i++) {
					getterIndices.put(classification.getters.get(i).getPropertyName(), i);
				}
				access.getterIndices = Collections.unmodifiableMap(getterIndices);
			}
			{
				Map<String, Integer> fieldIndices = new HashMap<>();
				for (int i = 0; i < classification.fields.size(); i++) {
					fieldIndices.put(classification.fields.get(i).getPropertyName(), i);
				}
				access.fieldIndices = Collections.unmodifiableMap(fieldIndices);
			}
		} catch (Throwable t) {
			throw new IllegalStateException("创建访问类失败: " + accessClassName, t);
		}
		return access;
	}

	/**
	 * 构建访问类字节码
	 * <p>使用 ASM 动态生成 Bean 访问器的子类字节码</p>
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
		String superClassNameInternal = BeanAccess.class.getName().replace('.', '/');
		cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, accessClassNameInternal,
			"L" + superClassNameInternal + "<L" + Type.getInternalName(type) + ";>;",
			superClassNameInternal, null);
		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup", "java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

		AsmUtils.insertDefaultConstructor(cw, superClassNameInternal);
		insertSetterInvokers(cw, accessClassNameInternal, type, classification.setters);
		insertGetterInvokers(cw, accessClassNameInternal, type, classification.getters);
		insertFieldInvokers(cw, accessClassNameInternal, type, classification.fields);

		cw.visitEnd();
		byte[] byteArray = cw.toByteArray();

		return loader.defineAccessClass(accessClassName, byteArray);
	}

	/**
	 * 插入 Setter 方法调用代码
	 * <p>生成通过索引调用 setter 方法的字节码</p>
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

		SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setIndexProperty = BeanAccess::setIndexProperty;
		// 生成各方法的索引式调用方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, setIndexProperty.serialized().getImplMethodName(), "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);

				Label[] labels = AsmUtils.newLabels(setters.size());
				Label labelDefault = new Label();
				Label labelBreak = new Label();
				methodVisitor.visitTableSwitchInsn(0, setters.size() - 1, labelDefault, labels);
				// case
				for (int idxName = 0; idxName < setters.size(); idxName++) {
					methodVisitor.visitLabel(labels[idxName]);
					BeanPropertyInfo info = setters.get(idxName);
					Method method = info.getWriteMethod();
					String methodName = method.getName();
					boolean hasThrows = method.getExceptionTypes().length > 0;
					Class<?>[] parameterTypes = method.getParameterTypes();

					// 方法调用
					{
						Label labelStart = new Label();
						Label labelEnd = new Label();
						Label labelCatch = new Label();
						if (hasThrows) {
							methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
							methodVisitor.visitLabel(labelStart);
						}
						// try
						methodVisitor.visitVarInsn(ALOAD, 1);
						methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
						if (Modifier.isStatic(method.getModifiers())) {
							methodVisitor.visitInsn(POP);
						}

						methodVisitor.visitVarInsn(ALOAD, 3);
						Class<?> parameterType = parameterTypes[0];
						Type paramType = Type.getType(parameterType);
						AsmUtils.autoUnBoxing(methodVisitor, paramType);

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
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
						Class<?> returnType = method.getReturnType();
						AsmUtils.autoBoxing(methodVisitor, returnType);
						methodVisitor.visitJumpInsn(Opcodes.GOTO, labelBreak);

						if (hasThrows) {
							methodVisitor.visitLabel(labelEnd);
						}

						// catch
						if (hasThrows) {
							methodVisitor.visitLabel(labelCatch);
							methodVisitor.visitVarInsn(ASTORE, 4);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 4);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}

					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("Method not found");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);

				// break
				methodVisitor.visitLabel(labelBreak);
			}

			methodVisitor.visitInsn(RETURN);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}

	/**
	 * 插入 Getter 方法调用代码
	 * <p>生成通过索引调用 getter 方法的字节码</p>
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
		SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getIndexProperty = BeanAccess::getIndexProperty;

		// 生成各方法的索引式调用方法
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED | ACC_VARARGS, getIndexProperty.serialized().getImplMethodName(), "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			// switch 方法名分支
			{
				methodVisitor.visitVarInsn(ILOAD, 2);
				Label[] labels = AsmUtils.newLabels(getters.size());
				Label labelDefault = new Label();
				Label labelBreak = new Label();
				methodVisitor.visitTableSwitchInsn(0, getters.size() - 1, labelDefault, labels);

				// case
				for (int idxName = 0; idxName < getters.size(); idxName++) {
					methodVisitor.visitLabel(labels[idxName]);
					BeanPropertyInfo info = getters.get(idxName);
					Method method = info.getReadMethod();
					String methodName = method.getName();
					boolean hasThrows = method.getExceptionTypes().length > 0;

					// 方法调用
					{
						Label labelStart = new Label();
						Label labelEnd = new Label();
						Label labelCatch = new Label();
						if (hasThrows) {
							methodVisitor.visitTryCatchBlock(labelStart, labelEnd, labelCatch, "java/lang/Throwable");
							methodVisitor.visitLabel(labelStart);
						}
						// try
						methodVisitor.visitVarInsn(ALOAD, 1);
						methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
						if (Modifier.isStatic(method.getModifiers())) {
							methodVisitor.visitInsn(POP);
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
						methodVisitor.visitMethodInsn(invokeOpcode, Type.getInternalName(declaringClass), methodName, Type.getMethodDescriptor(method), isInterface);
						Class<?> returnType = method.getReturnType();
						if (returnType.equals(Void.TYPE)) {
							methodVisitor.visitInsn(ACONST_NULL);
						} else {
							AsmUtils.autoBoxing(methodVisitor, returnType);
						}
						if (hasThrows) {
							methodVisitor.visitLabel(labelEnd);
						}
						methodVisitor.visitInsn(ARETURN);
						// catch
						if (hasThrows) {
							methodVisitor.visitLabel(labelCatch);
							methodVisitor.visitVarInsn(ASTORE, 3);
							methodVisitor.visitTypeInsn(NEW, Type.getInternalName(IllegalArgumentException.class));
							methodVisitor.visitInsn(DUP);
							methodVisitor.visitVarInsn(ALOAD, 3);
							methodVisitor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(IllegalArgumentException.class), "<init>", "(Ljava/lang/Throwable;)V", false);
							methodVisitor.visitInsn(ATHROW);
						}
					}
				}
				// default
				methodVisitor.visitLabel(labelDefault);
				methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
				methodVisitor.visitInsn(DUP);
				methodVisitor.visitLdcInsn("Method not found");
				methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
				methodVisitor.visitInsn(ATHROW);
			}
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}

	/**
	 * 插入字段访问方法调用代码
	 * <p>生成通过索引访问字段的字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param accessClassNameInternal 访问类内部名
	 * @param type Bean 类型
	 * @param fields 字段列表
	 * @param <T> Bean 类型
	 */
	private static <T> void insertFieldInvokers(ClassWriter cw, String accessClassNameInternal, Class<T> type, List<BeanPropertyInfo> fields) {
		// ignore
		if (fields.isEmpty()) {
			return;
		}
		SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getIndexField = BeanAccess::getIndexField;
		SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setIndexField = BeanAccess::setIndexField;
		insertStdFieldInvoker(cw, type, fields, getIndexField, setIndexField);
	}

	/**
	 * 插入标准字段访问调用代码
	 * <p>生成 getter 和 setter 的字段访问字节码</p>
	 *
	 * @param cw ClassWriter
	 * @param type Bean 类型
	 * @param fields 字段列表
	 * @param getter Getter 函数接口
	 * @param setter Setter 消费者接口
	 * @param <T> Bean 类型
	 */
	private static <T> void insertStdFieldInvoker(ClassWriter cw, Class<T> type, List<BeanPropertyInfo> fields, SerializableTriFunction<BeanAccess<?>, Object, Integer, Object> getter, SerializableConsumerWithArgs4<BeanAccess<?>, Object, Integer, Object> setter) {
		// getter
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, getter.serialized().getImplMethodName(), "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = AsmUtils.newLabels(fields.size());
			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fields.size() - 1, labelDefault, labels);

			for (int idxField = 0; idxField < fields.size(); idxField++) {
				methodVisitor.visitLabel(labels[idxField]);

				BeanPropertyInfo info = fields.get(idxField);
				Field field = info.getField();
				String fieldName = field.getName();
				Type fieldType = Type.getType(field.getType());

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitInsn(POP);
					methodVisitor.visitFieldInsn(GETSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, Type.getDescriptor(field.getType()));
				} else {
					methodVisitor.visitFieldInsn(GETFIELD, Type.getInternalName(type), fieldName, Type.getDescriptor(field.getType()));
				}
				AsmUtils.autoBoxing(methodVisitor, fieldType);
				methodVisitor.visitInsn(ARETURN);
			}

			// default
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("Field not found");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);

			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();

		}
		// setter
		{
			MethodVisitor methodVisitor = cw.visitMethod(ACC_PROTECTED, setter.serialized().getImplMethodName(), "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
			methodVisitor.visitCode();
			methodVisitor.visitVarInsn(ILOAD, 2);

			Label[] labels = AsmUtils.newLabels(fields.size());

			Label labelDefault = new Label();
			methodVisitor.visitTableSwitchInsn(0, fields.size() - 1, labelDefault, labels);
			for (int idxField = 0; idxField < fields.size(); idxField++) {
				methodVisitor.visitLabel(labels[idxField]);

				BeanPropertyInfo info = fields.get(idxField);
				Field field = info.getField();
				String fieldName = field.getName();
				Type fieldType = Type.getType(field.getType());

				methodVisitor.visitVarInsn(ALOAD, 1);
				methodVisitor.visitTypeInsn(CHECKCAST, Type.getInternalName(type));
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitInsn(POP);
				}
				methodVisitor.visitVarInsn(ALOAD, 3);
				AsmUtils.autoUnBoxing(methodVisitor, fieldType);
				if (Modifier.isStatic(field.getModifiers())) {
					methodVisitor.visitFieldInsn(PUTSTATIC, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				} else {
					methodVisitor.visitFieldInsn(PUTFIELD, Type.getInternalName(field.getDeclaringClass()), fieldName, fieldType.getDescriptor());
				}
				methodVisitor.visitInsn(RETURN);
			}
			// default
			methodVisitor.visitLabel(labelDefault);
			methodVisitor.visitTypeInsn(NEW, "java/lang/IllegalArgumentException");
			methodVisitor.visitInsn(DUP);
			methodVisitor.visitLdcInsn("Field not found");
			methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "(Ljava/lang/String;)V", false);
			methodVisitor.visitInsn(ATHROW);
			methodVisitor.visitMaxs(0, 0);
			methodVisitor.visitEnd();
		}
	}


}
