package io.polaris.toolkit.spring.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
@Slf4j
public class CryptoPropertiesBeanPrepareListener implements ApplicationListener<CryptoPropertiesBeanPreparedEvent> {

	@Override
	public void onApplicationEvent(CryptoPropertiesBeanPreparedEvent event) {
		CryptoPropertiesBeanHelper.bindResolver(event.getApplicationContext());
	}

}
