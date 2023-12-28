package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.json.JsonSerializer;
import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.Types;
import io.polaris.core.log.ILogger;
import io.polaris.core.reflect.Reflects;
import io.polaris.core.service.StatefulServiceLoader;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Qt
 * @since 1.8
 */
public class CollectionConverter<T extends Collection<E>, E> extends AbstractConverter<T> {
	private static final ILogger log = ILogger.of(CollectionConverter.class);
	/** 集合类型 */
	private final JavaType<T> collectionType;
	/** 集合元素类型 */
	private final JavaType<E> elementType;


	public CollectionConverter(JavaType<T> collectionType, JavaType<E> elementType) {
		this.collectionType = collectionType;
		if (elementType == null) {
			elementType = JavaType.of(collectionType.getActualType(Collection.class, 0));
		}
		this.elementType = elementType;
	}

	public CollectionConverter(JavaType<T> collectionType) {
		this(collectionType, null);
	}

	public CollectionConverter(Type collectionType) {
		this(JavaType.of(collectionType), null);
	}

	public CollectionConverter() {
		this(Collection.class);
	}


	@Override
	public JavaType<T> getTargetType() {
		return collectionType;
	}

	@Override
	protected <S> T doConvert(S value, JavaType<T> targetType, JavaType<S> sourceType) {
		if (this.collectionType.getRawClass().isAssignableFrom(sourceType.getRawClass())) {
			Type sourceElementType = sourceType.getActualType(Collection.class, 0);
			// 元素泛型一致
			if (sourceElementType instanceof Class) {
				if (this.elementType.getRawClass().isAssignableFrom((Class<?>) sourceElementType)) {
					return (T) value;
				}
			} else if (this.elementType.getRawType() == sourceElementType) {
				return (T) value;
			}
		}

		T c = (T) create(Types.getClass(this.collectionType), Types.getClass(this.elementType));
		Iterator<?> iter = null;
		if (value instanceof Iterator) {
			iter = ((Iterator<?>) value);
		} else if (value instanceof Iterable) {
			iter = ((Iterable<?>) value).iterator();
		} else if (value instanceof Enumeration) {
			iter = Iterables.iterator((Enumeration<?>) value);
		} else if (value != null) {
			if (value.getClass().isArray()) {
				int length = Array.getLength(value);
				for (int i = 0; i < length; i++) {
					Object value1 = Array.get(value, i);
					c.add(Converters.convert(elementType, value1));
				}
			} else if (value instanceof CharSequence) {
				try {
					// 扩展json实现，
					Optional<JsonSerializer> optional = StatefulServiceLoader.load(JsonSerializer.class).optionalService();
					if (optional.isPresent()) {
						String json = value.toString();
						return optional.get().deserialize(json, collectionType.getRawType());
					}
				} catch (Exception e) {
					log.warn("解析JSON失败：{}", e.getMessage());
					if (log.isDebugEnabled()) {
						log.debug(e.getMessage(), e);
					}
				}
				splitCharSequence((CharSequence) value, c);
			} else {
				c.add(Converters.convert(elementType, value));
			}
		}
		if (iter != null) {
			while (iter.hasNext()) {
				Object next = iter.next();
				c.add(Converters.convert(elementType, next));
			}
		}
		return c;
	}

	private void splitCharSequence(CharSequence value, Collection<?> c) {
		int begin = 0;
		int end = value.length();
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			if (Character.isWhitespace(ch)) {
				begin = i;
			} else {
				if (ch == '[') {
					begin = i + 1;
				}
				break;
			}
		}
		for (int i = value.length() - 1; i >= 0; i--) {
			char ch = value.charAt(i);
			if (Character.isWhitespace(ch)) {
				end = i;
			} else {
				if (ch == ']') {
					end = i;
				}
				break;
			}
		}
		if (end > begin) {
			for (String s : value.subSequence(begin, end).toString().trim().split(SymbolConsts.COMMA)) {
				c.add(Converters.convert(elementType, s));
			}
		} else {
			c.add(null);
		}
	}

	static <T> Collection<T> create(Class<?> collectionType, Class<T> elementType) {
		final Collection<T> c;
		if (collectionType.isAssignableFrom(AbstractCollection.class)) {
			// 抽象集合默认使用ArrayList
			c = new ArrayList<>();
		} else if (collectionType.isAssignableFrom(HashSet.class)) {
			c = new HashSet<>();
		} else if (collectionType.isAssignableFrom(LinkedHashSet.class)) {
			c = new LinkedHashSet<>();
		} else if (collectionType.isAssignableFrom(TreeSet.class)) {
			c = new TreeSet<>((o1, o2) -> {
				// 优先按照对象本身比较，如果没有实现比较接口，默认按照toString内容比较
				if (o1 instanceof Comparable) {
					return ((Comparable<T>) o1).compareTo(o2);
				}
				return o1.toString().compareTo(o2.toString());
			});
		} else if (collectionType.isAssignableFrom(EnumSet.class)) {
			c = (Collection<T>) EnumSet.noneOf((Class<Enum>) elementType);
		} else if (collectionType.isAssignableFrom(BlockingDeque.class)) {
			c = new LinkedBlockingDeque<>();
		} else if (collectionType.isAssignableFrom(Deque.class)) {
			c = new ArrayDeque<>();
		} else if (collectionType.isAssignableFrom(ArrayList.class)) {
			c = new ArrayList<>();
		} else if (collectionType.isAssignableFrom(LinkedList.class)) {
			c = new LinkedList<>();
		} else {
			try {
				c = (Collection<T>) Reflects.newInstance(collectionType);
			} catch (final Exception e) {
				// 无法创建当前类型的对象，尝试创建父类型对象
				final Class<?> superclass = collectionType.getSuperclass();
				if (null != superclass && collectionType != superclass) {
					return create(superclass, elementType);
				}
				throw new UnsupportedOperationException(e);
			}
		}
		return c;
	}
}
