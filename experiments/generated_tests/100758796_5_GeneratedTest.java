java
package io.left.ripple;

import static io.left.rightmesh.mesh.MeshManager.DATA_RECEIVED;
import static io.left.rightmesh.mesh.MeshManager.PEER_CHANGED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.util.Log;
import io.left.rightmesh.android.AndroidMeshManager;
import io.left.rightmesh.id.MeshId;
import io.left.rightmesh.mesh.MeshManager;
import io.left.rightmesh.mesh.MeshStateListener;
import io.left.rightmesh.util.RightMeshException;

import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class, AndroidMeshManager.class})
public class RightMeshConnectorTest {

    private RightMeshConnector connector;

    @Mock
    private AndroidMeshManager androidMeshManager;

    @Mock
    private ConnectSuccessListener connectSuccessListener;

    @Mock
    private DataReceiveListener dataReceiveListener;

    @Mock
    private PeerChangedListener peerchangedListener;

    @Mock
    private MeshId meshId;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Log.class);
        connector = new RightMeshConnector();
        connector.androidMeshManager = androidMeshManager;
        connector.connectSuccessListener = connectSuccessListener;
        connector.dataReceiveListener = dataReceiveListener;
        connector.peerchangedListener = peerchangedListener;
        connector.meshPort = 8080;
    }

    @Test
    public void meshStateChanged_successState_bindsPortAndUpdatesPeers() throws RightMeshException {
        doNothing().when(androidMeshManager).bind(connector.meshPort);
        connector.meshStateChanged(meshId, MeshStateListener.SUCCESS);
        verify(androidMeshManager).bind(connector.meshPort);
        verify(connectSuccessListener).onConnectSuccess(meshId);
        verify(androidMeshManager).on(DATA_RECEIVED, connector.dataReceiveListener::onDataReceive);
        verify(androidMeshManager).on(PEER_CHANGED, connector.peerchangedListener::onPeerChange);
    }

    @Test
    public void meshStateChanged_successState_bindThrowsServiceDisconnectedException() throws RightMeshException {
        RightMeshException.RightMeshServiceDisconnectedException exception = new RightMeshException.RightMeshServiceDisconnectedException("Service disconnected");
        PowerMockito.doThrow(exception).when(androidMeshManager).bind(connector.meshPort);

        connector.meshStateChanged(meshId, MeshStateListener.SUCCESS);
        PowerMockito.verifyStatic(Log.class, times(1));
        Log.e("RightMeshConnector", "Service disconnected while binding, with message: " + exception.getMessage());
    }

    @Test
    public void meshStateChanged_successState_bindThrowsRightMeshException() throws RightMeshException {
        RightMeshException exception = new RightMeshException("MeshPort already bound");
        PowerMockito.doThrow(exception).when(androidMeshManager).bind(connector.meshPort);

        connector.meshStateChanged(meshId, MeshStateListener.SUCCESS);
        PowerMockito.verifyStatic(Log.class, times(1));
        Log.e("RightMeshConnector", "MeshPort already bound, with message: " + exception.getMessage());
    }
}