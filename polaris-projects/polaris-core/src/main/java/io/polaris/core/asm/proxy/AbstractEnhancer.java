package io.polaris.core.asm.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.polaris.core.asm.generator.AbstractClassGenerator;
import io.polaris.core.asm.internal.*;
import io.polaris.core.collection.PrimitiveArrays;
import io.polaris.core.err.BytecodeOperationException;
import io.polaris.core.err.InvocationException;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since May 13, 2024
 */
public abstract class AbstractEnhancer extends AbstractClassGenerator {
	protected static final String GENERATED$ENHANCED = AsmConsts.CLASS_ENHANCED_FIELD;
	protected static final String GENERATED$STATIC_INTERCEPTOR = "GENERATED$STATIC_INTERCEPTOR";
	protected static final String GENERATED$THREAD_INTERCEPTOR = "GENERATED$THREAD_INTERCEPTOR";
	protected static final String GENERATED$TARGET_METHODS = "GENERATED$TARGET_METHODS";
	protected static final String GENERATED$SUPER_INVOKER = "GENERATED$SUPER_INVOKER";
	protected static final String GENERATED$RAW_INVOKER = "GENERATED$RAW_INVOKER";
	protected static final String GENERATED$EMPTY_ARGS = "GENERATED$EMPTY_ARGS";

	protected static final String GENERATED$SUPER_ = "GENERATED$SUPER_";

	protected static final String GENERATED$INTERCEPTORS_MATRIX = "GENERATED$INTERCEPTORS_MATRIX";

	protected static final Type TYPE_ABSTRACT_ENHANCER = Type.getType(AbstractEnhancer.class);
	protected static final Type TYPE_THREAD_LOCAL = Type.getType(ThreadLocal.class);
	protected static final Type TYPE_METHOD = Type.getType(Method.class);
	protected static final Type TYPE_METHOD_ARRAY = Type.getType(Method[].class);
	protected static final Type TYPE_INVOKER = Type.getType(Invoker.class);
	protected static final Type TYPE_INTERCEPTOR_ARRAY = Type.getType(Interceptor[].class);
	protected static final Type TYPE_INTERCEPTOR = Type.getType(Interceptor.class);
	protected static final Type TYPE_INVOCATION_EXCEPTION = Type.getType(InvocationException.class);

//	protected static final Type TYPE_MATCHED_INTERCEPTOR_ARRAY = Type.getType(MatchedInterceptor[].class);
	protected static final Type TYPE_INTERCEPTOR_ARRAY_ARRAY = Type.getType(Interceptor[][].class);



