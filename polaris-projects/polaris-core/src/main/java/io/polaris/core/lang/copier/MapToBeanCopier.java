package io.polaris.core.lang.copier;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.polaris.core.lang.JavaType;
import io.polaris.core.lang.bean.Beans;
import io.polaris.core.lang.bean.PropertyAccessor;
import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import io.polaris.core.map.SetMultiMap;
import io.polaris.core.tuple.Tuple2;

/**
 * @author Qt
 * @since 1.8
 */
@SuppressWarnings("rawtypes")
public class MapToBeanCopier<T> extends BaseToBeanCopier<Map, T> {
	private static final ILogger log = ILoggers.of(MapToBeanCopier.class);

	/**
	 * @param source      来源Map
	 * @param sourceType  来源类型
	 * @param target      目标Bean对象
	 * @param targetType  目标类型
	 * @param copyOptions 拷贝选项
	 */
	public MapToBeanCopier(Map source, Type sourceType, T target, Type targetType, CopyOptions copyOptions) {
		super(source, sourceType, target, targetType, copyOptions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T copy() {
		try {
			// 记录已复制key
			final Set<String> recorder = new HashSet<>();
			final Map<String, PropertyAccessor> accessors = Beans.getIndexedFieldAndPropertyAccessors(JavaType.of(targetType).getRawClass());
			final SetMultiMap<String, String> candidates = createTargetBeanMapCandidateKeys(accessors);

			final List<Tuple2<String, Object>> sourceEntries = new ArrayList<>(this.source.size());
			this.source.forEach(wrapConsumer((k, value) -> {
				if (k == null) {
					return;
				}
				String key = k.toString();
				String sourceKey = options.editKey(key);
				if (sourceKey == null) {
					return;
				}
				if (options.isIgnoredKey(sourceKey)) {
					return;
				}
				if (value == null && options.ignoreNull()) {
					return;
				}
				sourceEntries.add(Tuple2.of(sourceKey, value));
			}));
			// set with candidates
			setTargetValues(sourceEntries, accessors, candidates, recorder);
		} catch (Exception e) {
			if (!options.ignoreError()) {
				throw new UnsupportedOperationException(e);
			} else {
				log.warn("Copy failed：{}", e.getMessage());
				if (log.isDebugEnabled()) {
					log.debug(e.getMessage(), e);
				}
			}
		}
		return this.target;
	}


}
