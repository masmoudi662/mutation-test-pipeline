java
package org.apache.hive.hcatalog.templeton.tool;

import org.junit.Test;

import static org.junit.Assert.*;

public class TempletonUtilsTest {

  @Test
  public void testIsset_null() {
    assertFalse(TempletonUtils.isset(null));
  }

  @Test
  public void testIsset_empty() {
    assertFalse(TempletonUtils.isset(""));
  }

  @Test
  public void testIsset_whitespace() {
    assertTrue(TempletonUtils.isset(" "));
  }

  @Test
  public void testIsset_nonEmpty() {
    assertTrue(TempletonUtils.isset("test"));
  }

  @Test
  public void testIsset_longString() {
    assertTrue(TempletonUtils.isset("This is a long string"));
  }
}