package io.polaris.core.lang.bean;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Qt
 * @since 1.8,  Aug 28, 2023
 */
public class BeanPropertyBuilders {

	public static <T> BeanPropertyBuilder<T> of(T dest) {
		return new StdBuilder<>(dest);
	}

	public static <T> BeanPropertyBuilder<T> of(Class<T> destType) {
		return new StdBuilder<>(destType);
	}

	public static <T> BeanPropertyBuilder<List<T>> of(List<T> list, Class<T> type) {
		return new ListBuilder<T>(list, type);
	}

	public static <T> BeanPropertyBuilder<List<T>> of(List<T> list, Class<T> type, int size) {
		return new ListBuilder<T>(list, type, size);
	}


	abstract static class AbstractBuilder<T> implements BeanPropertyBuilder<T> {
		protected Deque<Seriation> seriations = new ArrayDeque<>();
		protected T dest;
		protected Object lastOrig;
		protected boolean ignoredNull = true;

		private Seriation newSeriation(String origProperty, String destProperty, Object value) {
			Seriation seriation = new Seriation();
			if (origProperty != null) {// 需要从orig对象取值
				seriation.orig = lastOrig;
				seriation.origProperty = origProperty;
			}
			seriation.destProperty = destProperty;
			seriation.propertyValue = value;
			seriation.ignoredNull = ignoredNull;
			return seriation;
		}

		public void to(T dest) {
			this.dest = dest;
		}

		@Override
		public BeanPropertyBuilder<T> from(Object orig) {
			lastOrig = orig;
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> ignoreNull(boolean ignored) {
			ignoredNull = ignored;
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> set(String destProperty, Object value) {
			Seriation seriation = newSeriation(null, destProperty, value);
			seriations.add(seriation);
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> mapAll() {
			if (lastOrig != null) {
				mapAll(lastOrig.getClass());
			}
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> mapAll(Class<?> clazz) {
			BeanMetadata metadata = BeanMetadatas.getMetadata(clazz);
			for (String name : metadata.getters().keySet()) {
				map(name, name);
			}
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> map(String origProperty, String destProperty) {
			if (lastOrig != null) {
				Seriation seriation = newSeriation(origProperty, destProperty, null);
				seriations.add(seriation);
			}
			return this;
		}

		@Override
		public BeanPropertyBuilder<T> exec() {
			Seriation seriation = null;
			while ((seriation = seriations.poll()) != null) {
				exec(seriation);
			}
			return this;
		}

		protected abstract void exec(Seriation seriation);

		@Override
		public T done() {
			exec();
			return dest;
		}

		protected static class Seriation {
			Object orig;
			String origProperty;
			String destProperty;
			Object propertyValue;
			boolean ignoredNull = true;
		}
	}

	static class ListBuilder<T> extends AbstractBuilder<List<T>> implements BeanPropertyBuilder<List<T>> {
		private List<T> list;
		private Class<T> clazz;

		public ListBuilder(List<T> list, Class<T> clazz) {
			this(list, clazz, 0);
		}

		public ListBuilder(List<T> list, Class<T> clazz, int size) {
			if (list == null) {
				this.list = new ArrayList<>();
			} else {
				this.list = list;
			}
			if (clazz == null) {
				//this.clazz = (Class<T>) LinkedHashMap.class;
				throw new IllegalArgumentException("class is null" );
			} else {
				this.clazz = clazz;
			}
			for (int i = this.list.size(); i < size; i++) {
				this.list.add(newOne());
			}
		}

		private T newOne() {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException("class cannot be initialized", e);
			}
		}

		private T getIndexObj(int i) {
			T one;
			if (list.size() <= i) {
				one = newOne();
				list.add(one);
			} else {
				one = list.get(i);
			}
			return one;
		}

		@SuppressWarnings({"rawtypes"})
		@Override
		public void exec(Seriation seriation) {
			Object orig = seriation.orig;
			if (orig != null) {
				if (orig instanceof Collection) {
					int i = 0;
					for (Object o : (Collection) orig) {
						Object val = Beans.getPathProperty(o, seriation.origProperty);
						if (val != null || !seriation.ignoredNull) {
							Object one = getIndexObj(i);
							Beans.setPathProperty(one, seriation.destProperty, val);
						}
						i++;
					}
				} else if (orig.getClass().isArray()) {
					int len = Array.getLength(orig);
					for (int i = 0; i < len; i++) {
						Object o = Array.get(orig, i);
						Object val = Beans.getPathProperty(o, seriation.origProperty);
						if (val != null || !seriation.ignoredNull) {
							Object one = getIndexObj(i);
							Beans.setPathProperty(one, seriation.destProperty, val);
						}
					}
				} else {
					for (Object one : list) {
						Object val = Beans.getPathProperty(orig, seriation.origProperty);
						if (val != null || !seriation.ignoredNull) {
							Beans.setPathProperty(one, seriation.destProperty, val);
						}
					}
				}
			} else {
				for (Object one : list) {
					if (seriation.propertyValue != null || !seriation.ignoredNull) {
						Beans.setPathProperty(one, seriation.destProperty, seriation.propertyValue);
					}
				}
			}
		}

		@Override
		public List<T> done() {
			exec();
			return list;
		}

	}

	static class StdBuilder<T> extends AbstractBuilder<T> implements BeanPropertyBuilder<T> {
		private T dest;

		public StdBuilder(T dest) {
			this.dest = dest;
		}

		public StdBuilder(Class<T> clazz) {
			try {
				this.dest = clazz.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		public StdBuilder(Object orig, T dest) {
			this(dest);
			from(orig);
		}

		public void exec(Seriation seriation) {
			Object orig = seriation.orig;
			if (orig != null) {
				Object val = Beans.getPathProperty(orig, seriation.origProperty);
				if (val != null || !seriation.ignoredNull) {
					Beans.setPathProperty(dest, seriation.destProperty, val);
				}
			} else {
				if (seriation.propertyValue != null || !seriation.ignoredNull) {
					Beans.setPathProperty(dest, seriation.destProperty, seriation.propertyValue);
				}
			}
		}

		@Override
		public T done() {
			exec();
			return dest;
		}
	}

}
