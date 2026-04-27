java
package me.escoffier.fluid.kafka;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import me.escoffier.fluid.config.Config;
import me.escoffier.fluid.models.Source;
import me.escoffier.fluid.spi.SourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KafkaSourceFactoryTest {

    @Mock
    private Vertx vertx;

    @Mock
    private Config config;

    @Test
    public void testCreate() {
        KafkaSourceFactory factory = new KafkaSourceFactory();
        when(config.getObject()).thenReturn(new JsonObject());
        Single<Source<String>> source = factory.create(vertx, "my-source", config);
        assertThat(source).isNotNull();
        assertThat(source.blockingGet()).isInstanceOf(KafkaSource.class);
    }

    @Test
    public void testKafkaSourceCreation() {
        KafkaSourceFactory factory = new KafkaSourceFactory();
        when(config.getObject()).thenReturn(new JsonObject());
        Single<Source<String>> source = factory.create(vertx, "test-topic", config);
        assertThat(source.blockingGet()).isNotNull();
    }

    @Test
    public void testKafkaSourceName() {
        KafkaSourceFactory factory = new KafkaSourceFactory();
        when(config.getObject()).thenReturn(new JsonObject());
        Single<Source<String>> source = factory.create(vertx, "topic-name", config);
        assertThat(source.blockingGet().name()).isEqualTo("topic-name");
    }

    @Test
    public void testConfigObjectIsNotNull() {
        KafkaSourceFactory factory = new KafkaSourceFactory();
        when(config.getObject()).thenReturn(new JsonObject());
        Single<Source<String>> source = factory.create(vertx, "some-name", config);
        assertThat(source).isNotNull();
    }

    @Test
    public void testVertxNotNull() {
        KafkaSourceFactory factory = new KafkaSourceFactory();
        when(config.getObject()).thenReturn(new JsonObject());
        Single<Source<String>> source = factory.create(vertx, "testing", config);
        assertThat(source).isNotNull();
    }
}