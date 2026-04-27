java
package org.ObjectLayout.examples;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PointArrayTest {

    @Test
    void newInstance() {
        PointArray array = PointArray.newInstance(10);
        assertNotNull(array);
        assertEquals(10, array.getLength());
    }

    @Test
    void newInstance_zeroLength() {
        PointArray array = PointArray.newInstance(0);
        assertNotNull(array);
        assertEquals(0, array.getLength());
    }

    @Test
    void setAndGetPoint() {
        PointArray array = PointArray.newInstance(1);
        Point p = new Point(1, 2);
        array.set(0, p);
        Point retrieved = array.get(0);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.x);
        assertEquals(2, retrieved.y);
    }

    @Test
    void getPoint_outOfBounds() {
        PointArray array = PointArray.newInstance(1);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
    }

    @Test
    void setPoint_outOfBounds() {
        PointArray array = PointArray.newInstance(1);
        assertThrows(IndexOutOfBoundsException.class, () -> array.set(1, new Point(1,1)));
    }
}