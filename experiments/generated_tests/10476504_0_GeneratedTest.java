java
package com.devicehive.service;

import com.devicehive.auth.HivePrincipal;
import com.devicehive.configuration.Messages;
import com.devicehive.exceptions.HiveException;
import com.devicehive.model.FilterEntity;
import com.devicehive.model.enums.PluginStatus;
import com.devicehive.model.query.PluginReqisterQuery;
import com.devicehive.model.query.PluginUpdateQuery;
import com.devicehive.model.response.EntityCountResponse;
import com.devicehive.model.rpc.*;
import com.devicehive.model.updates.PluginUpdate;
import com.devicehive.proxy.config.WebSocketKafkaProxyConfig;
import com.devicehive.resource.util.ResponseFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonObject;

import static javax.ws.rs.core.Response.Status.CREATED;
import static com.devicehive.configuration.Constants.PLUGIN_SUBMITTED;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PluginRegisterServiceTest {

    @InjectMocks
    private PluginRegisterService pluginRegisterService;

    @Mock
    private WebSocketKafkaProxyConfig webSocketKafkaProxyConfig;

    @Test
    public void testRegister() {
        Long userId = 1L;
        PluginReqisterQuery pluginReqisterQuery = new PluginReqisterQuery();
        pluginReqisterQuery.setName("testPlugin");
        pluginReqisterQuery.setVersion("1.0");
        PluginUpdate pluginUpdate = new PluginUpdate();
        String authorization = "testAuth";

        CompletableFuture<Response> future = pluginRegisterService.register(userId, pluginReqisterQuery, pluginUpdate, authorization);

        assertNotNull(future);
    }
}