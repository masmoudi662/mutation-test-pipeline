java
package org.pg4200.sol01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyArrayListIntegerTest {

    @Test
    void get_validIndex() {
        MyArrayListInteger list = new MyArrayListInteger();
        list.add(10);
        list.add(20);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
    }

    @Test
    void get_invalidIndex_negative() {
        MyArrayListInteger list = new MyArrayListInteger();
        list.add(10);
        assertNull(list.get(-1));
    }

    @Test
    void get_invalidIndex_outOfBounds() {
        MyArrayListInteger list = new MyArrayListInteger();
        list.add(10);
        assertNull(list.get(1));
    }

    @Test
    void get_emptyList() {
        MyArrayListInteger list = new MyArrayListInteger();
        assertNull(list.get(0));
    }

    @Test
    void get_multipleElements() {
        MyArrayListInteger list = new MyArrayListInteger();
        list.add(5);
        list.add(10);
        list.add(15);
        assertEquals(5, list.get(0));
        assertEquals(10, list.get(1));
        assertEquals(15, list.get(2));
    }

    @Test
    void get_largeIndex() {
        MyArrayListInteger list = new MyArrayListInteger();
        for (int i = 0; i < 100; i++) {
            list.add(i * 2);
        }
        assertEquals(196, list.get(98));
        assertEquals(198, list.get(99));
        assertNull(list.get(100));
    }

    @Test
    void get_afterRemove() {
        MyArrayListInteger list = new MyArrayListInteger();
        list.add(1);
        list.add(2);
        list.remove(0);
        assertEquals(2, list.get(0));
        assertNull(list.get(1));
    }
}