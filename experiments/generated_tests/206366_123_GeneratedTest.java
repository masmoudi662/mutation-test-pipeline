java
package org.apache.mina.coap.codec;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.mina.coap.CoapMessage;
import org.apache.mina.coap.CoapOption;
import org.apache.mina.coap.CoapOptionType;
import org.apache.mina.coap.CoapRequest;
import org.apache.mina.coap.CoapResponse;
import org.apache.mina.coap.CoapType;
import org.junit.Test;

public class CoapEncoderTest {

    private final CoapEncoder encoder = new CoapEncoder();

    @Test
    public void testEncodeEmptyMessage() {
        CoapMessage message = new CoapRequest(CoapType.CON, 1, 12345, "token".getBytes());
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x44, 0x01, 0x30, 0x39, 't', 'o', 'k', 'e', 'n' };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithPayload() {
        CoapMessage message = new CoapResponse(CoapType.ACK, 205, 12345, "token".getBytes());
        message.setPayload("payload".getBytes());
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x64, (byte) 205, 0x30, 0x39, 't', 'o', 'k', 'e', 'n', (byte) 0xFF, 'p', 'a', 'y', 'l', 'o',
                'a', 'd' };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithOptions() {
        CoapMessage message = new CoapRequest(CoapType.NON, 2, 12345, new byte[0]);
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, "path".getBytes()));
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x50, 0x02, 0x30, 0x39, 0x34, 'p', 'a', 't', 'h' };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithMultipleOptions() {
        CoapMessage message = new CoapRequest(CoapType.NON, 2, 12345, new byte[0]);
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, "path1".getBytes()));
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, "path2".getBytes()));
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x50, 0x02, 0x30, 0x39, 0x35, 'p', 'a', 't', 'h', '1', 0x05, 'p', 'a', 't', 'h', '2' };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithDeltaEncoding() {
        CoapMessage message = new CoapRequest(CoapType.NON, 2, 12345, new byte[0]);
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, "path".getBytes()));
        message.addOption(new CoapOption(CoapOptionType.CONTENT_FORMAT, new byte[] { 0 }));
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x50, 0x02, 0x30, 0x39, 0x34, 'p', 'a', 't', 'h', 0x01, 0x00 };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithExtendedDelta() {
        CoapMessage message = new CoapRequest(CoapType.NON, 2, 12345, new byte[0]);
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, "path".getBytes()));
        message.addOption(new CoapOption(CoapOptionType.MAX_AGE, new byte[] { 0, 1 }));
        ByteBuffer buffer = encoder.encode(message, null);
        byte[] expected = { 0x50, 0x02, 0x30, 0x39, 0x34, 'p', 'a', 't', 'h', (byte) 0xD2, (byte) 0x0E, 0x00, 0x01 };
        assertEquals(ByteBuffer.wrap(expected), buffer);
    }

    @Test
    public void testEncodeMessageWithLargeOptionValue() {
        CoapMessage message = new CoapRequest(CoapType.NON, 2, 12345, new byte[0]);
        byte[] longValue = new byte[270];
        Arrays.fill(longValue, (byte) 'a');
        message.addOption(new CoapOption(CoapOptionType.URI_PATH, longValue));
        ByteBuffer buffer = encoder.encode(message, null);
        assertEquals(5 + 2 + 270, buffer.limit());
    }
}