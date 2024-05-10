package io.polaris.core.cluster;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
public interface ClusterInstanceRegister {

	void register(InstanceNode instanceNode) throws InstanceRegisterException;

}
