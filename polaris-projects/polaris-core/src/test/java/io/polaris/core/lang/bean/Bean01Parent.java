package io.polaris.core.lang.bean;

import io.polaris.core.log.ILogger;
import lombok.Data;

/**
 * @author Qt
 * @since 1.8,  Aug 06, 2023
 */
@Data
public class Bean01Parent {
	static String staticIdParent;
	String idParent;
	private static final ILogger log = ILogger.of(Bean01.class);
	String nameParent;
	protected String nameProtectedParent;
	private String namePrivateParent;



}
