package io.polaris.core.guid;

/**
 * @author Qt
 * @since Jul 16, 2024
 */
public interface GuidNodeStrategyProvider {

	GuidNodeStrategy get();

	GuidNodeStrategy get(String app);

}
