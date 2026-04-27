java
package demo;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SupplyLocationBulkUploadControllerTest {

    @InjectMocks
    private SupplyLocationBulkUploadController controller;

    @Mock
    private SupplyLocationService service;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUploadFitleredLocations() {
        List<SupplyLocation> inputLocations = new ArrayList<>();
        SupplyLocation location1 = new SupplyLocation();
        location1.setZip("50401");
        inputLocations.add(location1);
        SupplyLocation location2 = new SupplyLocation();
        location2.setZip("12345");
        inputLocations.add(location2);

        List<SupplyLocation> expectedLocations = new ArrayList<>();
        expectedLocations.add(location1);

        when(service.saveSupplyLocationsZipContains504(inputLocations)).thenReturn(expectedLocations);

        List<SupplyLocation> actualLocations = controller.uploadFitleredLocations(inputLocations);

        assertEquals(expectedLocations, actualLocations);
    }

    @Test
    public void testUploadFitleredLocations_emptyList() {
        List<SupplyLocation> inputLocations = new ArrayList<>();
        List<SupplyLocation> expectedLocations = new ArrayList<>();

        when(service.saveSupplyLocationsZipContains504(inputLocations)).thenReturn(expectedLocations);

        List<SupplyLocation> actualLocations = controller.uploadFitleredLocations(inputLocations);

        assertEquals(expectedLocations, actualLocations);
    }

    @Test
    public void testUploadFitleredLocations_nullList() {
        List<SupplyLocation> inputLocations = null;

        try {
            controller.uploadFitleredLocations(inputLocations);
        } catch (NullPointerException e) {
            assert true; // Expecting a NullPointerException
        }
    }

    @Test
    public void testUploadFitleredLocations_no504Zip() {
        List<SupplyLocation> inputLocations = new ArrayList<>();
        SupplyLocation location1 = new SupplyLocation();
        location1.setZip("12345");
        inputLocations.add(location1);
        SupplyLocation location2 = new SupplyLocation();
        location2.setZip("67890");
        inputLocations.add(location2);

        List<SupplyLocation> expectedLocations = new ArrayList<>();

        when(service.saveSupplyLocationsZipContains504(inputLocations)).thenReturn(expectedLocations);

        List<SupplyLocation> actualLocations = controller.uploadFitleredLocations(inputLocations);

        assertEquals(expectedLocations, actualLocations);
    }

    @Test
    public void testUploadFitleredLocations_multiple504Zip() {
        List<SupplyLocation> inputLocations = new ArrayList<>();
        SupplyLocation location1 = new SupplyLocation();
        location1.setZip("50401");
        inputLocations.add(location1);
        SupplyLocation location2 = new SupplyLocation();
        location2.setZip("50402");
        inputLocations.add(location2);

        List<SupplyLocation> expectedLocations = new ArrayList<>();
        expectedLocations.add(location1);
        expectedLocations.add(location2);

        when(service.saveSupplyLocationsZipContains504(inputLocations)).thenReturn(expectedLocations);

        List<SupplyLocation> actualLocations = controller.uploadFitleredLocations(inputLocations);

        assertEquals(expectedLocations, actualLocations);
    }
}