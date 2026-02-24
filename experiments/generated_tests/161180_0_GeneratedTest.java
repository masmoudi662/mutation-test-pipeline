java
package org.jredis.ri.alphazero.support;

import org.junit.Test;
import static org.junit.Assert.*;

public class ConvertTest {

    @Test
    public void testToBytesIntSmallPositive() {
        byte[] bytes = Convert.toBytes(123);
        assertArrayEquals("123".getBytes(), bytes);
    }

    @Test
    public void testToBytesIntSmallNegative() {
        byte[] bytes = Convert.toBytes(-123);
        assertArrayEquals("-123".getBytes(), bytes);
    }

    @Test
    public void testToBytesIntLargePositive() {
        byte[] bytes = Convert.toBytes(Integer.MAX_VALUE);
        assertArrayEquals(Integer.toString(Integer.MAX_VALUE).getBytes(), bytes);
    }

    @Test
    public void testToBytesIntLargeNegative() {
        byte[] bytes = Convert.toBytes(Integer.MIN_VALUE);
        assertArrayEquals(Integer.toString(Integer.MIN_VALUE).getBytes(), bytes);
    }

    @Test
    public void testToBytesLongSmallPositive() {
        byte[] bytes = Convert.toBytes(123L);
        assertArrayEquals("123".getBytes(), bytes);
    }

    @Test
    public void testToBytesLongLargePositive() {
        byte[] bytes = Convert.toBytes(Long.MAX_VALUE);
        assertArrayEquals(Long.toString(Long.MAX_VALUE).getBytes(), bytes);
    }

    @Test
    public void testToIntValidPositive() {
        int result = Convert.toInt("123".getBytes());
        assertEquals(123, result);
    }
    
    @Test
    public void testToIntValidNegative() {
        int result = Convert.toInt("-123".getBytes());
        assertEquals(-123, result);
    }

    @Test
    public void testToLongValidPositive() {
        long result = Convert.toLong("1234567890123".getBytes());
        assertEquals(1234567890123L, result);
    }

    @Test
    public void testToDouble() {
        double result = Convert.toDouble("123.45".getBytes());
        assertEquals(123.45, result, 0.001);
    }
}