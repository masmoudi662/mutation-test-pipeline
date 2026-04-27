java
package com.redhat.coolstore.catalog.api;

import com.redhat.coolstore.catalog.model.Product;
import com.redhat.coolstore.catalog.verticle.service.CatalogService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class ApiVerticleTest {

    private Vertx vertx;
    private CatalogService catalogService;
    private ApiVerticle apiVerticle;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        catalogService = Mockito.mock(CatalogService.class);
        apiVerticle = new ApiVerticle();
        apiVerticle.catalogService = catalogService;
        vertx.deployVerticle(apiVerticle, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testGetProducts(TestContext context) {
        Async async = context.async();
        List<Product> products = new ArrayList<>();
        products.add(new Product("123", "test", "desc", 12.34));

        when(catalogService.getProducts(any())).thenAnswer(i -> {
            Future<List<Product>> future = Future.future();
            future.complete(products);
            i.getArgument(0, Handler.class).handle(future);
            return null;
        });

        TestRequestContext requestContext = new TestRequestContext();
        apiVerticle.getProducts(requestContext);

        vertx.setTimer(100, h -> {
            context.assertEquals("application/json", requestContext.response().getHeader("Content-type"));
            context.assertNotNull(requestContext.response().endResult);
            async.complete();
        });
    }

    @Test
    public void testGetProducts_failure(TestContext context) {
        Async async = context.async();

        when(catalogService.getProducts(any())).thenAnswer(i -> {
            Future<List<Product>> future = Future.future();
            future.fail("Failed to get products");
            i.getArgument(0, Handler.class).handle(future);
            return null;
        });

        TestRequestContext requestContext = new TestRequestContext();
        apiVerticle.getProducts(requestContext);

        vertx.setTimer(100, h -> {
            context.assertNotNull(requestContext.failed());
            async.complete();
        });
    }

    private interface Handler<T> {
        void handle(Future<T> ar);
    }

    private static class TestRequestContext {
        private String endResult;
        private String contentType;
        private Throwable failure;

        public TestRequestContext() {
        }

        public TestResponse response() {
            return new TestResponse(this);
        }

        public String endResult() {
            return endResult;
        }

        public String contentType() {
            return contentType;
        }

        public Throwable failed() {
            return failure;
        }
    }

    private static class TestResponse {
        private TestRequestContext requestContext;

        public TestResponse(TestRequestContext requestContext) {
            this.requestContext = requestContext;
        }

        public TestResponse putHeader(String name, String value) {
            requestContext.contentType = value;
            return this;
        }

        public void end(String result) {
            requestContext.endResult = result;
        }

        public String getHeader(String headerName) {
            return requestContext.contentType;
        }

        public void end(Throwable failure) {
            requestContext.failure = failure;
        }

    }
}