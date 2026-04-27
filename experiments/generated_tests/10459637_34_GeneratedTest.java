java
package net.floodlightcontroller.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class MACAddressTest {

    @Test
    public void testValueOfValidAddress() {
        MACAddress macAddress = MACAddress.valueOf("00:11:22:33:44:55");
        assertNotNull(macAddress);
        assertEquals(0x00, macAddress.toBytes()[0] & 0xFF);
        assertEquals(0x11, macAddress.toBytes()[1] & 0xFF);
        assertEquals(0x22, macAddress.toBytes()[2] & 0xFF);
        assertEquals(0x33, macAddress.toBytes()[3] & 0xFF);
        assertEquals(0x44, macAddress.toBytes()[4] & 0xFF);
        assertEquals(0x55, macAddress.toBytes()[5] & 0xFF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidAddressTooShort() {
        MACAddress.valueOf("00:11:22:33:44");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidAddressTooLong() {
        MACAddress.valueOf("00:11:22:33:44:55:66");
    }

    @Test(expected = NumberFormatException.class)
    public void testValueOfInvalidAddressNonHexCharacter() {
        MACAddress.valueOf("00:11:22:33:44:GG");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidAddressNull() {
        MACAddress.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidAddressEmptyString() {
        MACAddress.valueOf("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalidAddressIncorrectSeparator() {
        MACAddress.valueOf("00-11-22-33-44-55");
    }

    @Test
    public void testValueOfValidAddressUppercase() {
        MACAddress macAddress = MACAddress.valueOf("AA:BB:CC:DD:EE:FF");
        assertNotNull(macAddress);
        assertEquals(0xAA, macAddress.toBytes()[0] & 0xFF);
        assertEquals(0xBB, macAddress.toBytes()[1] & 0xFF);
        assertEquals(0xCC, macAddress.toBytes()[2] & 0xFF);
        assertEquals(0xDD, macAddress.toBytes()[3] & 0xFF);
        assertEquals(0xEE, macAddress.toBytes()[4] & 0xFF);
        assertEquals(0xFF, macAddress.toBytes()[5] & 0xFF);
    }

    @Test
    public void testValueOfValidAddressMixedCase() {
        MACAddress macAddress = MACAddress.valueOf("Aa:Bb:Cc:Dd:Ee:Ff");
        assertNotNull(macAddress);
        assertEquals(0xAA, macAddress.toBytes()[0] & 0xFF);
        assertEquals(0xBB, macAddress.toBytes()[1] & 0xFF);
        assertEquals(0xCC, macAddress.toBytes()[2] & 0xFF);
        assertEquals(0xDD, macAddress.toBytes()[3] & 0xFF);
        assertEquals(0xEE, macAddress.toBytes()[4] & 0xFF);
        assertEquals(0xFF, macAddress.toBytes()[5] & 0xFF);
    }
}