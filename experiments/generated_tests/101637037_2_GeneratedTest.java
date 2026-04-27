java
package org.zapodot.junit.jms.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.zapodot.jms.common.EmbeddedJMSBrokerHolder;
import org.zapodot.junit.jms.EmbeddedJmsRule;

import javax.jms.ConnectionFactory;
import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class EmbeddedJmsRuleImplTest {

    @Test
    public void testConnectionFactory() {
        EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl();
        ConnectionFactory connectionFactory = rule.connectionFactory();
        assertNotNull(connectionFactory);
        assertTrue(connectionFactory instanceof ActiveMQConnectionFactory);
    }

    @Test
    public void testApply() {
        EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl();
        Statement base = mock(Statement.class);
        Description description = Description.EMPTY;
        Statement statement = rule.apply(base, description);
        assertNotNull(statement);
    }

    @Test
    public void testActiveMqConnectionFactory() {
        EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl();
        ActiveMQConnectionFactory connectionFactory = rule.activeMqConnectionFactory();
        assertNotNull(connectionFactory);
    }

    @Test
    public void testBrokerUri() {
        EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl();
        URI brokerUri = rule.brokerUri();
        assertNotNull(brokerUri);
        assertTrue(brokerUri.toString().startsWith("vm://"));
    }

    @Test
    public void testEmbeddedJMSBrokerHolder() {
        EmbeddedJmsRuleImpl rule = new EmbeddedJmsRuleImpl();
        EmbeddedJMSBrokerHolder brokerHolder = rule.embeddedJMSBrokerHolder();
        assertNotNull(brokerHolder);
    }
}