java
package com.tddinaction.swing.plotmap.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import org.junit.Test;

public class PlotMapCanvasImplTest {

    @Test
    public void testPlot() {
        PlotMapCanvasImpl canvas = new PlotMapCanvasImpl();
        Point p = new Point(10, 20);
        canvas.plot(p);
    }

    @Test
    public void testAddRemoveListener() {
        PlotMapCanvasImpl canvas = new PlotMapCanvasImpl();
        PointEventListener listener = mock(PointEventListener.class);
        canvas.addRemoveListener(listener);
    }

    @Test
    public void testMouseClicked() {
        PlotMapCanvasImpl canvas = new PlotMapCanvasImpl();
        PointEventListener listener = mock(PointEventListener.class);
        canvas.addRemoveListener(listener);

        Point p = new Point(10, 20);
        canvas.plot(p);

        MouseEvent e = new MouseEvent(canvas, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 10, 20, 1, false);
        canvas.dispatchEvent(e);

        verify(listener).onPointEvent(p);
    }
}