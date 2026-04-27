java
package minium;

import minium.internal.InternalOffsets;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OffsetsTest {

    @Test
    public void testAt() {
        Offset offset = Offsets.at(HorizontalOffset.CENTERED, VerticalOffset.TOP);
        assertNotNull(offset);
    }

    @Test
    public void testAtWithLeftTop() {
        Offset offset = Offsets.at(HorizontalOffset.LEFT, VerticalOffset.TOP);
        assertNotNull(offset);
    }

    @Test
    public void testAtWithRightBottom() {
        Offset offset = Offsets.at(HorizontalOffset.RIGHT, VerticalOffset.BOTTOM);
        assertNotNull(offset);
    }

    @Test
    public void testAtWithCenteredBottom() {
        Offset offset = Offsets.at(HorizontalOffset.CENTERED, VerticalOffset.BOTTOM);
        assertNotNull(offset);
    }

    @Test
    public void testAtWithLeftCentered() {
        Offset offset = Offsets.at(HorizontalOffset.LEFT, VerticalOffset.CENTERED);
        assertNotNull(offset);
    }
}