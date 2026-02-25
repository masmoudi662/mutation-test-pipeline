java
package de.linsin.alterego;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.jibble.pircbot.PircBot;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BotTest {

    private Bot bot;
    private TestBot testBot;
    private MessageBatcher messageBatcher;

    static class TestBot extends Bot {
        private MessageBatcher messageBatcher;

        public TestBot(MessageBatcher messageBatcher) {
            super(messageBatcher);
            this.messageBatcher = messageBatcher;
        }

        @Override
        protected void onMessage(String argChannel, String argSender, String argLogin, String argHostname, String argMessage) {
            super.onMessage(argChannel, argSender, argLogin, argHostname, argMessage);
        }
    }


    @Before
    public void setUp() {
        messageBatcher = Mockito.mock(MessageBatcher.class);
        testBot = new TestBot(messageBatcher);
    }

    @Test
    public void testOnMessage() {
        String channel = "#testChannel";
        String sender = "testSender";
        String login = "testLogin";
        String hostname = "testHostname";
        String message = "testMessage";

        testBot.onMessage(channel, sender, login, hostname, message);

        verify(messageBatcher).batchMessages(anyString());
    }

    @Test
    public void testOnMessageWithDifferentValues() {
        String channel = "#anotherChannel";
        String sender = "anotherSender";
        String login = "anotherLogin";
        String hostname = "anotherHostname";
        String message = "anotherMessage";

        testBot.onMessage(channel, sender, login, hostname, message);

        verify(messageBatcher).batchMessages(anyString());
    }
}