	/** Invoker - lambda method */
	protected static final Signature METHOD_INVOKER__INVOKE =
		new Signature("invoke", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});

	protected static final Signature GET_DECLARED_METHODS =
		AsmTypes.parseSignature("java.lang.reflect.Method[] getDeclaredMethods()");
	protected static final Signature GET_DECLARED_CONSTRUCTORS =
		AsmTypes.parseSignature("java.lang.reflect.Constructor[] getDeclaredConstructors()");
	protected static final Signature FIND_METHODS =
		AsmTypes.parseSignature("java.lang.reflect.Method[] findMethods(String[], java.lang.reflect.Method[])");
	protected static final Signature FIND_CONSTRUCTORS =
		AsmTypes.parseSignature("java.lang.reflect.Constructor[] findConstructors(String[], java.lang.reflect.Constructor[])");

	protected static final Signature GENERATED$INVOKE_SUPER =
		new Signature("GENERATED$INVOKE_SUPER", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});
	protected static final Signature GENERATED$INVOKE_RAW =
		new Signature("GENERATED$INVOKE_RAW", AsmConsts.TYPE_OBJECT, new Type[]{
			Type.INT_TYPE, AsmConsts.TYPE_OBJECT, AsmConsts.TYPE_OBJECT_ARRAY
		});
	protected static final Signature GENERATED$SET_TARGET_METHODS =
		new Signature("GENERATED$SET_TARGET_METHODS", Type.VOID_TYPE, new Type[]{
			TYPE_METHOD_ARRAY
		});
	protected static final Signature GENERATED$DEL_THREAD_INTERCEPTOR =
		new Signature("GENERATED$DEL_THREAD_INTERCEPTOR", Type.VOID_TYPE, new Type[]{});
	protected static final Signature GENERATED$BIND_INTERCEPTOR =
		new Signature("GENERATED$BIND_INTERCEPTOR", Type.VOID_TYPE, new Type[]{
			AsmConsts.TYPE_OBJECT
		});

	protected static final Signature CSTRUCT_NULL = AsmTypes.parseConstructor("");
	protected static final Signature THREAD_LOCAL_GET = AsmTypes.parseSignature("Object get()");
	protected static final Signature THREAD_LOCAL_SET = AsmTypes.parseSignature("void set(Object)");
	protected static final Signature THREAD_LOCAL_REMOVE = AsmTypes.parseSignature("void remove()");


	/** @see #FIND_METHODS */
	public static Method[] findMethods(String[] namesAndDescriptors, Method[] methods) {
		Map<String, Method> map = new HashMap<>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			map.put(method.getName() + Type.getMethodDescriptor(method), method);
		}
		Method[] result = new Method[namesAndDescriptors.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = map.get(namesAndDescriptors[i]);
			if (result[i] == null) {
				throw new IllegalArgumentException(namesAndDescriptors[i]);
			}
		}
		return result;
	}

	public static Constructor<?>[] findConstructors(String[] descriptors, Constructor<?>[] constructors) {
		Map<String, Constructor<?>> map = new HashMap<>();
		for (int i = 0; i < constructors.length; i++) {
			Constructor<?> constructor = constructors[i];
			map.put(Type.getConstructorDescriptor(constructor), constructor);
		}
		Constructor<?>[] result = new Constructor<?>[descriptors.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = map.get(descriptors[i]);
			if (result[i] == null) {
				throw new IllegalArgumentException(descriptors[i]);
			}
		}
		return result;
	}

	protected Class<?> superclass;
	protected boolean withFinal = true;
	protected Class<?>[] interfaces;
	protected Long serialVersion;
	protected Method[] targetMethods;

	public AbstractEnhancer() {
		super();
	}

	public AbstractEnhancer superclass(Class<?> superclass) {
		checkState();
		if (superclass != null && superclass.isInterface()) {
			interfaces(new Class[]{superclass});
		} else if (superclass != null && superclass.equals(Object.class)) {
			// affects choice of ClassLoader
			this.superclass = null;
		} else {
			this.superclass = superclass;
		}
		return this;
	}

	public AbstractEnhancer withFinal(boolean withFinal) {
		checkState();
		this.withFinal = withFinal;
		return this;
	}

	public AbstractEnhancer interfaces(Class<?>[] interfaces) {
		checkState();
		this.interfaces = interfaces;
		return this;
	}

	public AbstractEnhancer serialVersionUID(Long serialVersionUID) {
		checkState();
		this.serialVersion = serialVersionUID;
		return this;
	}


	public Object create() {
		Class<?> type = createClass();
		try {
			bindThreadInterceptors(type);
			return AsmReflects.newInstance(type);
		} finally {
			unbindThreadInterceptors(type);
		}
	}

	public Class<?> createClass() {
		Class<?> parent = (superclass == null) ? Object.class : superclass;
		if (Modifier.isFinal(parent.getModifiers())) {
			throw new IllegalArgumentException("Cannot subclass final class: " + parent.getName());
		}
		if (isEditable()) {
			if (superclass != null) {
				setPackageName(superclass.getPackage().getName());
				setBaseName(superclass.getSimpleName() + "$" + getClass().getSimpleName());
			}
			setKey(generateKey());
		}
		return super.generateClass();
	}

	protected abstract Object generateKey();

	protected abstract void registerStaticInterceptors(Class<?> type);

	protected abstract void bindThreadInterceptors(Class<?> type);

	protected void unbindThreadInterceptors(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$DEL_THREAD_INTERCEPTOR.getName());
			setter.invoke(null);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	protected void registerTargetMethods(Class<?> type) {
		try {
			Method setter = type.getDeclaredMethod(GENERATED$SET_TARGET_METHODS.getName(), Method[].class);
			setter.invoke(null, new Object[]{this.targetMethods});
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(type + " is not an enhanced class");
		} catch (ReflectiveOperationException e) {
			throw new BytecodeOperationException(e);
		}
	}

	@Override
	protected Class<?> generate(ClassLoader classLoader, ClassLoaderData data) {
		Class<?> c = super.generate(classLoader, data);
		registerTargetMethods(c);
		registerStaticInterceptors(c);
		return c;
	}

	@Override
	protected ClassLoader getDefaultClassLoader() {
		if (superclass != null) {
			return superclass.getClassLoader();
		} else if (interfaces != null) {
			return interfaces[0].getClassLoader();
		} else {
			return null;
		}
	}

	@Override
	protected ProtectionDomain getProtectionDomain() {
		if (superclass != null) {
			return AsmReflects.getProtectionDomain(superclass);
		} else if (interfaces != null) {
			return AsmReflects.getProtectionDomain(interfaces[0]);
		} else {
			return null;
		}
	}

	@Override
	public void generateClass(ClassVisitor cv) throws Exception {
		String className = getClassName();
		Class<?> parent = (superclass == null) ? Object.class : superclass;
		List<Constructor<?>> constructors = new ArrayList<>();
		for (Constructor<?> c : parent.getDeclaredConstructors()) {
			int mod = c.getModifiers();
			if (Modifier.isPrivate(mod)) {
				continue;
			}
			if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
				constructors.add(c);
			} else {
				if (AsmTypes.getPackageName(className).equals(AsmTypes.getPackageName(Type.getType(parent)))) {
					constructors.add(c);
				}
			}
		}
		if (constructors.isEmpty()) {
			throw new IllegalArgumentException("no accessible constructors: " + parent.getName());
		}

		Map<String, Method> allMethods = AsmReflects.getAllAccessibleMethods(parent);
		// 添加缺失的接口方法
		Map<String, Method> undeclaredMethods = new HashMap<>();
		if (interfaces != null && interfaces.length > 0) {
			for (Class<?> anInterface : interfaces) {
				for (Method method : anInterface.getMethods()) {
					Method old = allMethods.putIfAbsent(method.getName() + Type.getMethodDescriptor(method), method);
					if (old == null) {
						// 此接口方法未实现
						undeclaredMethods.put(method.getName() + Type.getMethodDescriptor(method), method);
					}
				}
			}
		}
		// 移除不可继承或实现的方法
		for (Iterator<Map.Entry<String, Method>> it = allMethods.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Method> entry = it.next();
			Method method = entry.getValue();
			int mod = method.getModifiers();
			if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
				it.remove();
			}
			if (method.isBridge() || method.isSynthetic()) {
				it.remove();
			}

			if (!Modifier.isPublic(mod) && !Modifier.isProtected(mod)
				&& !AsmTypes.getPackageName(className).equals(AsmTypes.getPackageName(Type.getType(method.getDeclaringClass())))) {
				it.remove();
			}
		}

		// 保存目标方法数组
		this.targetMethods = allMethods.values().toArray(new Method[0]);
		// 方法签名列表
		List<MethodInfo> methods = new ArrayList<>();
		for (int i = 0; i < this.targetMethods.length; i++) {
			Method method = this.targetMethods[i];
			int mod = method.getModifiers();
			int modifiers = (mod
				& ~AsmConsts.ACC_ABSTRACT
				& ~AsmConsts.ACC_NATIVE
				& ~AsmConsts.ACC_SYNCHRONIZED);
			if (withFinal) {
				modifiers |= AsmConsts.ACC_FINAL;
			}
			methods.add(AsmReflects.getMethodInfo(method, modifiers));
		}
		saveTargetMethods(this.targetMethods);


		ClassEmitter ce = new ClassEmitter(cv);
		ce.begin_class(AsmConsts.V1_8, AsmConsts.ACC_PUBLIC, className,
			Type.getType(parent),
			AsmTypes.getTypes(interfaces),
			AsmConsts.SOURCE_FILE);

		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$ENHANCED, AsmConsts.TYPE_CLASS, null);
		if (serialVersion != null) {
			ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, AsmConsts.SUID_FIELD_NAME, Type.LONG_TYPE, serialVersion);
		}

		{
			CodeEmitter e = ce.getStaticHook();
			e.push(Type.getType(parent));
			e.putstatic(ce.getClassType(),GENERATED$ENHANCED, AsmConsts.TYPE_CLASS);
		}

		emitStandardFields(ce);
		emitSpecialFields(ce);
		emitStaticBlock(ce, constructors, methods);
		emitStaticSetTargetMethods(ce);
		emitStaticSetStaticInterceptor(ce);
		emitStaticSetThreadInterceptor(ce);
		emitStaticDelThreadInterceptor(ce);
		emitStaticBindInterceptor(ce);
		emitStaticInvokeRaw(ce, methods, undeclaredMethods);
		emitStaticInvokeSuper(ce, methods, undeclaredMethods);
		emitConstructors(ce, constructors);
		emitMethods(ce, methods, undeclaredMethods);
		emitSuperMethods(ce, methods, undeclaredMethods);
		emitSpecialMethod(ce, methods, undeclaredMethods);

		ce.end_class();
	}

	protected void saveTargetMethods(Method[] targetMethods) {
	}

	protected void emitStandardFields(ClassEmitter ce) {
		ce.declare_field(AsmConsts.PRIVATE_STATIC, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$THREAD_INTERCEPTOR, TYPE_THREAD_LOCAL, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$SUPER_INVOKER, TYPE_INVOKER, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$RAW_INVOKER, TYPE_INVOKER, null);
		ce.declare_field(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$EMPTY_ARGS, AsmConsts.TYPE_OBJECT_ARRAY, null);
	}

	protected void emitSpecialFields(ClassEmitter ce) {
	}


	protected void emitStaticBlock(ClassEmitter ce, List<Constructor<?>> constructors, List<MethodInfo> methods) {
		Type thisType = ce.getClassType();
		CodeEmitter se = ce.getStaticHook();
		// GENERATED$THREAD_INTERCEPTOR = new ThreadLocal<>();
		se.new_instance(TYPE_THREAD_LOCAL);
		se.dup();
		se.invoke_constructor(TYPE_THREAD_LOCAL, CSTRUCT_NULL);
		se.putfield(GENERATED$THREAD_INTERCEPTOR);

		/* 改为初始化类后主动赋值
		// GENERATED$TARGET_METHODS = AbstractEnhancer.findMethods(new String[]{}, $T.class.getDeclaredMethods());
		se.push(methods.size());
		se.newarray(AsmConsts.TYPE_STRING);
		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);
			Signature sig = method.getSignature();
			se.dup();
			se.push(i);
			se.push(sig.getName() + sig.getDescriptor());
			se.aastore();
		}
		se.push(thisType);
		se.invoke_virtual(AsmConsts.TYPE_CLASS, GET_DECLARED_METHODS);
		se.invoke_static(TYPE_ABSTRACT_ENHANCER, FIND_METHODS, false);
		se.putstatic(thisType, GENERATED$TARGET_METHODS, TYPE_METHOD_ARRAY); */

		// GENERATED$EMPTY_ARGS = new Object[0];
		se.push(0);
		se.newarray(AsmConsts.TYPE_OBJECT);
		se.putfield(GENERATED$EMPTY_ARGS);

		// GENERATED$RAW_INVOKER = $T::GENERATED$INVOKE_RAW;
		se.invoke_lambda(TYPE_INVOKER, METHOD_INVOKER__INVOKE, thisType, GENERATED$INVOKE_RAW);
		se.putstatic(thisType, GENERATED$RAW_INVOKER, TYPE_INVOKER);
		// GENERATED$SUPER_INVOKER = $T::GENERATED$INVOKE_SUPER;
		se.invoke_lambda(TYPE_INVOKER, METHOD_INVOKER__INVOKE, thisType, GENERATED$INVOKE_SUPER);
		se.putstatic(thisType, GENERATED$SUPER_INVOKER, TYPE_INVOKER);
	}


	protected void emitStaticInvokeSuper(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		Type thisType = ce.getClassType();
		CodeEmitter e = ce.begin_method(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$INVOKE_SUPER, new Type[]{Type.getType(InvocationTargetException.class)});
		e.load_arg(1);
		e.checkcast_this();
		Local target = e.make_local();
		e.store_local(target);

		e.load_arg(0);
		final Label illegalArg = e.make_label();
		Block block = e.begin_block();

		e.process_switch(PrimitiveArrays.range(methods.size()), new ProcessSwitchCallback() {
			@Override
			public void processCase(int key, Label end) {
				MethodInfo method = methods.get(key);
				Type[] types = method.getSignature().getArgumentTypes();
				e.load_local(target);
				for (int i = 0; i < types.length; i++) {
					e.load_arg(2);
					e.aaload(i);
					e.unbox(types[i]);
				}
				e.invoke_virtual(thisType, new Signature(GENERATED$SUPER_ + method.getSignature().getName(), method.getSignature().getDescriptor()));
				e.box(method.getSignature().getReturnType());
				e.return_value();
			}

			@Override
			public void processDefault() {
				e.goTo(illegalArg);
			}
		});
		block.end();
		Emitters.wrap_throwable(block, Type.getType(InvocationTargetException.class));
		e.mark(illegalArg);
		e.throw_exception(Type.getType(IllegalArgumentException.class), "Cannot find matching method");
		e.end_method();
	}

	protected void emitStaticInvokeRaw(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		CodeEmitter e = ce.begin_method(AsmConsts.PRIVATE_FINAL_STATIC, GENERATED$INVOKE_RAW, new Type[]{Type.getType(InvocationTargetException.class)});

		e.load_arg(0);
		final Label illegalArg = e.make_label();
		Block block = e.begin_block();

		e.process_switch(PrimitiveArrays.range(methods.size()), new ProcessSwitchCallback() {
			@Override
			public void processCase(int key, Label end) {
				MethodInfo method = methods.get(key);
				Type[] types = method.getSignature().getArgumentTypes();
				e.load_arg(1);
				e.checkcast(method.getClassInfo().getType());
				for (int i = 0; i < types.length; i++) {
					e.load_arg(2);
					e.aaload(i);
					e.unbox(types[i]);
				}
				if (undeclaredMethods.containsKey(method.getSignature().getName() + method.getSignature().getDescriptor())) {
					// 扩展接口方法
					e.invoke_interface(method.getClassInfo().getType(), method.getSignature());
				} else {
					e.invoke_virtual(method.getClassInfo().getType(), method.getSignature());
				}
				e.box(method.getSignature().getReturnType());
				e.return_value();
			}

			@Override
			public void processDefault() {
				e.goTo(illegalArg);
			}
		});
		block.end();
		Emitters.wrap_throwable(block, Type.getType(InvocationTargetException.class));
		e.mark(illegalArg);
		e.throw_exception(Type.getType(IllegalArgumentException.class), "Cannot find matching method");
		e.end_method();
	}

	protected void emitStaticSetTargetMethods(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			GENERATED$SET_TARGET_METHODS,
			null);
		e.load_arg(0);
		e.putfield(GENERATED$TARGET_METHODS);
		e.return_value();
		e.end_method();
	}

	protected void emitStaticSetStaticInterceptor(ClassEmitter ce) {
		Signature signature = getSetStaticInterceptorSignature();
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			signature,
			null);
		e.load_arg(0);
		e.putfield(GENERATED$STATIC_INTERCEPTOR);
		e.return_value();
		e.end_method();
	}

	protected abstract Signature getSetStaticInterceptorSignature();

	protected void emitStaticSetThreadInterceptor(ClassEmitter ce) {
		Signature signature = getSetThreadInterceptorSignature();
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			signature,
			null);
		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.load_arg(0);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_SET);
		e.return_value();
		e.end_method();
	}

	protected abstract Signature getSetThreadInterceptorSignature();


	private void emitStaticDelThreadInterceptor(ClassEmitter ce) {
		CodeEmitter e = ce.begin_method(AsmConsts.ACC_PUBLIC | AsmConsts.ACC_STATIC,
			GENERATED$DEL_THREAD_INTERCEPTOR,
			null);
		e.getfield(GENERATED$THREAD_INTERCEPTOR);
		e.invoke_virtual(TYPE_THREAD_LOCAL, THREAD_LOCAL_REMOVE);
		e.return_value();
		e.end_method();
	}

	protected abstract void emitStaticBindInterceptor(ClassEmitter ce);


	protected void emitConstructors(ClassEmitter ce, List<Constructor<?>> constructors) {
		for (Constructor<?> constructor : constructors) {
			MethodInfo methodInfo = AsmReflects.getMethodInfo(constructor);
			CodeEmitter e = Emitters.begin_method(ce, methodInfo,
				AsmConsts.ACC_PUBLIC);
			e.load_this();
			e.dup();
			e.load_args();
			Signature sig = methodInfo.getSignature();
			e.super_invoke_constructor(sig);
			e.invoke_static_this(GENERATED$BIND_INTERCEPTOR);
			e.return_value();
			e.end_method();
		}
	}

	protected abstract void emitMethods(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods);

	protected void emitSuperMethods(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
		Type thisType = ce.getClassType();
		for (int i = 0; i < methods.size(); i++) {
			MethodInfo method = methods.get(i);
			// invoke super method
			{
				// 继承调用添加final/private修饰
				CodeEmitter e = ce.begin_method(AsmConsts.ACC_FINAL | AsmConsts.ACC_PRIVATE
						| (method.getModifiers() & (~AsmConsts.ACC_PUBLIC) & (~AsmConsts.ACC_PROTECTED)),
					new Signature(GENERATED$SUPER_ + method.getSignature().getName(), method.getSignature().getDescriptor()),
					method.getExceptionTypes());
				e.load_this();
				e.load_args();
				Method undeclaredMethod = undeclaredMethods.get(method.getSignature().getName() + method.getSignature().getDescriptor());
				if (undeclaredMethod != null) {
					// 扩展接口的方法，父类并未实现
					if (undeclaredMethod.isDefault()) {
						e.super_invoke(method.getSignature());
					} else {
						Type returnType = method.getSignature().getReturnType();
						e.zero_or_null(returnType);
					}
				} else {
					// 父类的方法
					e.super_invoke(method.getSignature());
				}

				e.return_value();
				e.end_method();
			}
		}
	}

	protected void emitSpecialMethod(ClassEmitter ce, List<MethodInfo> methods, Map<String, Method> undeclaredMethods) {
	}

}
