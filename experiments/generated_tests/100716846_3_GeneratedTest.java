java
package com.fnproject.fn.runtime.flow;

import com.fnproject.fn.api.Headers;
import com.fnproject.fn.api.flow.FlowCompletionException;
import com.fnproject.fn.api.flow.HttpMethod;
import com.fnproject.fn.api.flow.LambdaSerializationException;
import com.fnproject.fn.api.flow.PlatformException;
import com.fnproject.fn.runtime.exception.PlatformCommunicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RemoteFlowApiClientTest {

    @Mock
    private BlobStoreClient blobStoreClient;

    @Mock
    private FlowRuntimeGlobals flowRuntimeGlobals;

    private RemoteFlowApiClient remoteFlowApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        remoteFlowApiClient = new RemoteFlowApiClient(blobStoreClient);
    }

    @Test
    void invokeFunction_withEmptyHeadersAndData() {
        FlowId flowId = new FlowId("testFlowId");
        String functionId = "testFunctionId";
        byte[] data = new byte[0];
        HttpMethod method = HttpMethod.GET;
        Headers headers = Headers.emptyHeaders();
        CodeLocation codeLocation = new CodeLocation("testLocation");

        assertThrows(NullPointerException.class, () -> remoteFlowApiClient.invokeFunction(flowId, functionId, data, method, headers, codeLocation));
    }

    @Test
    void invokeFunction_withNonEmptyDataAndHeaders() {
        FlowId flowId = new FlowId("testFlowId");
        String functionId = "testFunctionId";
        byte[] data = "testData".getBytes();
        HttpMethod method = HttpMethod.POST;
        Headers headers = Headers.fromHeaders(Collections.singletonMap("Content-Type", Collections.singletonList("text/plain")));
        CodeLocation codeLocation = new CodeLocation("testLocation");

        when(blobStoreClient.writeBlob(eq("testFlowId"), eq(data), eq("text/plain"))).thenReturn(new BlobResponse("blobKey", 8));
        assertThrows(NullPointerException.class, () -> remoteFlowApiClient.invokeFunction(flowId, functionId, data, method, headers, codeLocation));
    }
}