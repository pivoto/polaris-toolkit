package io.polaris.core.lang.copier;

import io.polaris.core.lang.bean.BeanMap;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.SetMultiMap;
import io.polaris.core.string.StringCases;
import io.polaris.core.tuple.Tuple2;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToBeanCopier<T> extends BaseCopier<Map, T> {
	private static final ILogger log = ILoggers.of(MapToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param target      目标Bean对象
	 * @param targetType  目标泛型类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToBeanCopier(Map source, T target, Type targetType, CopyOptions copyOptions) {
		super(source, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T copy() {
		Class<?> actualEditable = target.getClass();
		if (options.getEditable() != null && options.getEditable().isAssignableFrom(actualEditable)) {
			actualEditable = options.getEditable();
		}
		try {
			final BeanMap<T> targetMap = Beans.newBeanMap(target, actualEditable);
			final SetMultiMap<String, String> candidates = createTargetBeanMapCandidateKeys(targetMap);
			final List<Tuple2<String, Object>> sourceEntries = new ArrayList<>(this.source.size());
			this.source.forEach(wrapConsumer((k, value) -> {
				if (k == null) {
					return;
				}
				String sourceKey = super.editKey(k.toString());
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
				log.warn("对象复制失败：{}",  e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}
		return this.target;
	}


}
