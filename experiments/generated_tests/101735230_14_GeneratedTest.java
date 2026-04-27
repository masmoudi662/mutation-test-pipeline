java
package core.sensors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuickChangeFilterTest {

    private QuickChangeFilter filter;
    private int maxQueueSize = 5;
    private double acceptableChange = 0.1;

    @BeforeEach
    void setUp() {
        filter = new QuickChangeFilter(maxQueueSize, acceptableChange);
    }

    @Test
    void filterValue_fillsQueueInitially() {
        for (int i = 0; i < maxQueueSize; i++) {
            double value = i * 1.0;
            assertEquals(value, filter.filterValue(value));
        }
        assertEquals(0.0, filter.previousInput.getLast());
        assertEquals(4.0, filter.previousInput.getFirst());
    }

    @Test
    void filterValue_acceptsValueWithinAcceptableChangeOfLastReturn() {
        filterValue_fillsQueueInitially();
        double value = 4.0 + acceptableChange / 2;
        assertEquals(value, filter.filterValue(value));
    }

    @Test
    void filterValue_rejectsValueOutsideAcceptableChangeOfLastReturnButAcceptsAverage() {
        filterValue_fillsQueueInitially();
        double value = 4.0 + acceptableChange * 2;
        assertEquals(4.0, filter.filterValue(value));
        filter.previousInput.removeFirst();
        filter.previousInput.addFirst(value);
        double newValue = 2.0;
        assertEquals(newValue, filter.filterValue(newValue));
    }

    @Test
    void filterValue_rejectsValueOutsideAcceptableChangeOfLastReturnAndAverage() {
        filterValue_fillsQueueInitially();
        double value = 4.0 + acceptableChange * 2;
        assertEquals(4.0, filter.filterValue(value));
    }

    @Test
    void filterValue_negativeValues() {
        filter = new QuickChangeFilter(3, 0.1);
        assertEquals(-1.0, filter.filterValue(-1.0));
        assertEquals(-2.0, filter.filterValue(-2.0));
        assertEquals(-3.0, filter.filterValue(-3.0));
        assertEquals(-3.0, filter.filterValue(-2.5));
    }

    @Test
    void filterValue_zeroValues() {
        filter = new QuickChangeFilter(3, 0.1);
        assertEquals(0.0, filter.filterValue(0.0));
        assertEquals(0.0, filter.filterValue(0.0));
        assertEquals(0.0, filter.filterValue(0.0));
        assertEquals(0.0, filter.filterValue(0.0));
    }

    @Test
    void filterValue_largeAcceptableChange() {
        filter = new QuickChangeFilter(3, 1.0);
        assertEquals(1.0, filter.filterValue(1.0));
        assertEquals(2.0, filter.filterValue(2.0));
        assertEquals(3.0, filter.filterValue(3.0));
        assertEquals(4.0, filter.filterValue(4.0));
    }

    @Test
    void filterValue_smallAcceptableChange() {
        filter = new QuickChangeFilter(3, 0.01);
        assertEquals(1.0, filter.filterValue(1.0));
        assertEquals(1.0, filter.filterValue(1.01));
        assertEquals(1.0, filter.filterValue(1.02));
        assertEquals(1.0, filter.filterValue(1.0));
    }

    @Test
    void filterValue_equalValues() {
        filter = new QuickChangeFilter(3, 0.1);
        assertEquals(1.0, filter.filterValue(1.0));
        assertEquals(1.0, filter.filterValue(1.0));
        assertEquals(1.0, filter.filterValue(1.0));
        assertEquals(1.0, filter.filterValue(1.0));
    }
}