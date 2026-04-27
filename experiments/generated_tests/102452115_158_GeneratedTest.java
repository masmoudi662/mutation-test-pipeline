java
package nl.tudelft.broccoli.core.track;

import nl.tudelft.broccoli.core.Marble;
import nl.tudelft.broccoli.core.grid.Direction;
import nl.tudelft.broccoli.core.grid.Tile;
import nl.tudelft.broccoli.core.grid.Tileable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OneWayTrackTest {

    private OneWayTrack oneWayTrack;
    private Track track;

    @BeforeEach
    void setUp() {
        track = mock(Track.class);
        oneWayTrack = new OneWayTrack();
        oneWayTrack.track = track;
    }

    @Test
    void isConnected() {
        when(track.isConnected()).thenReturn(true);
        assertTrue(oneWayTrack.isConnected());

        when(track.isConnected()).thenReturn(false);
        assertFalse(oneWayTrack.isConnected());
    }

    @Test
    void add() {
    }

    @Test
    void remove() {
    }

    @Test
    void getDirection() {
    }

    @Test
    void getMarble() {
    }

    @Test
    void rotate() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }
}