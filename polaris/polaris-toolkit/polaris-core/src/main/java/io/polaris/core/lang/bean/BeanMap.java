package io.polaris.core.lang.bean;

import io.polaris.core.compiler.MemoryCompiler;
import io.polaris.core.converter.ConverterRegistry;
import io.polaris.core.lang.Objs;
import io.polaris.core.lang.TypeRef;
import io.polaris.core.map.Maps;
import io.polaris.core.reflect.Reflects;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Qt
 * @since 1.8
 */
public class BeanMap extends AbstractMap<String, Object> implements Map<String, Object> {

	private static final AtomicLong seq = new AtomicLong(0);
	protected static final Map<Class<?>, Map<String, Type>> SETTER_TYPES = Maps.newSoftMap(new ConcurrentHashMap<>());
	protected static final Map<Class<?>, Map<String, Function<Object, Object>>> GETTERS = Maps.newSoftMap(new ConcurrentHashMap<>());
	protected static final Map<Class<?>, Map<String, BiConsumer<Object, Object>>> SETTERS = Maps.newSoftMap(new ConcurrentHashMap<>());
	protected static final Map<Class<?>, Class<IMetadata>> METADATA_CLASSES = Maps.newSoftMap(new ConcurrentHashMap<>());

	protected Map<String, Function<Object, Object>> getters;
	protected Map<String, BiConsumer<Object, Object>> setters;
	protected Map<String, Type> types;
	protected final boolean compilable;
	protected final Object bean;
	protected final Class<?> beanType;
	protected final BiFunction<Object, Type, Object> converter;
	protected final Function<String, Object> fallbackGetter;
	protected final BiConsumer<String, Object> fallbackSetter;

	public BeanMap(Object bean) {
		this(bean, null, false, null, null, null);
	}

	public BeanMap(Object bean
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		this(bean, null, true, converter, fallbackGetter, fallbackSetter);
	}

	public BeanMap(Object bean, Class<?> beanType
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		this(bean, beanType, true, converter, fallbackGetter, fallbackSetter);
	}

	public BeanMap(Object bean, Class<?> beanType, boolean compilable
		, BiFunction<Object, Type, Object> converter
		, Function<String, Object> fallbackGetter
		, BiConsumer<String, Object> fallbackSetter) {
		beanType = beanType != null ? beanType : bean.getClass();
		converter = converter != null ? converter : (o, t) -> ConverterRegistry.INSTANCE.convert(t, o);
		this.bean = bean;
		this.beanType = beanType;
		this.compilable = compilable;
		this.converter = converter;
		this.fallbackGetter = fallbackGetter;
		this.fallbackSetter = fallbackSetter;
		this.initGettersAndSetters(beanType);
		this.types = SETTER_TYPES.computeIfAbsent(beanType, (c) -> {
			try {
				Map<String, Type> types = new HashMap<>();
				BeanInfo beanInfo = Introspector.getBeanInfo(c);
				for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
					Method writeMethod = pd.getWriteMethod();
					String name = pd.getName();
					if (writeMethod != null) {
						Type type = writeMethod.getGenericParameterTypes()[0];
						types.put(name, type);
					}
				}
				return types;
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
		});
	}

