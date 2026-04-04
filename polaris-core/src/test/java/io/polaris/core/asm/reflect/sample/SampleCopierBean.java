package io.polaris.core.asm.reflect.sample;

import io.polaris.core.asm.reflect.BeanCopier;
import io.polaris.core.asm.reflect.BeanDirectCopier;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

/**
 * @author Qt
 * @since Apr 04, 2026
 */
@ToString
@FieldNameConstants
public class SampleCopierBean {
	// @formatter:off

	public String pubField;
	@Getter@Setter
	private byte[] byteArray;
	@Getter@Setter
	private int intField;

	public String getFieldThrowable() throws Throwable{
		return "";
	}


	public static void main(String[] args){
		BeanCopier.create(SampleCopierBean.class);
	}
}
