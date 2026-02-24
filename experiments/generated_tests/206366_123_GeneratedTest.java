java
package org.apache.mina.coap.codec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.coap.CoapMessage;
import org.apache.mina.coap.CoapOption;
import org.apache.mina.coap.CoapOptionType;
import org.apache.mina.coap.CoapType;
import org.junit.Test;

public class CoapEncoderTest {

    @Test
    public void testEncodeEmptyMessage() {
        CoapEncoder encoder = new CoapEncoder();
        CoapMessage message = new CoapMessage(CoapType.CONFIRMABLE, 1, 1234);
        message.setToken(new byte[0]);
        ByteBuffer buffer = encoder.encode(message, null);
        assertNotNull(buffer);
        assertEquals(4, buffer.limit());
    }

    @Test
    public void testEncodeMessageWithToken() {
        CoapEncoder encoder = new CoapEncoder();
        CoapMessage message = new CoapMessage(CoapType.CONFIRMABLE, 1, 1234);
        message.setToken(new byte[] { 0x01, 0x02 });
        ByteBuffer buffer = encoder.encode(message, null);
        assertNotNull(buffer);
        assertEquals(6, buffer.limit());
    }

    @Test
    public void testEncodeMessageWithOptions() {
        CoapEncoder encoder = new CoapEncoder();
        CoapMessage message = new CoapMessage(CoapType.CONFIRMABLE, 1, 1234);
        message.setToken(new byte[0]);

        List<CoapOption> options = new ArrayList<>();
        options.add(new CoapOption(CoapOptionType.URI_PATH, "test".getBytes()));
        message.setOptions(options);

        ByteBuffer buffer = encoder.encode(message, null);
        assertNotNull(buffer);
    }

    @Test
    public void testEncodeMessageWithPayload() {
        CoapEncoder encoder = new CoapEncoder();
        CoapMessage message = new CoapMessage(CoapType.CONFIRMABLE, 1, 1234);
        message.setToken(new byte[0]);
        message.setPayload("payload".getBytes());

        ByteBuffer buffer = encoder.encode(message, null);
        assertNotNull(buffer);
    }

    @Test
    public void testEncodeCompleteMessage() {
        CoapEncoder encoder = new CoapEncoder();
        CoapMessage message = new CoapMessage(CoapType.CONFIRMABLE, 1, 1234);
        message.setToken(new byte[] { 0x01, 0x02 });

        List<CoapOption> options = new ArrayList<>();
        options.add(new CoapOption(CoapOptionType.URI_PATH, "test".getBytes()));
        message.setOptions(options);

        message.setPayload("payload".getBytes());

        ByteBuffer buffer = encoder.encode(message, null);
        assertNotNull(buffer);
    }

    @Test
    public void testCreateEncoderState() {
        CoapEncoder encoder = new CoapEncoder();
        Void state = encoder.createEncoderState();
        assertEquals(null, state);
    }
}