java
package android.content.res;

import android.content.res.Resources.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import net.yui.BuildConfig;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ShadowResourcesTest {

    private ShadowResources shadowResources;
    private Resources resources;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        shadowResources = new ShadowResources();
        resources = mock(Resources.class);
    }

    @Test
    public void testGetString() {
        int id = 123;
        String expectedString = "test string";
        when(resources.getText(id)).thenReturn(expectedString);
        shadowResources.setResources(resources);
        assertEquals(expectedString, shadowResources.getString(id));
    }

    @Test(expected = NotFoundException.class)
    public void testGetStringNotFound() {
        int id = 456;
        when(resources.getText(id)).thenThrow(new NotFoundException("Resource not found"));
        shadowResources.setResources(resources);
        shadowResources.getString(id);
    }

    @Test
    public void testGetText() {
        int id = 789;
        CharSequence expectedText = "test text";
        when(resources.getText(id)).thenReturn(expectedText);
        shadowResources.setResources(resources);
        assertEquals(expectedText, shadowResources.getText(id));
    }
    
    @Test(expected = Resources.NotFoundException.class)
    public void testGetTextNotFound() {
        int id = 101;
        when(resources.getText(id)).thenThrow(new Resources.NotFoundException("Resource not found"));
        shadowResources.setResources(resources);
        shadowResources.getText(id);
    }
    
    @Test
    public void testSetResources() {
        shadowResources.setResources(resources);
    }
}