java
package com.tddinaction.swing.plotmap.view;

import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PlotMapCanvasImplTest {

    private PlotMapCanvasImpl canvas;

    @Before
    public void setUp() {
        canvas = new PlotMapCanvasImpl();
    }

    @Test
    public void testPlotPoint() {
        Point point = new Point(10, 20);
        canvas.plot(point);
        List<Point> plots = canvas.getPlots();
        assertEquals(1, plots.size());
        assertEquals(point, plots.get(0));
    }

    @Test
    public void testPlotMultiplePoints() {
        Point point1 = new Point(10, 20);
        Point point2 = new Point(30, 40);
        canvas.plot(point1);
        canvas.plot(point2);
        List<Point> plots = canvas.getPlots();
        assertEquals(2, plots.size());
        assertEquals(point1, plots.get(0));
        assertEquals(point2, plots.get(1));
    }

    @Test
    public void testGetPlotsReturnsCopy() {
        Point point = new Point(10, 20);
        canvas.plot(point);
        List<Point> plots = canvas.getPlots();
        plots.clear();
        assertEquals(1, canvas.getPlots().size());
    }
}