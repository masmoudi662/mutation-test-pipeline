java
package com.obsidiandynamics.socketx.undertow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.*;

import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.mockito.junit.*;
import org.xnio.*;

import com.obsidiandynamics.socketx.*;

import io.undertow.websockets.core.*;

@RunWith(MockitoJUnitRunner.class)
public class UndertowEndpointManagerTest {

  @Mock private WebSocketChannel channel;
  @Mock private XEndpointScanner scanner;
  @Mock private XEndpointListener<UndertowEndpoint> listener;
  @Mock private StreamSourceFrameChannel sourceChannel;
  
  private UndertowEndpointManager endpointManager;
  
  @Before
  public void setUp() {
    endpointManager = new UndertowEndpointManager(scanner, listener, 0);
  }
  
  @Test
  public void testCreateEndpoint() throws IOException {
    when(channel.setOption(Options.TCP_NODELAY, true)).thenReturn(true);
    final UndertowEndpoint endpoint = endpointManager.createEndpoint(channel);
    assertNotNull(endpoint);
    verify(channel).setOption(Options.TCP_NODELAY, true);
    verify(channel).getReceiveSetter();
    verify(channel).resumeReceives();
    verify(scanner).addEndpoint(endpoint);
    verify(listener).onConnect(endpoint);
  }
  
  @Test
  public void testCreateEndpointWithIdleTimeout() throws IOException {
    endpointManager = new UndertowEndpointManager(scanner, listener, 1000);
    when(channel.setOption(Options.TCP_NODELAY, true)).thenReturn(true);
    final UndertowEndpoint endpoint = endpointManager.createEndpoint(channel);
    assertNotNull(endpoint);
    verify(channel).setOption(Options.TCP_NODELAY, true);
    verify(channel).getReceiveSetter();
    verify(channel).resumeReceives();
    verify(scanner).addEndpoint(endpoint);
    verify(listener).onConnect(endpoint);
    verify(channel).setIdleTimeout(1000);
  }
}