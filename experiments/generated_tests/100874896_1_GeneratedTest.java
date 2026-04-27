java
package io.jpower.kcp.netty.internal;

import org.junit.Test;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ReItrLinkedListTest {

    @Test
    public void testListIterator() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        ReusableListIterator<Integer> iterator = list.listIterator();
        assertNotNull(iterator);
    }

    @Test
    public void testListIteratorWithIndex() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        ReusableListIterator<Integer> iterator = list.listIterator(1);
        assertNotNull(iterator);
        assertEquals(2, (int) iterator.next());
    }

    @Test
    public void testNextAndPrevious() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        list.add(1);
        list.add(2);
        list.add(3);

        ReusableListIterator<Integer> iterator = list.listIterator();

        assertEquals(1, (int) iterator.next());
        assertEquals(2, (int) iterator.next());
        assertEquals(3, (int) iterator.next());

        assertTrue(iterator.hasPrevious());
        assertEquals(3, (int) iterator.previous());
        assertEquals(2, (int) iterator.previous());
        assertEquals(1, (int) iterator.previous());

        assertFalse(iterator.hasPrevious());

    }

    @Test
    public void testHasNextAndHasPrevious() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        list.add(1);
        list.add(2);

        ReusableListIterator<Integer> iterator = list.listIterator();

        assertTrue(iterator.hasNext());
        assertFalse(iterator.hasPrevious());

        iterator.next();
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasPrevious());

        iterator.next();
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextThrowsException() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        ReusableListIterator<Integer> iterator = list.listIterator();
        iterator.next();
    }

    @Test(expected = NoSuchElementException.class)
    public void testPreviousThrowsException() {
        ReItrLinkedList<Integer> list = new ReItrLinkedList<>();
        ReusableListIterator<Integer> iterator = list.listIterator();
        iterator.previous();
    }
}