	protected void initGettersAndSetters(Class<?> clazz) {
		if (compilable) {
			IMetadata[] metadata = new IMetadata[1];
			this.getters = GETTERS.computeIfAbsent(clazz, (c) -> {
				if (metadata[0] == null) {
					try {
						metadata[0] = getMetadataClass(clazz).newInstance();
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				}
				return metadata[0].getters();
			});
			this.setters = SETTERS.computeIfAbsent(clazz, (c) -> {
				if (metadata[0] == null) {
					try {
						metadata[0] = getMetadataClass(clazz).newInstance();
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				}
				return metadata[0].setters();
			});
			/*this.types = SETTER_TYPES.computeIfAbsent(bean.getClass(), (c) -> {
				if (metadata[0] == null) {
					try {
						metadata[0] = getMetadataClass(clazz).newInstance();
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				}
				return metadata[0].types();
			});*/
			// 直接返回
			return;
		}
		this.getters = GETTERS.computeIfAbsent(clazz, (c) -> {
			try {
				Map<String, Function<Object, Object>> getters = new LinkedHashMap<>();
				BeanInfo beanInfo = Introspector.getBeanInfo(c);
				for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
					String key = pd.getName();
					if (Objs.equals("class", key)) {
						continue;
					}
					Method readMethod = pd.getReadMethod();
					if (readMethod != null) {
						getters.put(key, (bean) -> {
							try {
								return readMethod.invoke(bean);
							} catch (ReflectiveOperationException e) {
								throw new RuntimeException(e);
							}
						});
					}
				}
				return getters;
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
		});
		this.setters = SETTERS.computeIfAbsent(clazz, (c) -> {
			try {
				Map<String, BiConsumer<Object, Object>> setters = new LinkedHashMap<>();
				BeanInfo beanInfo = Introspector.getBeanInfo(c);
				for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
					String key = pd.getName();
					if (Objs.equals("class", key)) {
						continue;
					}
					Method writeMethod = pd.getWriteMethod();
					if (writeMethod != null) {
						setters.put(key, (bean, o) -> {
							try {
								writeMethod.invoke(bean, o);
							} catch (ReflectiveOperationException e) {
								throw new RuntimeException(e);
							}
						});
					}
				}
				return setters;
			} catch (IntrospectionException e) {
				throw new RuntimeException(e);
			}
		});
	}


	protected static <T> Class<IMetadata> getMetadataClass(Class<T> beanType) {
		return METADATA_CLASSES.computeIfAbsent(beanType, c -> buildMetadataClass(beanType));
	}

	private static <T> Class<IMetadata> buildMetadataClass(Class<T> beanType) {
		try {
			ClassName className = ClassName.get(beanType.getPackage().getName(),
				beanType.getSimpleName() + "$$BeanMapMetadata$$" + seq.incrementAndGet());
			TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className)
				.addModifiers(Modifier.PUBLIC)
				.addSuperinterface(ClassName.get(IMetadata.class))
				.addMethod(
					MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
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
					.addModifiers(Modifier.PUBLIC);
				// Map<String, BiConsumer<Object, Object>> setters();
				MethodSpec.Builder settersMethodBuilder = MethodSpec.methodBuilder("setters")
					.returns(settersType)
					.addModifiers(Modifier.PUBLIC);
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

	public Type getType(String key) {
		return (Type) this.types.get(key);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			Function<Object, Object> function = getters.get(key);
			if (function != null) {
				return function.apply(bean);
			}
			if (fallbackGetter != null) {
				return fallbackGetter.apply((String) key);
			}
		}
		return null;
	}

	@Override
	public Object put(String key, Object value) {
		Object old = get(key);
		BiConsumer<Object, Object> consumer = setters.get(key);
		if (consumer != null) {
			if (converter != null) {
				Type type = types.get(key);
				if (type != null) {
					value = converter.apply(value, type);
				}
			}
			consumer.accept(bean, value);
		} else {
			if (fallbackSetter != null) {
				fallbackSetter.accept(key, value);
			}
		}
		return old;
	}


	@Override
	public void putAll(Map<? extends String, ?> m) {
		m.forEach((k, v) -> put(k, v));
	}


	@Override
	public int size() {
		return getters.size();
	}

	@Override
	public boolean isEmpty() {
		return getters.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return getters.containsKey(key);
	}

	@Override
	public Set<String> keySet() {
		return getters.keySet();
	}

	@Override
	public Collection<Object> values() {
		List<Object> values = new ArrayList<>(getters.size());
		for (Entry<String, Function<Object, Object>> e : getters.entrySet()) {
			values.add(e.getValue().apply(bean));
		}
		return values;
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		Set<Map.Entry<String, Object>> set = new HashSet<>();
		for (Entry<String, Function<Object, Object>> e : getters.entrySet()) {
			String key = e.getKey();
			Function<Object, Object> function = e.getValue();
			Map.Entry<String, Object> entry = new Map.Entry<String, Object>() {
				@Override
				public String getKey() {
					return key;
				}

				@Override
				public Object getValue() {
					return function.apply(bean);
				}

				@Override
				public Object setValue(Object value) {
					return put(key, value);
				}
			};
			set.add(entry);
		}
		return set;
	}

	@Override
	public boolean containsValue(Object value) {
		for (Entry<String, Function<Object, Object>> entry : getters.entrySet()) {
			Function<Object, Object> function = entry.getValue();
			Object obj1 = function.apply(bean);
			if (Objs.equals(obj1, value)) {
				return true;
			}
		}
		return true;
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public static interface IMetadata {
		Map<String, Type> types();

		Map<String, Function<Object, Object>> getters();

		Map<String, BiConsumer<Object, Object>> setters();
	}
}
