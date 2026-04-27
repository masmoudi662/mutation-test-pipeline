java
package kontent.ai.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kontent.ai.delivery.template.TemplateEngineConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DeliveryClientTest {

    @Test
    public void testGetItem_codename() {
        DeliveryClient client = new DeliveryClient(new DeliveryOptions());
        CompletionStage<ContentItemResponse> stage = client.getItem("codename");
        assertNotNull(stage);
    }
}