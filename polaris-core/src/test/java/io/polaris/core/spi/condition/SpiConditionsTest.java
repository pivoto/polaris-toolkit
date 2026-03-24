package io.polaris.core.spi.condition;

import io.polaris.core.env.GlobalStdEnv;
import io.polaris.core.io.Consoles;
import io.polaris.core.lang.annotation.Annotations;
import io.polaris.core.spi.condition.case01.SpiCondition01;
import io.polaris.core.spi.condition.case01.SpiConditionalClass2;
import org.junit.jupiter.api.Test;

class SpiConditionsTest {

	@Test
	void testCondition() {
		Annotations.findMergedAnnotationAttributes(SpiConditionalClass2.class, a -> a.isAnnotationPresent(SpiConditional.class)).forEach((k,v)->{
			Consoles.println("注解：{}, 值：{}", k.getName(), v.asMap());
		});
		GlobalStdEnv.set(SpiCondition01.class.getName(),"false");
		Consoles.println(SpiConditions.matches(SpiConditionalClass2.class, false));
		GlobalStdEnv.set(SpiCondition01.class.getName(),"true");
		Consoles.println(SpiConditions.matches(SpiConditionalClass2.class, false));
		GlobalStdEnv.set("SpiConditionalClass.enabled","true");
		Consoles.println(SpiConditions.matches(SpiConditionalClass2.class, false));

	}
}
