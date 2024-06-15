package io.polaris.core.asm.proxy;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {

	Object invoke(int index, Object obj, Object[] args) throws InvocationTargetException;

}
