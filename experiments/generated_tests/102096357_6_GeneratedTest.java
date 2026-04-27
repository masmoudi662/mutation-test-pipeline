java
package com.vr_object.fixed;

import org.junit.Test;

import static com.vr_object.fixed.Vector3D.createVector;
import static com.vr_object.fixed.Vector3D.distanceDotLine;
import static com.vr_object.fixed.Vector3D.distanceVertices;
import static com.vr_object.fixed.Vector3D.scalarMultiply;
import static org.junit.Assert.*;

public class Vector3DTest {

    private static final float Epsilon = 0.0001f;

    @Test
    public void testDistancevertexSegment_start() {
        float[] dot = {0, 0, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(1.0f, distance, Epsilon);
    }

    @Test
    public void testDistancevertexSegment_end() {
        float[] dot = {0, 1, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(Math.sqrt(2), distanceVertices(dot, segmentEnd), Epsilon);
    }

    @Test
    public void testDistancevertexSegment_middle() {
        float[] dot = {0, 0.5f, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(1, distance, Epsilon);
    }

    @Test
    public void testDistancevertexSegment_outside_start() {
        float[] dot = {2, 0, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(1, distance, Epsilon);
    }

    @Test
    public void testDistancevertexSegment_outside_end() {
        float[] dot = {2, 1, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(Math.sqrt(2), distanceVertices(dot, segmentEnd), Epsilon);
    }

    @Test
    public void testDistancevertexSegment_same_start_end() {
        float[] dot = {0, 0, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 0, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(1, distance, Epsilon);
    }

    @Test
    public void testDistancevertexSegment_3d() {
        float[] dot = {0, 0, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 1};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(1.0f, distance, Epsilon);
    }

    @Test
    public void testDistancevertexSegment_perpendicular_coincides_start() {
        float[] dot = {1, 0, 0};
        float[] segmentStart = {1, 0, 0};
        float[] segmentEnd = {1, 1, 0};
        float distance = Vector3D.distancevertexSegment(dot, segmentStart, segmentEnd);
        assertEquals(0, distance, Epsilon);
    }
}