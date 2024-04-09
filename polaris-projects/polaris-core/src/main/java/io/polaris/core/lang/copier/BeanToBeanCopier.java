package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.SetMultiMap;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class BeanToBeanCopier<S, T> extends BaseCopier<S, T> {
	private static final ILogger log = ILoggers.of(BeanToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public BeanToBeanCopier(S source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (options.getEditable() != null && options.getEditable().isAssignableFrom(actualEditable)) {
			actualEditable = options.getEditable();
		}

		try {
			final BeanMap<S> sourceMap = Beans.newBeanMap(source);
			final BeanMap<T> targetMap = Beans.newBeanMap(target, actualEditable);
			final SetMultiMap<String, String> candidates = createTargetBeanMapCandidateKeys(targetMap);
			final List<Tuple2<String, Object>> sourceEntries = new ArrayList<>(sourceMap.size());

			sourceMap.forEach(wrapConsumer((sourceKey, value) -> {
				sourceKey = super.editKey(sourceKey);
				if (sourceKey == null) {
					return;
				}
				if (super.isIgnore(sourceKey)) {
					return;
				}
				if (value == null && options.isIgnoreNull()) {
					return;
				}
				sourceEntries.add(Tuple2.of(sourceKey, value));
			}));
			setTargetValues(sourceEntries, targetMap, candidates);
		} catch (Exception e) {
			if (!options.isIgnoreError()) {
				throw new UnsupportedOperationException(e);
			} else {
				log.warn("对象复制失败：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}

		return this.target;
	}

}
