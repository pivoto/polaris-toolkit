package io.polaris.core.cluster;

import java.util.List;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
public interface ClusterInstanceQuery {

	List<InstanceNode> query() throws InstanceQueryException;

}
