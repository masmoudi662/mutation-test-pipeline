java
package de.javakaffee.web.msm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class NodeIdServiceTest {

    private NodeAvailabilityCache<String> nodeAvailabilityCache;
    private NodeIdList nodeIdList;
    private List<String> failoverNodeIds;
    private NodeIdService nodeIdService;

    @Before
    public void setUp() {
        nodeAvailabilityCache = mock(NodeAvailabilityCache.class);
        nodeIdList = mock(NodeIdList.class);
        failoverNodeIds = new ArrayList<>();
        nodeIdService = new NodeIdService(nodeAvailabilityCache, nodeIdList, failoverNodeIds);
    }

    @Test
    public void testGetAvailableNodeId_RegularNodes() {
        List<String> nodes = Arrays.asList("node1", "node2", "node3");
        NodeIdService service = new NodeIdService(nodes, null);

        when(nodeAvailabilityCache.isNodeAvailable("node2")).thenReturn(true);

        String availableNodeId = service.getAvailableNodeId("node1");
        assertEquals("node2", availableNodeId);
    }

    @Test
    public void testGetAvailableNodeId_FailoverNodes() {
        List<String> nodes = Arrays.asList("node1", "node2");
        List<String> failoverNodes = Arrays.asList("failover1", "failover2");
        NodeIdService service = new NodeIdService(nodes, failoverNodes);

        when(nodeAvailabilityCache.isNodeAvailable("failover1")).thenReturn(true);

        String availableNodeId = service.getAvailableNodeId("node1");
        assertEquals("failover1", availableNodeId);
    }

    @Test
    public void testGetAvailableNodeId_NoAvailableNodes() {
        List<String> nodes = Arrays.asList("node1", "node2");
        List<String> failoverNodes = Arrays.asList("failover1", "failover2");
        NodeIdService service = new NodeIdService(nodes, failoverNodes);

        when(nodeAvailabilityCache.isNodeAvailable(anyString())).thenReturn(false);

        String availableNodeId = service.getAvailableNodeId("node1");
        assertNull(availableNodeId);
    }

    @Test
    public void testGetNextNodeId() {
        List<String> nodes = Arrays.asList("node1", "node2", "node3");
        NodeIdService service = new NodeIdService(nodes, null);
        when(nodeIdList.getNextNodeId("node1")).thenReturn("node2");
        String nextNodeId = service.getNextNodeId("node1");
        assertEquals("node2", nextNodeId);
    }

    @Test
    public void testIsNodeAvailable() {
        when(nodeAvailabilityCache.isNodeAvailable("node1")).thenReturn(true);
        assertTrue(nodeIdService.isNodeAvailable("node1"));
    }

    @Test
    public void testSetNodeAvailable() {
        nodeIdService.setNodeAvailable("node1", true);
        verify(nodeAvailabilityCache).setNodeAvailable("node1", true);
    }

    @Test
    public void testGetMemcachedNodeId() {
    	List<String> nodes = Arrays.asList("node1", "node2");
        NodeIdService service = new NodeIdService(nodes, null);
        when(nodeAvailabilityCache.isNodeAvailable(anyString())).thenReturn(true);
    	assertNotNull(service.getMemcachedNodeId());
    }
}