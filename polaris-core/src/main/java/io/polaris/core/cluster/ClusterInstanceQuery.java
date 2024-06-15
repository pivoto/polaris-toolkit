package io.polaris.core.cluster;

import java.util.List;

/**
 * @author Qt
 * @since  Apr 17, 2024
 */
public interface ClusterInstanceQuery {

	List<InstanceNode> query() throws InstanceQueryException;

}
