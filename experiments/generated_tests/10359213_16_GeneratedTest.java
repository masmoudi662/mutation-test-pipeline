java
package i5.las2peer.p2p;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;
import i5.las2peer.api.Context;
import i5.las2peer.api.Node;
import i5.las2peer.api.p2p.ServiceNameVersion;
import i5.las2peer.api.p2p.ServiceVersion;
import i5.las2peer.communication.Message;
import i5.las2peer.logging.L2pLogger;
import i5.las2peer.security.AgentImpl;
import i5.las2peer.security.ServiceAgent;

public class NodeServiceCacheTest {

  private NodeServiceCache nodeServiceCache;
  private L2pLogger logger;
  private Node node;
  private AgentImpl agent;
  private ScheduledExecutorService scheduler;

  @Before
  public void setUp() throws Exception {
    logger = L2pLogger.createLogger(NodeServiceCache.class.getName());
    node = mock(Node.class);
    agent = mock(AgentImpl.class);
    scheduler = mock(ScheduledExecutorService.class);
    when(node.getAgent()).thenReturn(agent);
    nodeServiceCache = new NodeServiceCache(node, logger, scheduler);
    nodeServiceCache.localServices = new HashMap<>();
    nodeServiceCache.globalServices = new HashMap<>();
    Context.get().store("node", node);
  }

  @Test
  public void testGetServiceAgentInstanceLocalExact() throws Exception {
    String serviceName = "TestService";
    ServiceVersion serviceVersion = new ServiceVersion(1, 0, 0);
    ServiceNameVersion service = new ServiceNameVersion(serviceName, serviceVersion);

    Map<ServiceVersion, ServiceInstance> versionMap = new HashMap<>();
    ServiceAgent serviceAgent = mock(ServiceAgent.class);
    ServiceInstance serviceInstance = new ServiceInstance(serviceAgent, null);
    versionMap.put(serviceVersion, serviceInstance);

    nodeServiceCache.localServices.put(serviceName, versionMap);

    ServiceInstance result =
        nodeServiceCache.getServiceAgentInstance(service, true, true, agent);

    assertNotNull(result);
  }
}