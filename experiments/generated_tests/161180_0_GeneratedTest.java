java
package org.jredis.ri.alphazero.support;

import org.jredis.RedisException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ConvertTest {

    @Test
    public void testToBytesPositiveSmall() {
        byte[] result = Convert.toBytes(10);
        assertNotNull(result);
        assertEquals("10", new String(result));
    }

    @Test
    public void testToBytesNegativeSmall() {
        byte[] result = Convert.toBytes(-10);
        assertNotNull(result);
        assertEquals("-10", new String(result));
    }

    @Test
    public void testToBytesPositiveLarge() {
        byte[] result = Convert.toBytes(70000);
        assertNotNull(result);
        assertEquals("70000", new String(result));
    }

    @Test
    public void testToBytesNegativeLarge() {
        byte[] result = Convert.toBytes(-70000);
        assertNotNull(result);
        assertEquals("-70000", new String(result));
    }

    @Test
    public void testToBytesZero() {
        byte[] result = Convert.toBytes(0);
        assertNotNull(result);
        assertEquals("0", new String(result));
    }

    @Test
    public void testToBytesMaxInt() {
        byte[] result = Convert.toBytes(Integer.MAX_VALUE);
        assertNotNull(result);
        assertEquals(String.valueOf(Integer.MAX_VALUE), new String(result));
    }

    @Test
    public void testToBytesMinInt() {
        byte[] result = Convert.toBytes(Integer.MIN_VALUE);
        assertNotNull(result);
        assertEquals(String.valueOf(Integer.MIN_VALUE), new String(result));
    }

    @Test
    public void testToBytesPositiveCache() {
        byte[] result1 = Convert.toBytes(1000);
        byte[] result2 = Convert.toBytes(1000);
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("1000", new String(result1));
        assertEquals("1000", new String(result2));
    }

    @Test
    public void testToBytesNegativeCache() {
        byte[] result1 = Convert.toBytes(-1000);
        byte[] result2 = Convert.toBytes(-1000);
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("-1000", new String(result1));
        assertEquals("-1000", new String(result2));
    }
}