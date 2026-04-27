java
package com.annimon.hotarufx.lexer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotaruLexerTest {

    @Test
    public void testTokenizeText_SimpleString() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"hello\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("hello", token.text());
        assertEquals(7, token.length());
    }

    @Test
    public void testTokenizeText_EscapedQuote() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"hello \\\" world\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("hello \" world", token.text());
        assertEquals(16, token.length());
    }

    @Test
    public void testTokenizeText_EscapedSpecialChars() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\0\\b\\f\\n\\r\\t\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("\0\b\f\n\r\t", token.text());
        assertEquals(14, token.length());
    }

    @Test
    public void testTokenizeText_UnicodeEscape() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\u0041\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("A", token.text());
        assertEquals(8, token.length());
    }

    @Test
    public void testTokenizeText_UnicodeEscapeMultipleU() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\uuuu0041\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("A", token.text());
        assertEquals(10, token.length());
    }

    @Test
    public void testTokenizeText_UnicodeEscapeInvalid() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\u004\"");
        assertThrows(RuntimeException.class, () -> lexer.tokenizeText('"'));
    }

    @Test
    public void testTokenizeText_BackslashAlone() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\\\\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("\\", token.text());
        assertEquals(4, token.length());
    }

    @Test
    public void testTokenizeText_UnterminatedString() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"hello");
        assertThrows(RuntimeException.class, () -> lexer.tokenizeText('"'));
    }

    @Test
    public void testTokenizeText_EmptyString() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("", token.text());
        assertEquals(2, token.length());
    }

    @Test
    public void testTokenizeText_BackslashFollowedByRandomChar() {
        HotaruLexer lexer = new HotaruLexer();
        lexer.setText("\"\\g\"");
        Token token = lexer.tokenizeText('"');
        assertEquals(HotaruTokenId.TEXT, token.id());
        assertEquals("\\g", token.text());
        assertEquals(4, token.length());
    }
}