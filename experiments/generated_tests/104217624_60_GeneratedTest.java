java
package net.ripe.rpki.validator3.domain.cleanup;

import net.ripe.rpki.validator3.api.util.InstantWithoutNanos;
import net.ripe.rpki.validator3.storage.Storage;
import net.ripe.rpki.validator3.storage.stores.RpkiObjects;
import net.ripe.rpki.validator3.util.Time;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RpkiObjectCleanupServiceTest {

    @Mock
    private RpkiObjects rpkiObjects;

    @Mock
    private Storage storage;

    @InjectMocks
    private RpkiObjectCleanupService rpkiObjectCleanupService;

    private Duration cleanupGraceDuration = Duration.ofDays(1);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rpkiObjectCleanupService = new RpkiObjectCleanupService();
        rpkiObjectCleanupService.rpkiObjects = rpkiObjects;
        rpkiObjectCleanupService.storage = storage;
        rpkiObjectCleanupService.cleanupGraceDuration = cleanupGraceDuration;

    }

    @Test
    public void testCleanupRpkiObjects() throws Exception {
        Instant now = Instant.now();
        InstantWithoutNanos unreachableSince = InstantWithoutNanos.of(now.minus(cleanupGraceDuration));

        when(rpkiObjects.deleteUnreachableObjects(any())).thenReturn(Pair.of(10L, 100L));

        long deletedCount = rpkiObjectCleanupService.cleanupRpkiObjects();

        assertEquals(10L, deletedCount);
        verify(storage, times(1)).gc();
        verify(rpkiObjects, times(1)).deleteUnreachableObjects(any());
    }

    @Test
    public void testCleanupRpkiObjects_zeroDeleted() throws Exception {
        when(rpkiObjects.deleteUnreachableObjects(any())).thenReturn(Pair.of(0L, 50L));

        long deletedCount = rpkiObjectCleanupService.cleanupRpkiObjects();

        assertEquals(0L, deletedCount);
        verify(storage, times(1)).gc();
        verify(rpkiObjects, times(1)).deleteUnreachableObjects(any());
    }

    @Test
    public void testCleanupRpkiObjects_exception() throws Exception {
        when(rpkiObjects.deleteUnreachableObjects(any())).thenThrow(new RuntimeException("Simulated exception"));

        try {
            rpkiObjectCleanupService.cleanupRpkiObjects();
        } catch (Exception e) {
            assertEquals("Simulated exception", e.getMessage());
        }

        verify(storage, never()).gc();
        verify(rpkiObjects, times(1)).deleteUnreachableObjects(any());
    }
}