java
package by.bsu.onewire.common.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class AddressUtilsTest {

    @Test
    public void testToLongValidAddress() {
        String address = "28FFB0410500009E";
        long expected = 3094850247436127390L;
        long actual = AddressUtils.toLong(address);
        assertEquals(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void testToLongNullAddress() {
        AddressUtils.toLong(null);
    }

    @Test(expected = StringIndexOutOfBoundsException.class)
    public void testToLongShortAddress() {
        String address = "28FFB041050000";
        AddressUtils.toLong(address);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToLongInvalidCharacter() {
        String address = "28FFB0410500009G";
        AddressUtils.toLong(address);
    }

    @Test
    public void testToLongAllZeros() {
        String address = "0000000000000000";
        long expected = 0L;
        long actual = AddressUtils.toLong(address);
        assertEquals(expected, actual);
    }

    @Test
    public void testToLongAllF() {
        String address = "FFFFFFFFFFFFFFFF";
        long expected = -1L;
        long actual = AddressUtils.toLong(address);
        assertEquals(expected, actual);
    }

    @Test
    public void testToLongMixedCase() {
        String address = "28fFb0410500009e";
        long expected = 3094850247436127390L;
        long actual = AddressUtils.toLong(address);
        assertEquals(expected, actual);
    }

    @Test
    public void testToLongLeadingZeros() {
        String address = "00000028FFB04105";
        long expected = 0x28FFB04105L;
        long actual = AddressUtils.toLong(address);
    }
}