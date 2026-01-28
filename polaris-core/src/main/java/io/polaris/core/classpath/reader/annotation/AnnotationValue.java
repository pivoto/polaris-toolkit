package io.polaris.core.classpath.reader.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import io.polaris.core.asm.internal.AsmTypes;
import io.polaris.core.classpath.reader.AnnotationMetadata;
import io.polaris.core.classpath.reader.MethodMetadata;
import io.polaris.core.classpath.reader.impl.DefaultClassMetadata;
import io.polaris.core.classpath.reader.impl.DefaultMethodMetadata;
import io.polaris.core.converter.Converters;
import io.polaris.core.lang.annotation.AnnotationAttributes;
import io.polaris.core.tuple.LazyRef;
import io.polaris.core.tuple.Ref;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.objectweb.asm.Type;

/**
 * @author Qt
 * @since Aug 12, 2025
 */
@ToString
@EqualsAndHashCode
public class AnnotationValue implements AnnotationMetadata {
	/** annotationDesc， 如 Ljavax/validation/constraints/NotNull; */
	private final String descriptor;
	private final boolean visible;
	private final Type type;
	private final String className;
	/** member className */
	private final Map<String, String> valueTypes = new HashMap<>();
	/** member value */
	private final Map<String, AnnotationMetadata> values = new HashMap<>();
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final DefaultClassMetadata classMetadata;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private final Ref<Class<? extends Annotation>> runtimeAnnotationType;

	@Nullable
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	AnnotationValue owner;

	public AnnotationValue(DefaultClassMetadata classMetadata, String descriptor, boolean visible) {
		this.classMetadata = classMetadata;
		this.descriptor = descriptor;
		this.type = Type.getType(descriptor);
		this.className = type.getClassName();
		this.visible = visible;
		if (classMetadata != null) {
			List<MethodMetadata> methods = classMetadata.getMethods();
			if (methods != null) {
				for (MethodMetadata method : methods) {
					String name = method.getMethodName();
					AnnotationMetadata defaultValue = method.getDefaultValue();
					if (defaultValue != null) {
						this.values.put(name, defaultValue);
					}
					this.valueTypes.put(name, method.getReturnTypeName());
				}
			}
		}
		this.runtimeAnnotationType = LazyRef.of(() -> {
			try {
				//noinspection unchecked
				return (Class<? extends Annotation>) AsmTypes.getClass(type);
			} catch (ClassNotFoundException e) {
				return null;
			}
		});
	}

	public void setValue(String name, AnnotationMetadata value) {
		this.values.put(name, value);
		if (value instanceof AnnotationSimpleValue) {
			((AnnotationSimpleValue) value).owner = this;
		} else if (value instanceof AnnotationEnumValue) {
			((AnnotationEnumValue) value).owner = this;
		} else if (value instanceof AnnotationArrayValue) {
			((AnnotationArrayValue) value).owner = this;
		} else if (value instanceof AnnotationValue) {
			((AnnotationValue) value).owner = this;
		}
	}


	@Override
	public String dump() {
		return dump(0);
	}

