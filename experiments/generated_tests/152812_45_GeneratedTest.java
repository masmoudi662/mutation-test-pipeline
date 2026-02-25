java
package de.javakaffee.web.msm;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.juli.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NodeIdServiceTest {

    private NodeIdService nodeIdService;
    private List<String> nodeIds;
    private List<String> failoverNodeIds;

    @Mock
    private Log log;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        nodeIds = new ArrayList<>();
        failoverNodeIds = new ArrayList<>();
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
    }

    @Test
    public void testGetAvailableNodeId_RegularNodes_Empty() {
        assertNull(nodeIdService.getAvailableNodeId("node1"));
    }

    @Test
    public void testGetAvailableNodeId_RegularNodes_Single() {
        nodeIds.add("node2");
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        assertEquals("node2", nodeIdService.getAvailableNodeId("node1"));
    }

    @Test
    public void testGetAvailableNodeId_RegularNodes_Multiple() {
        nodeIds.addAll(Arrays.asList("node2", "node3"));
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        String result = nodeIdService.getAvailableNodeId("node1");
        assertTrue(result.equals("node2") || result.equals("node3"));
    }

    @Test
    public void testGetAvailableNodeId_FailoverNodes_Empty() {
        nodeIds.add("node1");
        failoverNodeIds.clear();
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);

        assertEquals("node1", nodeIdService.getAvailableNodeId("node0"));

        nodeIds.clear();
        failoverNodeIds.clear();
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);

        assertNull(nodeIdService.getAvailableNodeId("node0"));

        failoverNodeIds.add("node2");
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);

        assertEquals("node2", nodeIdService.getAvailableNodeId("node0"));

    }

    @Test
    public void testGetAvailableNodeId_FailoverNodes_Single() {
        failoverNodeIds.add("node2");
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        assertEquals("node2", nodeIdService.getAvailableNodeId("node1"));
    }

    @Test
    public void testGetAvailableNodeId_FailoverNodes_Multiple() {
        failoverNodeIds.addAll(Arrays.asList("node2", "node3"));
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        String result = nodeIdService.getAvailableNodeId("node1");
        assertTrue(result.equals("node2") || result.equals("node3"));
    }

    @Test
    public void testGetAvailableNodeId_RegularAndFailover() {
        nodeIds.add("node1");
        failoverNodeIds.add("node2");
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        assertEquals("node1", nodeIdService.getAvailableNodeId("node0"));

        nodeIds.clear();
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        assertEquals("node2", nodeIdService.getAvailableNodeId("node0"));
    }

    @Test
    public void testGetAvailableNodeId_NoNodes() {
        nodeIdService = new NodeIdService(nodeIds, failoverNodeIds, log);
        assertNull(nodeIdService.getAvailableNodeId("node1"));
    }
}