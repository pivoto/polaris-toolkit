package io.polaris.validation;

import lombok.*;

/**
 * @author Qt
 * @since 1.8
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
	private boolean valid;
	private String message;

	public static ValidationResult success() {
		return new ValidationResult(true, null);
	}

	public static ValidationResult error(String message) {
		return new ValidationResult(false, message);
	}
}
