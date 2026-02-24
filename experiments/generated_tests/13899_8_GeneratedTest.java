java
package org.adbcj.mysql.codec;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class IoUtilsTest {

    @Test
    public void testSafeRead() throws IOException {
        byte[] bytes = {1, 2, 3};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.safeRead(in), 1);
        assertEquals(IoUtils.safeRead(in), 2);
        assertEquals(IoUtils.safeRead(in), 3);
    }

    @Test(expectedExceptions = EOFException.class)
    public void testSafeReadEOF() throws IOException {
        InputStream in = new ByteArrayInputStream(new byte[0]);
        IoUtils.safeRead(in);
    }

    @Test
    public void testReadShort() throws IOException {
        byte[] bytes = {0x01, 0x02};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.readShort(in), 513);
    }

    @Test
    public void testReadUnsignedShort() throws IOException {
        byte[] bytes = {0x01, 0x02};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.readUnsignedShort(in), 513);
    }

    @Test
    public void testWriteShort() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IoUtils.writeShort(out, 513);
        byte[] bytes = out.toByteArray();
        assertEquals(bytes[0], (byte) 0x01);
        assertEquals(bytes[1], (byte) 0x02);
    }

    @Test
    public void testWriteInt() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IoUtils.writeInt(out, 16909060);
        byte[] bytes = out.toByteArray();
        assertEquals(bytes[0], (byte) 0x04);
        assertEquals(bytes[1], (byte) 0x03);
        assertEquals(bytes[2], (byte) 0x02);
        assertEquals(bytes[3], (byte) 0x01);
    }

    @Test
    public void testReadUnsignedMediumInt() throws IOException {
        byte[] bytes = {0x01, 0x02, 0x03};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.readUnsignedMediumInt(in), 197121);
    }

    @Test
    public void testReadMediumInt() throws IOException {
        byte[] bytes = {0x01, 0x02, (byte) 0x80};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.readMediumInt(in), -8323071);
    }

    @Test
    public void testReadInt() throws IOException {
        byte[] bytes = {0x01, 0x02, 0x03, 0x04};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.readInt(in), 67305985);
    }
}