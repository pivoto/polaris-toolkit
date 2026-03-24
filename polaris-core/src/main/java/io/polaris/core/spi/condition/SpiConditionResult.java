package io.polaris.core.spi.condition;

import io.polaris.core.lang.Copyable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Qt
 * @since Mar 24, 2026
 */
@EqualsAndHashCode
@ToString
public class SpiConditionResult implements Copyable<SpiConditionResult> {

	@Getter
	private final boolean matched;
	@Getter
	private final String message;

	private SpiConditionResult(boolean matched, String message) {
		this.matched = matched;
		this.message = message;
	}

	public static SpiConditionResult of(boolean matched, String message) {
		return new SpiConditionResult(matched, message);
	}

	public static SpiConditionResult match(String message) {
		return new SpiConditionResult(true, message);
	}

	public static SpiConditionResult noMatch(String message) {
		return new SpiConditionResult(false, message);
	}

	@Override
	public SpiConditionResult copy() {
		return new SpiConditionResult(this.matched, this.message);
	}
}
