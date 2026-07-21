package io.polaris.core.asm.generator;

import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

import io.polaris.core.asm.internal.AsmConsts;
import io.polaris.core.asm.internal.AsmReflects;
import io.polaris.core.asm.internal.ClassEmitter;
import io.polaris.core.asm.internal.Emitters;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

/**
 * Bean 生成器
 * <p>动态生成包含指定属性的 JavaBean 类，支持继承父类和自定义属性</p>
 *
 * @author Qt
 * @since May 10, 2024
 */
public class BeanGenerator extends AbstractClassGenerator {
	/** 父类类型 */
	private Class<?> superclass;
	/** 属性映射表：属性名 -> 属性类型 */
	private final Map<String, Type> props = new HashMap<>();

	/**
	 * 默认构造函数
	 */
	public BeanGenerator() {
		super();
	}

	/**
	 * 批量添加属性
	 *
	 * @param generator Bean 生成器实例
	 * @param props 属性映射表：属性名 -> 属性类型
	 */
	public static void addProperties(BeanGenerator generator, Map<String, Class<?>> props) {
		props.forEach(generator::addProperty);
	}

	/**
	 * 从类型中读取并添加属性
	 * <p>使用 JavaBeans 规范读取属性的 getter/setter 方法</p>
	 *
	 * @param generator Bean 生成器实例
	 * @param type 目标类型，从中提取属性信息
	 */
	public static void addProperties(BeanGenerator generator, Class<?> type) {
		addProperties(generator, AsmReflects.getBeanProperties(type));
	}

	/**
	 * 从属性描述符数组中添加属性
	 *
	 * @param generator Bean 生成器实例
	 * @param descriptors 属性描述符数组
	 */
	public static void addProperties(BeanGenerator generator, PropertyDescriptor[] descriptors) {
		for (PropertyDescriptor descriptor : descriptors) {
			generator.addProperty(descriptor.getName(), descriptor.getPropertyType());
		}
	}

	/**
	 * 添加属性
	 *
	 * @param name 属性名
	 * @param type 属性类型
	 * @throws IllegalArgumentException 当属性名已存在时抛出
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void addProperty(String name, Class<?> type) {
		checkState();
		if (props.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
		}
		props.put(name, Type.getType(type));
	}

	/**
	 * 设置父类
	 *
	 * @param superclass 父类类型，为 Object.class 时会被设为 null
	 * @throws IllegalStateException 当生成器已冻结时抛出
	 */
	public void setSuperclass(Class<?> superclass) {
		checkState();
		if (superclass != null && superclass.equals(Object.class)) {
			superclass = null;
		}
		this.superclass = superclass;
	}

	/**
	 * 配置包名、基础类名和键值
	 */
	@Override
	protected void onBeforeCreateClass() {
		if (isEditable()) {
			if (superclass != null) {
				// 使用父类的包名和类名作为基础
				setPackageName(superclass.getPackage().getName());
				setBaseName(superclass.getSimpleName() + "$" + BeanGenerator.class.getSimpleName());
			}
			// 设置缓存键值：包含父类和所有属性信息
			setKey(new Object[]{superclass != null ? superclass : Object.class, props});
		}
	}

	/**
	 * 获取默认类加载器
	 * <p>返回父类的 ClassLoader，如果父类为 null 则返回 null</p>
	 *
	 * @return 默认类加载器
	 */
	@Override
	protected ClassLoader getDefaultClassLoader() {
		if (superclass != null) {
			return superclass.getClassLoader();
		} else {
			return null;
		}
	}

	/**
	 * 获取保护域
	 * <p>返回父类的 ProtectionDomain</p>
	 *
	 * @return ProtectionDomain 实例
	 */
	@Override
	protected ProtectionDomain getProtectionDomain() {
		return AsmReflects.getProtectionDomain(superclass);
	}

	@Override
	protected Class<?> getTargetClass() {
		return superclass;
	}

	/**
	 * 生成类字节码
	 * <p>使用 ASM 框架动态创建包含指定属性的 JavaBean 类</p>
	 *
	 * @param cv ClassVisitor，用于写入类结构
	 * @throws Exception 生成过程中可能出现的异常
	 */
	@Override
	public void generate(ClassVisitor cv) throws Exception {
		int size = props.size();
		// 提取所有属性名和类型
		String[] names = props.keySet().toArray(new String[size]);
		Type[] types = new Type[size];
		for (int i = 0; i < size; i++) {
			types[i] = props.get(names[i]);
		}
		// 创建类发射器
		ClassEmitter ce = new ClassEmitter(cv);
		// 开始定义类：版本 1.8，public 修饰符
		ce.begin_class(AsmConsts.V1_8,
			AsmConsts.ACC_PUBLIC,
			getClassName(),
			superclass != null ? Type.getType(superclass) : AsmConsts.TYPE_OBJECT,
			null,
			null);
		// 添加无参构造函数
		Emitters.null_constructor(ce);
		// 添加所有属性的 getter 和 setter 方法
		Emitters.add_properties(ce, names, types);
		// 结束类的定义
		ce.end_class();
	}
}
