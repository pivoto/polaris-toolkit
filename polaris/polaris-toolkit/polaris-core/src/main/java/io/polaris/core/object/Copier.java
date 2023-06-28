package io.polaris.core.object;

import io.polaris.core.object.copier.CopyOptions;
import io.polaris.core.object.copier.GeneralCopier;

import java.lang.reflect.Type;

/**
 * @author Qt
 * @since 1.8
 */
@FunctionalInterface
public interface Copier<T> {

	T copy();

}
