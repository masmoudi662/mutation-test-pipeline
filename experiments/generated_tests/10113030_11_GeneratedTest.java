java
package com.google.maps.android.data.kml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import com.google.android.gms.maps.model.GroundOverlay;
import com.google.maps.android.data.Feature;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class KmlContainerParserTest {

    @Test
    public void testCreateContainer() throws XmlPullParserException, IOException {
        XmlPullParser parser = Mockito.mock(XmlPullParser.class);
        Mockito.when(parser.getEventType()).thenReturn(START_TAG, END_TAG);
        Mockito.when(parser.getName()).thenReturn("Folder");

        KmlContainer container = KmlContainerParser.createContainer(parser);

        assertNotNull(container);
    }

    @Test
    public void testAssignPropertiesToContainer() throws XmlPullParserException, IOException {
        XmlPullParser parser = Mockito.mock(XmlPullParser.class);
        Mockito.when(parser.getEventType()).thenReturn(START_TAG, START_TAG, END_TAG, END_TAG);
        Mockito.when(parser.getName()).thenReturn("Folder", "name", "name", "Folder");
        Mockito.when(parser.next()).thenReturn(XmlPullParser.TEXT, XmlPullParser.END_TAG);
        Mockito.when(parser.getText()).thenReturn("Test Container");

        KmlContainer container = KmlContainerParser.assignPropertiesToContainer(parser);

        assertNotNull(container);
    }

    @Test
    public void testAssignPropertiesToContainerEmpty() throws XmlPullParserException, IOException {
        XmlPullParser parser = Mockito.mock(XmlPullParser.class);
        Mockito.when(parser.getEventType()).thenReturn(START_TAG, END_TAG);
        Mockito.when(parser.getName()).thenReturn("Folder");
        KmlContainer container = KmlContainerParser.assignPropertiesToContainer(parser);
        assertNotNull(container);
    }
}