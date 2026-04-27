java
package org.gdg.frisbee.android.eventseries;

import org.gdg.frisbee.android.api.model.Event;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class EventDateComparatorTest {

    private EventDateComparator comparator;

    @Mock
    private EventAdapter.Item item1;
    @Mock
    private EventAdapter.Item item2;
    @Mock
    private Event event1;
    @Mock
    private Event event2;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        comparator = new EventDateComparator();
        when(item1.getEvent()).thenReturn(event1);
        when(item2.getEvent()).thenReturn(event2);
    }

    @Test
    public void compare_bothNull() {
        when(event1.getStart()).thenReturn(null);
        when(event2.getStart()).thenReturn(null);
        assertEquals(0, comparator.compare(item1, item2));
    }

    @Test
    public void compare_event1Null() {
        when(event1.getStart()).thenReturn(null);
        when(event2.getStart()).thenReturn(DateTime.now());
        assertEquals(1, comparator.compare(item1, item2));
    }

    @Test
    public void compare_event2Null() {
        when(event1.getStart()).thenReturn(DateTime.now());
        when(event2.getStart()).thenReturn(null);
        assertEquals(-1, comparator.compare(item1, item2));
    }

    @Test
    public void compare_event1BeforeEvent2() {
        DateTime now = DateTime.now();
        when(event1.getStart()).thenReturn(now.minusDays(1));
        when(event2.getStart()).thenReturn(now);
        assertEquals(-1, comparator.compare(item1, item2));
    }

    @Test
    public void compare_event1AfterEvent2() {
        DateTime now = DateTime.now();
        when(event1.getStart()).thenReturn(now.plusDays(1));
        when(event2.getStart()).thenReturn(now);
        assertEquals(1, comparator.compare(item1, item2));
    }

    @Test
    public void compare_sameTime() {
        DateTime now = DateTime.now();
        when(event1.getStart()).thenReturn(now);
        when(event2.getStart()).thenReturn(now);
        assertEquals(0, comparator.compare(item1, item2));
    }
}