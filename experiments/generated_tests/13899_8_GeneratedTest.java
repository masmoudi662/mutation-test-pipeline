java
package org.adbcj.mysql.codec;

import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class IoUtilsTest {

    @Test
    public void testSafeRead_validByte() throws IOException {
        byte[] bytes = {10};
        InputStream in = new ByteArrayInputStream(bytes);
        int result = IoUtils.safeRead(in);
        assertEquals(result, 10);
    }

    @Test(expectedExceptions = EOFException.class)
    public void testSafeRead_endOfStream() throws IOException {
        byte[] bytes = {};
        InputStream in = new ByteArrayInputStream(bytes);
        IoUtils.safeRead(in);
    }

    @Test
    public void testSafeRead_multipleBytes() throws IOException {
        byte[] bytes = {1, 2, 3};
        InputStream in = new ByteArrayInputStream(bytes);
        assertEquals(IoUtils.safeRead(in), 1);
        assertEquals(IoUtils.safeRead(in), 2);
        assertEquals(IoUtils.safeRead(in), 3);
        try {
            IoUtils.safeRead(in);
            fail("Expected EOFException");
        } catch (EOFException e) {
            // Expected
        }
    }

    @Test
    public void testSafeRead_maxByteValue() throws IOException {
        byte[] bytes = {(byte) 255};
        InputStream in = new ByteArrayInputStream(bytes);
        int result = IoUtils.safeRead(in);
        assertEquals(result, 255);
    }

    @Test
    public void testSafeRead_zeroByte() throws IOException {
        byte[] bytes = {0};
        InputStream in = new ByteArrayInputStream(bytes);
        int result = IoUtils.safeRead(in);
        assertEquals(result, 0);
    }
}