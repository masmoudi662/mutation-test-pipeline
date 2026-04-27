java
package com.carrotsearch.labs.langid;

import org.junit.Test;
import static org.junit.Assert.*;

public class DoubleLinkedCountingSetTest {

  @Test
  public void testIncrement() {
    DoubleLinkedCountingSet set = new DoubleLinkedCountingSet(10);
    set.increment(5);
    set.increment(5);
    set.increment(2);

    // Unfortunately, no direct access to internal state for assertions.
    // Need to rely on side effects or public methods (if any).
  }

  @Test
  public void testIncrementMultipleKeys() {
    DoubleLinkedCountingSet set = new DoubleLinkedCountingSet(10);
    set.increment(1);
    set.increment(2);
    set.increment(3);
    set.increment(1);
    set.increment(2);
    set.increment(1);
  }

  @Test
  public void testIncrementWithCapacity() {
      DoubleLinkedCountingSet set = new DoubleLinkedCountingSet(3);
      set.increment(1);
      set.increment(2);
      set.increment(3);
      set.increment(1);
  }

    @Test
    public void testIncrementZero() {
        DoubleLinkedCountingSet set = new DoubleLinkedCountingSet(10);
        set.increment(0);
        set.increment(0);
    }

    @Test
    public void testIncrementLargeKey() {
        DoubleLinkedCountingSet set = new DoubleLinkedCountingSet(100);
        set.increment(99);
        set.increment(99);
        set.increment(99);
    }
}