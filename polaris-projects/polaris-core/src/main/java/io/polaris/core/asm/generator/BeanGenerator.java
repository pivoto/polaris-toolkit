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
 * @author Qt
 * @since May 10, 2024
 */
public class BeanGenerator extends AbstractClassGenerator {
	private Class<?> superclass;
	private final Map<String, Type> props = new HashMap<>();

	public BeanGenerator() {
		super();
	}

	public static void addProperties(BeanGenerator generator, Map<String, Class<?>> props) {
		props.forEach(generator::addProperty);
	}

	public static void addProperties(BeanGenerator generator, Class<?> type) {
		addProperties(generator, AsmReflects.getBeanProperties(type));
	}

	public static void addProperties(BeanGenerator generator, PropertyDescriptor[] descriptors) {
		for (PropertyDescriptor descriptor : descriptors) {
			generator.addProperty(descriptor.getName(), descriptor.getPropertyType());
		}
	}

	public void addProperty(String name, Class<?> type) {
		checkState();
		if (props.containsKey(name)) {
			throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
		}
		props.put(name, Type.getType(type));
	}

	public void setSuperclass(Class<?> superclass) {
		checkState();
		if (superclass != null && superclass.equals(Object.class)) {
			superclass = null;
		}
		this.superclass = superclass;
	}

	public Object create() {
		return AsmReflects.newInstance(createClass());
	}

	public Class<?> createClass() {
		if (isEditable()) {
			if (superclass != null) {
				setPackageName(superclass.getPackage().getName());
				setBaseName(superclass.getSimpleName() + "$" + BeanGenerator.class.getSimpleName());
			}
			setKey(new Object[]{superclass != null ? superclass : Object.class, props});
		}
		return super.generateClass();
	}

	@Override
	protected ClassLoader getDefaultClassLoader() {
		if (superclass != null) {
			return superclass.getClassLoader();
		} else {
			return null;
		}
	}

	@Override
	protected ProtectionDomain getProtectionDomain() {
		return AsmReflects.getProtectionDomain(superclass);
	}

	@Override
	public void generateClass(ClassVisitor cv) throws Exception {
		int size = props.size();
		String[] names = props.keySet().toArray(new String[size]);
		Type[] types = new Type[size];
		for (int i = 0; i < size; i++) {
			types[i] = props.get(names[i]);
		}
		ClassEmitter ce = new ClassEmitter(cv);
		ce.begin_class(AsmConsts.V1_8,
			AsmConsts.ACC_PUBLIC,
			getClassName(),
			superclass != null ? Type.getType(superclass) : AsmConsts.TYPE_OBJECT,
			null,
			null);
		Emitters.null_constructor(ce);
		Emitters.add_properties(ce, names, types);
		ce.end_class();
	}
}
