java
package org.frameworkset.elasticsearch.serial;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CharEscapeUtilTest {

    @Test
    public void testEscape_emptyString() {
        assertEquals("", CharEscapeUtil.escape(""));
    }

    @Test
    public void testEscape_noSpecialCharacters() {
        assertEquals("abc", CharEscapeUtil.escape("abc"));
    }

    @Test
    public void testEscape_equals() {
        assertEquals("\\=", CharEscapeUtil.escape("="));
    }

    @Test
    public void testEscape_backslash() {
        assertEquals("\\\\", CharEscapeUtil.escape("\\"));
    }

    @Test
    public void testEscape_plus() {
        assertEquals("\\+", CharEscapeUtil.escape("+"));
    }

    @Test
    public void testEscape_minus() {
        assertEquals("\\-", CharEscapeUtil.escape("-"));
    }

    @Test
    public void testEscape_exclamation() {
        assertEquals("\\!", CharEscapeUtil.escape("!"));
    }

    @Test
    public void testEscape_parentheses() {
        assertEquals("\\(\\)", CharEscapeUtil.escape("()"));
    }

    @Test
    public void testEscape_complexString() {
        assertEquals("abc\\=\\+\\-\\!\\(\\)\\{\\}\\~\\*\\?\\|\\&\\/\\\\:\\[\\]\\\"\\^def", CharEscapeUtil.escape("abc=+-!(){}~*?|&/\\:[]\"^def"));
    }

    @Test
    public void testEscape_slashes() {
        assertEquals("\\/", CharEscapeUtil.escape("/"));
    }
}