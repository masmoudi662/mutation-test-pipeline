java
package org.apache.tinkerpop.gremlin.object.reflect;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClassesTest {

    @Test
    public void testIsVertexWithNull() {
        assertFalse(Classes.isVertex(null));
    }

    @Test
    public void testIsVertexWithString() {
        assertFalse(Classes.isVertex("string"));
    }

    @Test
    public void testIsVertexWithObject() {
        assertFalse(Classes.isVertex(new Object()));
    }

    @Test
    public void testIsEdgeWithNull() {
        assertFalse(Classes.isEdge(null));
    }

    @Test
    public void testIsEdgeWithString() {
        assertFalse(Classes.isEdge("string"));
    }

    @Test
    public void testIsEdgeWithObject() {
        assertFalse(Classes.isEdge(new Object()));
    }
}