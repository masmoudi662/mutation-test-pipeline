java
package com.pinterest.yuvi.bitstream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class BitStreamTest {

  @Test
  public void testWrite() {
    BitStream bitStream = new BitStream(10);
    bitStream.write(3, 5);
    bitStream.write(3, 2);
    bitStream.write(3, 7);
    assertEquals(3, bitStream.getIndex());
    assertEquals(9, bitStream.getShift());
  }

  @Test
  public void testWriteInvalidN() {
    BitStream bitStream = new BitStream(1);
    assertThrows(IllegalArgumentException.class, () -> bitStream.write(0, 1));
    assertThrows(IllegalArgumentException.class, () -> bitStream.write(65, 1));
  }

  @Test
  public void testWriteCrossBoundary() {
    BitStream bitStream = new BitStream(2);
    bitStream.write(60, 1);
    bitStream.write(10, 2);
    assertEquals(2, bitStream.getIndex());
    assertEquals(6, bitStream.getShift());
  }

  @Test
  public void testWriteMultipleBlocks() {
    BitStream bitStream = new BitStream(4);
    bitStream.write(64, 1);
    bitStream.write(64, 2);
    bitStream.write(64, 3);
    assertEquals(3, bitStream.getIndex());
    assertEquals(0, bitStream.getShift());
  }

  @Test
  public void testWriteLargeValue() {
    BitStream bitStream = new BitStream(1);
    bitStream.write(1, 1);
  }

  @Test
  public void testWriteZeroValue() {
    BitStream bitStream = new BitStream(1);
    bitStream.write(1, 0);
  }
}