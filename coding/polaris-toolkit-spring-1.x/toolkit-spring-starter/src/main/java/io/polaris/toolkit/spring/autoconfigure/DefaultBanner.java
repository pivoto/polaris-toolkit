package io.polaris.toolkit.spring.autoconfigure;

import io.polaris.toolkit.core.io.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * @author Qt
 * @version Nov 03, 2021
 * @since 1.8
 */
@Slf4j
public class DefaultBanner extends ResourceBanner implements Banner {
	private static final String DEFAULT = "${AnsiColor.BRIGHT_MAGENTA}\n" +
			":: Project (${application.formatted-version: unknown-version}) :＼(^O^)／:  Spring-Boot ${spring-boot.formatted-version}" +
			"${AnsiColor.DEFAULT}\n";

	public DefaultBanner() {
		super(buildResource());
	}

	private static InputStreamResource buildResource() {
		try {
			InputStream in = IOUtils.getInputStream("banner.txt", DefaultBanner.class);
			return new InputStreamResource(in);
		} catch (IOException e) {
			log.warn("Banner file cannot read");
			return new InputStreamResource(new ByteArrayInputStream(DEFAULT.getBytes(StandardCharsets.UTF_8)));
		}
	}

	public static void attachToIfNecessary(SpringApplication springApplication) {
		try {
			Field field = springApplication.getClass().getDeclaredField("banner");
			field.setAccessible(true);
			Object o = field.get(springApplication);
			if (o == null) {
				springApplication.setBanner(new DefaultBanner());
			}
		} catch (Exception ignore) {
		}
	}


}
