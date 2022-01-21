package com.jcfc.app.demo.component;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Qt
 * @version Nov 02, 2021
 * @since 1.8
 */
@Data
@SuppressWarnings("ConfigurationProperties")
@ConfigurationProperties("demo.properties")
public class DemoProperties {

	private String password;
}
