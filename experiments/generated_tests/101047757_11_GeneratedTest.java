java
package tum.cms.sim.momentum.model.layout.graph.raw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tum.cms.sim.momentum.configuration.scenario.EdgeConfiguration;
import tum.cms.sim.momentum.configuration.scenario.GraphScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.ScenarioConfiguration;
import tum.cms.sim.momentum.configuration.scenario.VertexConfiguration;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.properties.ModelProperties;
import tum.cms.sim.momentum.model.scenario.ScenarioManager;
import tum.cms.sim.momentum.utility.geometry.GeometryFactory;
import tum.cms.sim.momentum.utility.geometry.Vector2D;

public class FromConfigurationOperationTest {

  @Mock private ScenarioManager scenarioManager;
  @Mock private ModelProperties properties;

  @InjectMocks private FromConfigurationOperation fromConfigurationOperation;

  private SimulationState simulationState;
  private ScenarioConfiguration scenarioConfiguration;
  private GraphScenarioConfiguration graphConfiguration;
  private List<ScenarioConfiguration> configurations;
  private List<Area> areas;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    simulationState = new SimulationState();
    scenarioConfiguration = new ScenarioConfiguration();
    graphConfiguration = new GraphScenarioConfiguration();
    configurations = new ArrayList<>();
    areas = new ArrayList<>();

    configurations.add(scenarioConfiguration);

    fromConfigurationOperation = new FromConfigurationOperation();
    fromConfigurationOperation.setConfigurations(configurations);
    fromConfigurationOperation.setScenarioManager(scenarioManager);
    fromConfigurationOperation.setProperties(properties);
  }

  @Test
  void testCallPreProcessing_graphFound_verticesAndEdgesAdded() {
    Integer graphId = 1;
    graphConfiguration.setId(graphId);
    graphConfiguration.setName("Test Graph");

    VertexConfiguration vertex1 = new VertexConfiguration();
    vertex1.setId(1);
    vertex1.setPoint(new tum.cms.sim.momentum.configuration.scenario.Vector2D(0.0, 0.0));

    VertexConfiguration vertex2 = new VertexConfiguration();
    vertex2.setId(2);
    vertex2.setPoint(new tum.cms.sim.momentum.configuration.scenario.Vector2D(1.0, 1.0));

    EdgeConfiguration edge = new EdgeConfiguration();
    edge.setIdLeft(1);
    edge.setIdRight(2);

    graphConfiguration.setVertices(List.of(vertex1, vertex2));
    graphConfiguration.setEdges(List.of(edge));

    scenarioConfiguration.setGraphs(Collections.singletonList(graphConfiguration));

    when(properties.getIntegerProperty("graphId")).thenReturn(graphId);
    when(scenarioManager.getAreas()).thenReturn(areas);
    when(scenarioManager.getGraphs()).thenReturn(new ArrayList<>());

    fromConfigurationOperation.callPreProcessing(simulationState);

    assertNotNull(scenarioManager.getGraphs());
    assertEquals(1, scenarioManager.getGraphs().size());
    Graph graph = scenarioManager.getGraphs().get(0);
    assertNotNull(graph);
    assertEquals("Test Graph", graph.getName());
    assertEquals(2, graph.getVertices().size());
  }

  @Test
  void testCallPreProcessing_noGraphFound() {
    Integer graphId = 1;
    when(properties.getIntegerProperty("graphId")).thenReturn(graphId);
    scenarioConfiguration.setGraphs(new ArrayList<>());
    when(scenarioManager.getGraphs()).thenReturn(new ArrayList<>());
    fromConfigurationOperation.callPreProcessing(simulationState);
    assertEquals(0, scenarioManager.getGraphs().size());
  }

  @Test
  void testCallPreProcessing_withAreaSeed() {
    Integer graphId = 1;
    graphConfiguration.setId(graphId);
    VertexConfiguration vertex1 = new VertexConfiguration();
    vertex1.setId(1);
    vertex1.setPoint(new tum.cms.sim.momentum.configuration.scenario.Vector2D(0.0, 0.0));
    graphConfiguration.setVertices(List.of(vertex1));
    scenarioConfiguration.setGraphs(Collections.singletonList(graphConfiguration));
    when(properties.getIntegerProperty("graphId")).thenReturn(graphId);
    when(properties.getDoubleProperty("precisionSeed")).thenReturn(1.0);

    Area area = mock(Area.class);
    Vector2D poi = GeometryFactory.createVector(0.0, 0.0);
    when(area.getPointOfInterest()).thenReturn(poi);
    areas.add(area);
    when(scenarioManager.getAreas()).thenReturn(areas);
    when(scenarioManager.getGraphs()).thenReturn(new ArrayList<>());

    fromConfigurationOperation.callPreProcessing(simulationState);
    assertEquals(1, scenarioManager.getGraphs().size());
  }
}