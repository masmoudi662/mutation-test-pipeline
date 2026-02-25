java
package org.apache.fop.afp.modca;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class AbstractNamedAFPObjectTest {

    private static class MockAbstractNamedAFPObject extends AbstractNamedAFPObject {
        private final String name;

        public MockAbstractNamedAFPObject(String name) {
            super(name);
            this.name = name;
        }

        @Override
        public int getDataLength() {
            return 0;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    @Test
    public void testCopySF() {
        String name = "TESTNAME";
        MockAbstractNamedAFPObject obj = new MockAbstractNamedAFPObject(name);
        byte[] data = new byte[17]; // 8 (base) + 8 (name) + 1 (pad)
        byte type = (byte) 0xD3;
        byte category = (byte) 0xA8;

        obj.copySF(data, type, category);

        byte[] expected = new byte[]{
                (byte) 0xD3, (byte) 0xA8, 0x00, 0x11, (byte) 0xD3, (byte) 0xA8, 0x00, 0x00, 0x00,
                'T', 'E', 'S', 'T', 'N', 'A', 'M', 'E'
        };
        assertArrayEquals(expected, data);
    }

    @Test
    public void testGetNameBytes() throws Exception {
        String name = "TEST";
        MockAbstractNamedAFPObject obj = new MockAbstractNamedAFPObject(name);
        byte[] nameBytes = obj.getNameBytes();
        byte[] expected = name.getBytes("Cp500");
        assertArrayEquals(expected, nameBytes);
    }

    @Test
    public void testGetNameBytesPadded() throws Exception {
        String name = "TEST";
        MockAbstractNamedAFPObject obj = new MockAbstractNamedAFPObject(name);
        byte[] paddedNameBytes = obj.getNameBytesPadded(8);
        assertEquals(8, paddedNameBytes.length);
        assertEquals((byte) 'T', paddedNameBytes[0]);
        assertEquals((byte) 'E', paddedNameBytes[1]);
        assertEquals((byte) 'S', paddedNameBytes[2]);
        assertEquals((byte) 'T', paddedNameBytes[3]);
        assertEquals((byte) 0x40, paddedNameBytes[4]);
        assertEquals((byte) 0x40, paddedNameBytes[5]);
        assertEquals((byte) 0x40, paddedNameBytes[6]);
        assertEquals((byte) 0x40, paddedNameBytes[7]);
    }
}