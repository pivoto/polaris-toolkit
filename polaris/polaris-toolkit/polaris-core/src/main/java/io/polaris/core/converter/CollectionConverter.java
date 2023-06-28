package io.polaris.core.converter;

import io.polaris.core.collection.Iterables;
import io.polaris.core.consts.SymbolConsts;
import io.polaris.core.json.IJsonSerializer;
import io.polaris.core.lang.Types;
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
public class CollectionConverter implements Converter<Collection<?>> {

	/**
	 * 集合类型
	 */
	private final Type collectionType;
	/**
	 * 集合元素类型
	 */
	private final Type elementType;

	public CollectionConverter() {
		this(Collection.class);
	}

	public CollectionConverter(Type collectionType) {
		this(collectionType, Types.getTypeArgument(collectionType));
	}

	public CollectionConverter(Class<?> collectionType) {
		this(collectionType, Types.getTypeArgument(collectionType));
	}

	public CollectionConverter(Type collectionType, Type elementType) {
		this.collectionType = collectionType;
		this.elementType = elementType == null ? Object.class : elementType;
	}


	@Override
	public Collection<?> convert(Object value) {
		Collection<?> c = create(Types.getClass(this.collectionType), Types.getClass(this.elementType));
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
					c.add(ConverterRegistry.INSTANCE.convert(elementType, Array.get(value, i)));
				}
			} else if (value instanceof CharSequence) {
				// 扩展json实现，
				Optional<IJsonSerializer> optional = StatefulServiceLoader.load(IJsonSerializer.class).optionalService();
				if (optional.isPresent()) {
					String json = value.toString();
					return optional.get().deserialize(json, collectionType);
				}

				splitCharSequence((CharSequence) value, c);
			} else {
				c.add(ConverterRegistry.INSTANCE.convert(elementType, value));
			}
		}
		if (iter != null) {
			while (iter.hasNext()) {
				Object next = iter.next();
				c.add(ConverterRegistry.INSTANCE.convert(elementType, next));
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
				c.add(ConverterRegistry.INSTANCE.convert(elementType, s));
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