	@Override
	public String dump(int level) {
		char[] tab = new char[level];
		Arrays.fill(tab, '\t');
		StringBuilder sb = new StringBuilder();
		sb.append(tab);
		sb.append(className).append("(");
		if (!values.isEmpty()) {
			for (Map.Entry<String, AnnotationMetadata> entry : values.entrySet()) {
				sb.append(entry.getKey()).append(":").append(entry.getValue().dump()).append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
		}
		return sb.append(")").toString();
	}

	@Override
	public boolean isAnnotationType() {
		return true;
	}

	@Override
	public String annotationType() {
		return className;
	}

	public AnnotationMetadata get(String name) {
		return values.get(name);
	}

	@Override
	public String getClass(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationSimpleValue) {
				Object value = ((AnnotationSimpleValue) metadata).getValue();
				if (value instanceof Type) {
					return ((Type) value).getClassName();
				}
			}
		}
		return null;
	}

	@Override
	public String getString(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationSimpleValue) {
				Object value = ((AnnotationSimpleValue) metadata).getValue();
				if (value instanceof String) {
					return ((String) value);
				} else if (value != null) {
					return Converters.convertQuietly(String.class, value);
				}
			}
		}
		return null;
	}

	@Override
	public Boolean getBoolean(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationSimpleValue) {
				Object value = ((AnnotationSimpleValue) metadata).getValue();
				if (value instanceof Boolean) {
					return ((Boolean) value);
				} else if (value != null) {
					return Converters.convertQuietly(Boolean.class, value);
				}
			}
		}
		return null;
	}

	@Override
	public Number getNumber(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationSimpleValue) {
				Object value = ((AnnotationSimpleValue) metadata).getValue();
				if (value instanceof Number) {
					return ((Number) value);
				} else if (value != null) {
					return Converters.convertQuietly(Number.class, value);
				}
			}
		}
		return null;
	}

	@Override
	public EnumValue getEnum(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationEnumValue) {
				return new EnumValue(((AnnotationEnumValue) metadata).getClassName(), ((AnnotationEnumValue) metadata).getValue());
			}
		}
		return null;
	}

	@Override
	public AnnotationMetadata getAnnotation(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationValue) {
				return metadata;
			}
		}
		return null;
	}

	@Override
	public String[] getClassArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					String[] rs = new String[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationSimpleValue) {
							Object value = ((AnnotationSimpleValue) item).getValue();
							if (value instanceof Type) {
								rs[i] = ((Type) value).getClassName();
							}
						}
					}
					return rs;
				}
			}
		}
		return null;
	}

	@Override
	public String[] getStringArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					String[] rs = new String[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationSimpleValue) {
							Object value = ((AnnotationSimpleValue) item).getValue();
							if (value instanceof String) {
								rs[i] = ((String) value);
							} else if (value != null) {
								rs[i] = Converters.convertQuietly(String.class, value);
							}
						}
					}
					return rs;
				}
			}
		}
		return null;
	}

	@Override
	public Boolean[] getBooleanArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					Boolean[] rs = new Boolean[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationSimpleValue) {
							Object value = ((AnnotationSimpleValue) item).getValue();
							if (value instanceof Boolean) {
								rs[i] = ((Boolean) value);
							} else if (value != null) {
								rs[i] = Converters.convertQuietly(Boolean.class, value);
							}
						}
					}
					return rs;
				}
			}
		}
		return null;
	}

	@Override
	public Number[] getNumberArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					Number[] rs = new Number[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationSimpleValue) {
							Object value = ((AnnotationSimpleValue) item).getValue();
							if (value instanceof Number) {
								rs[i] = ((Number) value);
							} else if (value != null) {
								rs[i] = Converters.convertQuietly(Number.class, value);
							}
						}
					}
					return rs;
				}
			}
		}
		return null;
	}

	@Override
	public EnumValue[] getEnumArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					EnumValue[] rs = new EnumValue[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationEnumValue) {
							rs[i] = new EnumValue(((AnnotationEnumValue) item).getClassName(), ((AnnotationEnumValue) item).getValue());
						}
					}
					return rs;
				}
			}
		}
		return null;
	}

	@Override
	public AnnotationMetadata[] getAnnotationArray(String name) {
		AnnotationMetadata metadata = values.get(name);
		if (metadata != null) {
			if (metadata instanceof AnnotationArrayValue) {
				List<AnnotationMetadata> array = ((AnnotationArrayValue) metadata).getArray();
				if (array != null) {
					int len = array.size();
					AnnotationMetadata[] rs = new AnnotationMetadata[len];
					for (int i = 0; i < len; i++) {
						AnnotationMetadata item = array.get(i);
						if (item instanceof AnnotationValue) {
							rs[i] = item;
						}
					}
					return rs;
				}
			}
		}
		return null;
	}


	@Override
	public Class<? extends Annotation> runtimeAnnotationType() {
		return runtimeAnnotationType.get();
	}

	@Override
	public AnnotationAttributes asRuntimeAnnotationAttributes() {
		AnnotationAttributes annotationAttributes = AnnotationAttributes.of(runtimeAnnotationType());
		for (Map.Entry<String, AnnotationMetadata> entry : values.entrySet()) {
			String name = entry.getKey();
			AnnotationMetadata metadata = entry.getValue();
			annotationAttributes.setIfNotDefault(name, metadata.asRuntimeValue());
		}
		return annotationAttributes;
	}

	@Override
	public <A extends Annotation> A asRuntimeAnnotation() {
		return asRuntimeAnnotationAttributes().asAnnotation();
	}

}
