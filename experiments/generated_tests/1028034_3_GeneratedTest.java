java
package org.kabeja.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ParametricPlaneTest {

    @Test
    public void testGetParameter() {
        Point3D base = new Point3D(0, 0, 0);
        Vector directionX = new Vector(1, 0, 0);
        Vector directionY = new Vector(0, 1, 0);
        ParametricPlane plane = new ParametricPlane(base, directionX, directionY);

        Point3D p = new Point3D(1, 1, 0);
        double[] expected = {0.0, 1.0};
        double[] actual = plane.getParameter(p);
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    public void testGetParameter2() {
        Point3D base = new Point3D(1, 1, 1);
        Vector directionX = new Vector(1, 0, 0);
        Vector directionY = new Vector(0, 1, 0);
        ParametricPlane plane = new ParametricPlane(base, directionX, directionY);

        Point3D p = new Point3D(2, 2, 1);
        double[] expected = {0.0, 1.0};
        double[] actual = plane.getParameter(p);
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    public void testGetParameter3() {
        Point3D base = new Point3D(0, 0, 0);
        Vector directionX = new Vector(0, 1, 0);
        Vector directionY = new Vector(1, 0, 0);
        ParametricPlane plane = new ParametricPlane(base, directionX, directionY);

        Point3D p = new Point3D(1, 1, 0);
        double[] expected = {0.0, 1.0};
        double[] actual = plane.getParameter(p);
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    public void testGetParameter4() {
        Point3D base = new Point3D(0, 0, 0);
        Vector directionX = new Vector(1, 1, 0);
        Vector directionY = new Vector(1, -1, 0);
        ParametricPlane plane = new ParametricPlane(base, directionX, directionY);

        Point3D p = new Point3D(2, 0, 0);
        double[] expected = {1.0, 1.0};
        double[] actual = plane.getParameter(p);
        assertArrayEquals(expected, actual, 1e-6);
    }

    @Test
    public void testGetParameter5() {
        Point3D base = new Point3D(0, 0, 0);
        Vector directionX = new Vector(1, 0, 0);
        Vector directionY = new Vector(0, 0, 1);
        ParametricPlane plane = new ParametricPlane(base, directionX, directionY);

        Point3D p = new Point3D(1, 0, 1);
        double[] expected = {0.0, 1.0};
        double[] actual = plane.getParameter(p);
        assertArrayEquals(expected, actual, 1e-6);
    }
}