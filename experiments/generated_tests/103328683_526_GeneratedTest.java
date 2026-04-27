java
package com.fsck.k9.mail;

import com.fsck.k9.mail.ServerSettings.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TransportUrisTest {

    @Test
    public void decodeTransportUri_smtp() {
        ServerSettings settings = TransportUris.decodeTransportUri("smtp://user:password@server:123");
        assertEquals(Type.SMTP, settings.type);
    }

    @Test
    public void decodeTransportUri_webdav() {
        ServerSettings settings = TransportUris.decodeTransportUri("webdav://user:password@server:123");
        assertEquals(Type.WebDAV, settings.type);
    }

    @Test
    public void decodeTransportUri_ews() {
        ServerSettings settings = TransportUris.decodeTransportUri("ews://user:password@server:123");
        assertEquals(Type.EWS, settings.type);
    }

    @Test
    public void decodeTransportUri_invalid() {
        assertThrows(IllegalArgumentException.class, () -> TransportUris.decodeTransportUri("invalid://user:password@server:123"));
    }

    @Test
    public void decodeTransportUri_smtp_startTlsRequired() {
        ServerSettings settings = TransportUris.decodeTransportUri("smtp+tls://user:password@server:123");
        assertEquals(Type.SMTP, settings.type);
    }

    @Test
    public void decodeTransportUri_smtp_sslRequired() {
        ServerSettings settings = TransportUris.decodeTransportUri("smtp+ssl+trustallcerts://user:password@server:123");
        assertEquals(Type.SMTP, settings.type);
    }
}