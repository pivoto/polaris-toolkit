package io.polaris.core.cluster;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
public interface ClusterInstanceRegister {

	void register(InstanceNode instanceNode) throws InstanceRegisterException;

}
