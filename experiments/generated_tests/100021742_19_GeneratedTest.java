java
package me.snowdrop.stream.binder.artemis.provisioning;

import me.snowdrop.stream.binder.artemis.properties.ArtemisCommonProperties;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientRequestor;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;
import org.apache.activemq.artemis.api.core.management.AddressSettingsInfo;
import org.apache.activemq.artemis.api.core.management.ManagementHelper;
import org.apache.activemq.artemis.api.core.management.ResourceNames;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArtemisBrokerManagerTest {

    @Mock
    private ServerLocator serverLocator;

    @Mock
    private ClientSessionFactory sessionFactory;

    @Mock
    private ClientSession session;

    private ArtemisBrokerManager artemisBrokerManager;

    @Before
    public void setUp() throws Exception {
        artemisBrokerManager = new ArtemisBrokerManager();
        artemisBrokerManager.serverLocator = serverLocator;

        when(serverLocator.createSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.createSession()).thenReturn(session);
    }

    @Test
    public void createQueueSuccessfully() throws Exception {
        doNothing().when(session).createQueue(anyString(), anyString());
        artemisBrokerManager.createQueue("testAddress", "testQueue");
    }

    @Test
    public void createQueueThrowsProvisioningExceptionOnCreateQueueException() throws Exception {
        ActiveMQException activeMQException = mock(ActiveMQException.class);
        doThrow(activeMQException).when(session).createQueue(anyString(), anyString());
        assertThrows(ProvisioningException.class, () -> artemisBrokerManager.createQueue("testAddress", "testQueue"));
    }

    @Test
    public void createQueueThrowsProvisioningExceptionOnSessionFactoryException() throws Exception {
        when(serverLocator.createSessionFactory()).thenThrow(new Exception("Session factory creation failed"));

        assertThrows(ProvisioningException.class, () -> artemisBrokerManager.createQueue("testAddress", "testQueue"));
    }

    @Test
    public void createQueueThrowsProvisioningExceptionOnSessionException() throws Exception {
        when(sessionFactory.createSession()).thenThrow(new Exception("Session creation failed"));
        assertThrows(ProvisioningException.class, () -> artemisBrokerManager.createQueue("testAddress", "testQueue"));
    }
}