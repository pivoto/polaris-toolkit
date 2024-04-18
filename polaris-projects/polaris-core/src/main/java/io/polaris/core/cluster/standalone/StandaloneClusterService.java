package io.polaris.core.cluster.standalone;

import java.util.ArrayList;
import java.util.List;

import io.polaris.core.cluster.ClusterService;
import io.polaris.core.cluster.InstanceNode;
import io.polaris.core.service.ServiceName;

/**
 * @author Qt
 * @since 1.8,  Apr 17, 2024
 */
@SuppressWarnings("ALL")
@ServiceName("standalone")
public class StandaloneClusterService implements ClusterService {

	private volatile InstanceNode instanceNode;

	@Override
	public void register(InstanceNode instanceNode) {
		this.instanceNode = instanceNode;
		this.instanceNode.setSelf(true);
	}

	@Override
	public List<InstanceNode> query() {
		if (instanceNode == null) {
			return new ArrayList<>(0);
		}
		List<InstanceNode> remoteList = new ArrayList<>(1);
		remoteList.add(instanceNode);
		return remoteList;
	}

}
