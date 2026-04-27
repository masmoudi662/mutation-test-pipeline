java
package com.rewedigital.examples.msintegration.productdetailpage.product;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.support.Acknowledgment;

import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.AbstractKafkaConsumer;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.EventProcessingFailedException;
import com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable.UnprocessableEventService;
import com.rewedigital.examples.msintegration.productdetailpage.product.ProductEventConsumer;

@RunWith(MockitoJUnitRunner.class)
public class ProductEventConsumerTest {

    @Mock
    private UnprocessableEventService unprocessableEventService;

    @InjectMocks
    private ProductEventConsumer productEventConsumer;

    private ConsumerRecord<String, String> consumerRecord;
    private Acknowledgment ack;

    @Before
    public void setUp() {
        consumerRecord = new ConsumerRecord<>("topic", 1, 1, "key", "value");
        ack = mock(Acknowledgment.class);
    }

    @Test
    public void testListen() {
        ProductEventConsumer spy = spy(productEventConsumer);
        doNothing().when(spy).handleConsumerRecord(consumerRecord, ack);
        spy.listen(consumerRecord, ack);
        verify(spy).handleConsumerRecord(consumerRecord, ack);
    }

    @Test
    public void testHandleConsumerRecordSuccess() {
        ProductEventConsumer spy = spy(productEventConsumer);
        doNothing().when(spy).process(any(), any());

        spy.handleConsumerRecord(consumerRecord, ack);

        verify(ack).acknowledge();
    }

    @Test
    public void testHandleConsumerRecordFailure() {
        ProductEventConsumer spy = spy(productEventConsumer);
        doThrow(new EventProcessingFailedException("test", new RuntimeException())).when(spy).process(any(), any());
        try {
            spy.handleConsumerRecord(consumerRecord, ack);
        } catch (Exception e) {
           
        }
    }
    
    @Test
    public void testProcess() {
    	ProductEventConsumer consumer = new ProductEventConsumer();
    }
}