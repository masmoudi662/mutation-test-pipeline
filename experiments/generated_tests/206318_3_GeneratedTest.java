java
package org.apache.fop.afp.modca;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractNamedAFPObjectTest {

    private static class TestNamedAFPObject extends AbstractNamedAFPObject {
        TestNamedAFPObject(String name) {
            super(name);
        }
    }

    @Test
    public void testGetNameBytes_shortName() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("ABC");
        byte[] nameBytes = obj.getNameBytes();
        assertNotNull(nameBytes);
        assertEquals(8, nameBytes.length);
        String name = new String(nameBytes);
        assertEquals("ABC     ", name);
    }

    @Test
    public void testGetNameBytes_exactName() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("ABCDEFGH");
        byte[] nameBytes = obj.getNameBytes();
        assertNotNull(nameBytes);
        assertEquals(8, nameBytes.length);
        String name = new String(nameBytes);
        assertEquals("ABCDEFGH", name);
    }

    @Test
    public void testGetNameBytes_longName() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("ABCDEFGHIJKL");
        byte[] nameBytes = obj.getNameBytes();
        assertNotNull(nameBytes);
        assertEquals(8, nameBytes.length);
        String name = new String(nameBytes);
        assertEquals("IJKL", name);
    }

    @Test
    public void testGetName() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("TestName");
        assertEquals("TestName", obj.getName());
    }

    @Test
    public void testSetName() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("InitialName");
        obj.setName("NewName");
        assertEquals("NewName", obj.getName());
    }

    @Test
    public void testToString() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("ToStringTest");
        assertEquals("ToStringTest", obj.toString());
    }

    @Test
    public void testGetNameLength() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("TestName");
        assertEquals(8, obj.getNameLength());
    }

    @Test
    public void testCopySF() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject("TEST");
        byte[] data = new byte[20];
        obj.copySF(data, (byte) 0x01, (byte) 0x02);
        byte[] nameBytes = obj.getNameBytes();
        for (int i = 0; i < nameBytes.length; i++) {
            assertEquals(nameBytes[i], data[9 + i]);
        }
    }

    @Test
    public void testConstructor() {
        AbstractNamedAFPObject obj = new TestNamedAFPObject();
        assertTrue(obj instanceof AbstractNamedAFPObject);
    }
}