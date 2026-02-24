java
package by.bsu.onewire.common.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class AddressUtilsTest {

    @Test
    public void testToString() {
        assertEquals("0000000000000001", AddressUtils.toString(1));
        assertEquals("FFFFFFFFFFFFFFFF", AddressUtils.toString(-1));
        assertEquals("0000000000000000", AddressUtils.toString(0));
        assertEquals("00000000000000FF", AddressUtils.toString(255));
    }

    @Test
    public void testToLong() {
        assertEquals(1, AddressUtils.toLong("0000000000000001"));
        assertEquals(255, AddressUtils.toLong("00000000000000FF"));
        assertEquals(0, AddressUtils.toLong("0000000000000000"));
        assertEquals(0x123456789ABCDEF0L, AddressUtils.toLong("123456789ABCDEF0"));
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testToLongInvalidLengthShort() {
        AddressUtils.toLong("1234");
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testToLongInvalidLengthLong() {
        AddressUtils.toLong("12345678901234567");
    }

    @Test
    public void testToStringAndToLongCombination() {
        long originalAddress = 0x1A2B3C4D5E6F7089L;
        String addressString = AddressUtils.toString(originalAddress);
        long convertedAddress = AddressUtils.toLong(addressString);
        assertEquals(originalAddress, convertedAddress);
    }

    @Test
    public void testToStringWithLargeHex() {
        assertEquals("FFFFFFFFFFFFFFFF", AddressUtils.toString(-1L));
    }

    @Test
    public void testToLongWithLeadingZeros() {
        assertEquals(10, AddressUtils.toLong("000000000000000A"));
    }
}