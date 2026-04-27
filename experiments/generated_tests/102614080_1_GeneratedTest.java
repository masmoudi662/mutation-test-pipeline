java
package org.opendaylight.etcd.ds.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.watch.WatchEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opendaylight.infrautils.utils.function.CheckedConsumer;

@RunWith(MockitoJUnitRunner.class)
public class EtcdWatcherSplittingConsumerTest {

    private EtcdWatcherSplittingConsumer consumer;

    @Mock
    private CheckedConsumer<List<WatchEvent>, EtcdException> consumer1;

    @Mock
    private CheckedConsumer<List<WatchEvent>, EtcdException> consumer2;

    private ImmutableMap<ByteSequence, CheckedConsumer<List<WatchEvent>, EtcdException>> splitConsumers;

    @Mock
    private Optional<RevisionAwaiter> revAwaiter;
    @Mock
    private RevisionAwaiter revAwait;

    @Before
    public void setUp() {
        splitConsumers = ImmutableMap.of(
                ByteSequence.fromAscii("prefix1"), consumer1,
                ByteSequence.fromAscii("prefix2"), consumer2);
        revAwaiter = Optional.of(revAwait);
        consumer = new EtcdWatcherSplittingConsumer(splitConsumers, revAwaiter);
    }

    @Test
    public void testAcceptWithMatchingEvents() throws Exception {
        List<WatchEvent> watchEvents = new ArrayList<>();
        KeyValue kv1 = mock(KeyValue.class);
        KeyValue kv2 = mock(KeyValue.class);
        WatchEvent event1 = mock(WatchEvent.class);
        WatchEvent event2 = mock(WatchEvent.class);

        org.mockito.Mockito.when(kv1.getKey()).thenReturn(ByteSequence.fromAscii("prefix1key1"));
        org.mockito.Mockito.when(kv2.getKey()).thenReturn(ByteSequence.fromAscii("prefix2key2"));
        org.mockito.Mockito.when(event1.getKeyValue()).thenReturn(kv1);
        org.mockito.Mockito.when(event2.getKeyValue()).thenReturn(kv2);

        watchEvents.add(event1);
        watchEvents.add(event2);

        consumer.accept(1L, watchEvents);

        ArgumentCaptor<List<WatchEvent>> argument1 = ArgumentCaptor.forClass(List.class);
        verify(consumer1).accept(argument1.capture());
        assertEquals(1, argument1.getValue().size());
        assertEquals(event1, argument1.getValue().get(0));

        ArgumentCaptor<List<WatchEvent>> argument2 = ArgumentCaptor.forClass(List.class);
        verify(consumer2).accept(argument2.capture());
        assertEquals(1, argument2.getValue().size());
        assertEquals(event2, argument2.getValue().get(0));
    }

    @Test
    public void testAcceptWithNoMatchingEvents() throws Exception {
        List<WatchEvent> watchEvents = new ArrayList<>();
        KeyValue kv = mock(KeyValue.class);
        WatchEvent event = mock(WatchEvent.class);

        org.mockito.Mockito.when(kv.getKey()).thenReturn(ByteSequence.fromAscii("otherprefixkey"));
        org.mockito.Mockito.when(event.getKeyValue()).thenReturn(kv);

        watchEvents.add(event);

        consumer.accept(1L, watchEvents);

        ArgumentCaptor<List<WatchEvent>> argument1 = ArgumentCaptor.forClass(List.class);
        verify(consumer1).accept(argument1.capture());
        assertEquals(0, argument1.getValue().size());

        ArgumentCaptor<List<WatchEvent>> argument2 = ArgumentCaptor.forClass(List.class);
        verify(consumer2).accept(argument2.capture());
        assertEquals(0, argument2.getValue().size());
    }

    @Test
    public void testAcceptWithEmptyEventsList() throws Exception {
        List<WatchEvent> watchEvents = new ArrayList<>();

        consumer.accept(1L, watchEvents);

        ArgumentCaptor<List<WatchEvent>> argument1 = ArgumentCaptor.forClass(List.class);
        verify(consumer1).accept(argument1.capture());
        assertEquals(0, argument1.getValue().size());

        ArgumentCaptor<List<WatchEvent>> argument2 = ArgumentCaptor.forClass(List.class);
        verify(consumer2).accept(argument2.capture());
        assertEquals(0, argument2.getValue().size());
    }

    @Test
    public void testRevisionAwaiterUpdate() throws EtcdException {
        List<WatchEvent> watchEvents = new ArrayList<>();
        consumer.accept(1L, watchEvents);
        verify(revAwait).update(1L);
    }
}