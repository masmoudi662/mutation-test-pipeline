java
package com.icodici.universa.node2;

import com.icodici.crypto.KeyAddress;
import com.icodici.crypto.PrivateKey;
import com.icodici.crypto.PublicKey;
import com.icodici.universa.Core;
import com.icodici.universa.HashId;
import com.icodici.universa.contract.Contract;
import com.icodici.universa.node.PostgresLedger;
import com.icodici.universa.node.StateRecord;
import com.icodici.universa.node2.network.ClientHTTPServer;
import com.icodici.universa.node2.network.DatagramAdapter;
import com.icodici.universa.node2.network.NetworkV2;
import com.icodici.universa.node2.network.UDPAdapter;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

public class MainTest {

    @Test
    public void testShutdown() throws Exception {
        Main main = new Main();
        main.logger = Logger.getLogger("test");
        main.network = Mockito.mock(NetworkV2.class);
        main.ledger = Mockito.mock(PostgresLedger.class);
        main.clientHTTPServer = Mockito.mock(ClientHTTPServer.class);
        main.parser = new Object();

        main.shutdown();

        verify(main.network, times(1)).shutdown();
        verify(main.clientHTTPServer, times(1)).shutdown();
        verify(main.ledger, times(1)).close();
    }
}