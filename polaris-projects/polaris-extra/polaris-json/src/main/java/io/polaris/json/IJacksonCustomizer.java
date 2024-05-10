package io.polaris.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Qt
 * @since  Feb 04, 2024
 */
public interface IJacksonCustomizer {

	void customize(ObjectMapper mapper);

}
