package io.polaris.validation;

import java.util.function.Consumer;

import io.polaris.core.err.ValidationException;
import io.polaris.core.io.Consoles;
import org.junit.jupiter.api.Test;

class ValidationCheckerTest {

	@Test
	void test01() {
		Consumer<ValidationChecker> consumer = checker -> {
			checker.isEmpty("").withMessage("必须为空");
			checker.notEmpty("").withMessage("不能为空");
			checker.isTrue(false).withMessage("必须是true");
			checker.isFalse(true).withMessage("必须是false");
		};
		try {
			Validations.checkAll("验证失败", consumer);
		} catch (ValidationException e) {
			Consoles.printStackTrace(e);
		}
		try {
			Validations.check("验证失败", consumer);
		} catch (ValidationException e) {
			Consoles.printStackTrace(e);
		}

	}
}
