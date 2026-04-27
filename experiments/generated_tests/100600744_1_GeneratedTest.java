java
package jmstool.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jms.support.converter.SimpleMessageConverter;

import jmstool.model.SimpleMessage;

public class SimpleMessageTextCreatorTest {

    private SimpleMessageTextCreator messageCreator;
    private SimpleMessage message;
    private Session session;
    private SimpleMessageConverter messageConverter;

    @BeforeEach
    void setUp() {
        message = new SimpleMessage();
        session = mock(Session.class);
        messageConverter = new SimpleMessageConverter();
    }

    @Test
    void createMessage_withTextAndProperties() throws JMSException {
        String text = "Test Message";
        Map<String, String> props = new HashMap<>();
        props.put("key1", "value1");
        props.put("key2", "value2");

        message.setText(text);
        message.setProps(props);

        messageCreator = new SimpleMessageTextCreator(message);

        TextMessage textMessage = mock(TextMessage.class);
        when(session.createTextMessage(text)).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn(text);

        Message jmsMessage = messageCreator.createMessage(session);

        assertNotNull(jmsMessage);
    }

    @Test
    void createMessage_emptyText() throws JMSException {
        message.setText("");
        messageCreator = new SimpleMessageTextCreator(message);
        TextMessage textMessage = mock(TextMessage.class);
        when(session.createTextMessage("")).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn("");

        Message jmsMessage = messageCreator.createMessage(session);
        assertNotNull(jmsMessage);
    }

    @Test
    void createMessage_nullProperties() throws JMSException {
        message.setText("test");
        message.setProps(null);
        messageCreator = new SimpleMessageTextCreator(message);
        TextMessage textMessage = mock(TextMessage.class);
        when(session.createTextMessage("test")).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn("test");
        Message jmsMessage = messageCreator.createMessage(session);

        assertNotNull(jmsMessage);
    }

    @Test
    void createMessage_noProperties() throws JMSException {
        message.setText("test");
        message.setProps(new HashMap<>());
        messageCreator = new SimpleMessageTextCreator(message);
        TextMessage textMessage = mock(TextMessage.class);
        when(session.createTextMessage("test")).thenReturn(textMessage);
        when(textMessage.getText()).thenReturn("test");

        Message jmsMessage = messageCreator.createMessage(session);

        assertNotNull(jmsMessage);
    }
}