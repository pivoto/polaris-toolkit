package io.polaris.core.annotation.processing;

import io.polaris.core.annotation.Access;
import lombok.Data;

/**
 * @author Qt
 * @since Aug 29, 2025
 */
@Data
@Access(map = true, getters = true, setters = true)
public class BaseDemoBean {
	private long pid;
}
