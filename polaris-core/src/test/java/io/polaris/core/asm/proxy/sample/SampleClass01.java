package io.polaris.core.asm.proxy.sample;

import io.polaris.core.asm.proxy.BulkEnhancer;
import io.polaris.core.asm.proxy.Enhancer;

/**
 * @author Qt
 */
public class SampleClass01 {


	public static void main(String[] args) {
		new Enhancer()
			.superclass(SampleClass01.class)
			.createClass()
			;
		new BulkEnhancer()
			.superclass(SampleClass01.class)
			.createClass()
			;
	}
}
