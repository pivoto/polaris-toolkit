package io.polaris.core.lang.bean;

import io.polaris.core.log.Logger;
import io.polaris.core.log.Loggers;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since  Aug 06, 2023
 */
@Data
@FieldNameConstants
public class Bean01Parent {
	private static final Logger log = Loggers.of(Bean01.class);
	static String staticIdParent;
	String idParent;
	String nameParent;
	protected String nameProtectedParent;
	private String namePrivateParent;



}
