java
package org.gephi.graph.impl;

import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import it.unimi.dsi.fastutil.shorts.ShortSortedSet;
import java.util.Arrays;
import org.gephi.graph.impl.utils.MapDeepEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EdgeTypeStoreTest {

    private EdgeTypeStore edgeTypeStore;

    @BeforeEach
    public void setUp() {
        edgeTypeStore = new EdgeTypeStore();
    }

    @Test
    public void testAddType() {
        assertEquals(0, edgeTypeStore.addType("type1"));
        assertEquals(1, edgeTypeStore.addType("type2"));
    }

    @Test
    public void testGetLabel() {
        int id = edgeTypeStore.addType("type1");
        assertEquals("type1", edgeTypeStore.getLabel(id));
    }

    @Test
    public void testGetId() {
        edgeTypeStore.addType("type1");
        int id = edgeTypeStore.addType("type2");
        assertEquals(id, edgeTypeStore.getId("type2"));
    }

    @Test
    public void testHasType() {
        edgeTypeStore.addType("type1");
        assertTrue(edgeTypeStore.hasType("type1"));
        assertFalse(edgeTypeStore.hasType("type2"));
    }

    @Test
    public void testSize() {
        assertEquals(0, edgeTypeStore.size());
        edgeTypeStore.addType("type1");
        assertEquals(1, edgeTypeStore.size());
    }

    @Test
    public void testClear() {
        edgeTypeStore.addType("type1");
        edgeTypeStore.clear();
        assertEquals(0, edgeTypeStore.size());
    }

    @Test
    public void testEnsureCapacity() {
        edgeTypeStore.ensureCapacity(10);
    }
}