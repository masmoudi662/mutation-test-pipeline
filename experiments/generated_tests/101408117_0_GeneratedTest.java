java
package com.ibm.j9ddr.corereaders.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import static java.util.logging.Level.FINEST;
import static org.mockito.Mockito.*;

public class AbstractMemoryTest {

    private AbstractMemory abstractMemory;
    private IMemorySource memorySource;
    private SortedMemoryRanges memorySources;
    private Map<IMemorySource, IMemorySource> decoratorMappingTable;
    private Logger logger;

    @BeforeEach
    public void setUp() {
        abstractMemory = Mockito.mock(AbstractMemory.class, Mockito.CALLS_REAL_METHODS);
        memorySource = mock(IMemorySource.class);
        memorySources = mock(SortedMemoryRanges.class);
        decoratorMappingTable = new TreeMap<>();
        logger = Logger.getLogger(AbstractMemory.class.getName());

        abstractMemory.memorySources = memorySources;
        abstractMemory.decoratorMappingTable = decoratorMappingTable;
        abstractMemory.logger = logger;

        Mockito.doNothing().when(abstractMemory).setRangeTable(null);

        AbstractMemory.GLOBAL_CACHE_ENABLED = false;
        AbstractMemory.RECORDING_CACHE_STATS = false;
    }

    @Test
    public void testAddMemorySourceWithoutCacheOrStats() {
        when(memorySource.getBaseAddress()).thenReturn(0L);
        when(memorySource.getTopAddress()).thenReturn(100L);

        abstractMemory.addMemorySource(memorySource);

        verify(memorySources).addMemorySource(memorySource);
        verify(abstractMemory).setRangeTable(null);
    }

    @Test
    public void testAddMemorySourceWithGlobalCache() {
        AbstractMemory.GLOBAL_CACHE_ENABLED = true;

        when(memorySource.getBaseAddress()).thenReturn(0L);
        when(memorySource.getTopAddress()).thenReturn(100L);

        abstractMemory.addMemorySource(memorySource);

        verify(memorySources).addMemorySource(any(CachingMemorySource.class));
        verify(abstractMemory).setRangeTable(null);
    }

    @Test
    public void testAddMemorySourceWithRecordingCacheStats() {
        AbstractMemory.RECORDING_CACHE_STATS = true;

        when(memorySource.getBaseAddress()).thenReturn(0L);
        when(memorySource.getTopAddress()).thenReturn(100L);

        abstractMemory.addMemorySource(memorySource);

        verify(memorySources).addMemorySource(any(CountingMemorySource.class));
        verify(abstractMemory).setRangeTable(null);
    }
}