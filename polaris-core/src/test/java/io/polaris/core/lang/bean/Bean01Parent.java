package io.polaris.core.lang.bean;

import io.polaris.core.log.ILogger;
import io.polaris.core.log.ILoggers;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Aug 06, 2023
 */
@Data
@FieldNameConstants
public class Bean01Parent {
	private static final ILogger log = ILoggers.of(Bean01.class);
	static String staticIdParent;
	String idParent;
	String nameParent;
	protected String nameProtectedParent;
	private String namePrivateParent;



}
