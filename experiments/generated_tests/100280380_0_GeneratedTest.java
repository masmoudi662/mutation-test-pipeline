java
package io.axoniq.axondb.client.axon;

import io.axoniq.axondb.client.AxonDBClient;
import io.axoniq.axondb.client.AxonDBConfiguration;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.eventstore.TrackingEventStream;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AxonDBEventStoreTest {

    @Mock
    private AxonDBClient axonDBClient;
    @Mock
    private AxonDBConfiguration axonDBConfiguration;
    @Mock
    private EventStoreStorageEngine storageEngine;
    @InjectMocks
    private AxonDBEventStore axonDBEventStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        when(axonDBEventStore.storageEngine()).thenReturn(storageEngine);
    }

    @Test
    void testOpenStream() {
        TrackingToken trackingToken = null;
        TrackingEventStream expected = mock(TrackingEventStream.class);
        when(storageEngine.openStream(trackingToken)).thenReturn(expected);

        TrackingEventStream actual = axonDBEventStore.openStream(trackingToken);

        assertEquals(expected, actual);
        verify(storageEngine).openStream(trackingToken);
    }

    @Test
    void testCreateStorageEngine() {
        assertThrows(NullPointerException.class, () -> new AxonDBEventStore(null, new AxonDBConfiguration()));
        assertThrows(NullPointerException.class, () -> new AxonDBEventStore(mock(AxonDBClient.class), null));
    }
}