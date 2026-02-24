java
package de.linsin.alterego;

import de.linsin.alterego.notification.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.jibble.pircbot.User;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BotTest {

    private Bot bot;
    private NotificationService notificationService;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        bot = new Bot("TestBot", "testlogin", "AlterEgo");
        notificationService = mock(NotificationService.class);
        bot.addNotificationService(notificationService);
    }

    @Test
    public void testOnMessage() throws Exception {
        bot.onMessage("#testChannel", "TestSender", "testlogin", "testhost", "Test Message");
        Thread.sleep(Bot.MESSAGE_BATCH_DELAY_MS + 1000);

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, timeout(Bot.MESSAGE_BATCH_DELAY_MS + 1000).times(1)).notify(titleCaptor.capture(), messageCaptor.capture());

        assertEquals("message", titleCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains("TestSender: Test Message"));
    }

    @Test
    public void testOnPrivateMessage() throws Exception {
        bot.onPrivateMessage("TestSender", "testlogin", "testhost", "Test Private Message");
        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, times(1)).notify(titleCaptor.capture(), messageCaptor.capture());

        assertEquals("private message", titleCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains("TestSender: Test Private Message"));
    }

    @Test
    public void testOnJoinSwitchesSilentModeOn() throws Exception {
        User alterEgoUser = mock(User.class);
        when(alterEgoUser.getNick()).thenReturn("AlterEgo");

        User otherUser = mock(User.class);
        when(otherUser.getNick()).thenReturn("OtherUser");

        Bot botSpy = Mockito.spy(bot);
        when(botSpy.getUsers("#testChannel")).thenReturn(new User[]{alterEgoUser, otherUser});

        botSpy.onJoin("#testChannel", "TestSender", "testlogin", "testhost");

        botSpy.onMessage("#testChannel", "TestSender", "testlogin", "testhost", "Test Message");
        Thread.sleep(Bot.MESSAGE_BATCH_DELAY_MS + 1000);

        verify(notificationService, never()).notify(anyString(), anyString());
    }

    @Test
    public void testOnPartSwitchesSilentModeOff() throws Exception {
        User alterEgoUser = mock(User.class);
        when(alterEgoUser.getNick()).thenReturn("AlterEgo");

        User otherUser = mock(User.class);
        when(otherUser.getNick()).thenReturn("OtherUser");

        Bot botSpy = Mockito.spy(bot);
        when(botSpy.getUsers("#testChannel")).thenReturn(new User[]{otherUser});

        botSpy.onPart("#testChannel", "TestSender", "testlogin", "testhost");

        botSpy.onMessage("#testChannel", "TestSender", "testlogin", "testhost", "Test Message");
        Thread.sleep(Bot.MESSAGE_BATCH_DELAY_MS + 1000);

        verify(notificationService, timeout(Bot.MESSAGE_BATCH_DELAY_MS + 1000).times(1)).notify(anyString(), anyString());
    }

    @Test
    public void testBatchMessagesExceedsLimit() throws Exception {
        for (int i = 0; i < Bot.MESSAGE_BATCH_SIZE + 1; i++) {
            bot.onMessage("#testChannel", "TestSender", "testlogin", "testhost", "Test Message " + i);
        }

        ArgumentCaptor<String> titleCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(notificationService, timeout(100).times(1)).notify(titleCaptor.capture(), messageCaptor.capture());

        assertEquals("message", titleCaptor.getValue());
        assertTrue(messageCaptor.getValue().contains("Test Message 0"));
    }
}