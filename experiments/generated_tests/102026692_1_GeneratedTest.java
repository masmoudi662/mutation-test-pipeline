java
package com.scienjus.spring.cloud.etcd.serviceregistry;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.DeleteResponse;
import com.scienjus.spring.cloud.etcd.exception.EtcdOperationException;
import com.scienjus.spring.cloud.etcd.serviceregistry.properties.EtcdDiscoveryProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EtcdServiceRegistryTest {

    @Mock
    private Client etcdClient;

    @Mock
    private EtcdDiscoveryProperties properties;

    @Mock
    private Lease lease;

    @InjectMocks
    private EtcdServiceRegistry etcdServiceRegistry;

    private EtcdRegistration registration;

    @Before
    public void setUp() {
        registration = EtcdRegistration.builder().serviceId("testService").host("testHost").port(8080).build();
        when(properties.getPrefix()).thenReturn("/test");
    }

    @Test
    public void testDeregister() throws Exception {
        KV kvClient = mock(KV.class);
        CompletableFuture<DeleteResponse> deleteFuture = CompletableFuture.completedFuture(mock(DeleteResponse.class));

        when(etcdClient.getKVClient()).thenReturn(kvClient);
        when(kvClient.delete(any(ByteSequence.class))).thenReturn(deleteFuture);
        when(lease.revoke()).thenReturn(CompletableFuture.completedFuture(null));

        etcdServiceRegistry.deregister(registration);

        verify(etcdClient.getKVClient(), times(1)).delete(any(ByteSequence.class));
        verify(lease, times(1)).revoke();
    }

    @Test(expected = EtcdOperationException.class)
    public void testDeregisterThrowsException() throws Exception {
        KV kvClient = mock(KV.class);
        CompletableFuture<DeleteResponse> deleteFuture = new CompletableFuture<>();
        deleteFuture.completeExceptionally(new InterruptedException("test"));

        when(etcdClient.getKVClient()).thenReturn(kvClient);
        when(kvClient.delete(any(ByteSequence.class))).thenReturn(deleteFuture);

        etcdServiceRegistry.deregister(registration);
    }
}