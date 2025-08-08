package io.polaris.core.err;

/**
 * @author Qt
 * @since 1.8
 */
public interface ErrorCoded {

	/** 消息码 */
	String getCode();

	/** 消息描述 */
	String getMessage();

}
