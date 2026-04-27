java
package com.github.jberkel.whassup.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class WhatsAppMessageTest {

    @Test
    public void testFilterPrivateBlock_null() throws Exception {
        assertNull(WhatsAppMessage.filterPrivateBlock(null));
    }

    @Test
    public void testFilterPrivateBlock_empty() throws Exception {
        assertEquals("", WhatsAppMessage.filterPrivateBlock(""));
    }

    @Test
    public void testFilterPrivateBlock_noPrivate() throws Exception {
        assertEquals("abc", WhatsAppMessage.filterPrivateBlock("abc"));
    }

    @Test
    public void testFilterPrivateBlock_private() throws Exception {
        String s = "a" + Character.toString((char)0xE000) + "b";
        assertEquals("ab", WhatsAppMessage.filterPrivateBlock(s));
    }

    @Test
    public void testFilterPrivateBlock_onlyPrivate() throws Exception {
        String s = Character.toString((char)0xE000);
        assertEquals("", WhatsAppMessage.filterPrivateBlock(s));
    }

    @Test
    public void testFilterPrivateBlock_mixedPrivate() throws Exception {
        String s = "a" + Character.toString((char)0xE000) + "b" + Character.toString((char)0xE001) + "c";
        assertEquals("abc", WhatsAppMessage.filterPrivateBlock(s));
    }

    @Test
    public void testFilterPrivateBlock_surrogatePair() throws Exception {
        String s = "a" + new String(Character.toChars(0x1F600)) + "b";
        assertEquals("a" + new String(Character.toChars(0x1F600)) + "b", WhatsAppMessage.filterPrivateBlock(s));
    }

    @Test
    public void testFilterPrivateBlock_surrogatePairAndPrivate() throws Exception {
        String s = "a" + new String(Character.toChars(0x1F600)) + Character.toString((char)0xE000) + "b";
        assertEquals("a" + new String(Character.toChars(0x1F600)) + "b", WhatsAppMessage.filterPrivateBlock(s));
    }
}