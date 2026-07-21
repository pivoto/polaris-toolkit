package io.polaris.core.lang;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * @author Qt
 * @since Jul 16, 2026
 */
public class JavaAccessControllers {

	public static <T> T run(PrivilegedAction<T> action) {
		// JDK17后已废弃 AccessController.doPrivileged，这里统一使用此方法调用以作标记，便于后续升级版本时清理
		return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
	}

	public static <T> T run(PrivilegedExceptionAction<T> action) throws Exception {
		// JDK17后已废弃 AccessController.doPrivileged，这里统一使用此方法调用以作标记，便于后续升级版本时清理
		return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
	}
}
