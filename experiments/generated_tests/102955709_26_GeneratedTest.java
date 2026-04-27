java
package com.ibm.cloud.objectstorage.services.aspera.transfer;

import com.ibm.cloud.objectstorage.SdkClientException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class AsperaFaspManagerWrapperTest {

    private AsperaFaspManagerWrapper asperaFaspManagerWrapper;

    @Before
    public void setup() {
        asperaFaspManagerWrapper = new AsperaFaspManagerWrapper();
    }

    @Test
    public void pause_validXferId_returnsTrue() {
        String xferId = "test-xfer-id";
        try (MockedStatic<com.ibm.aspera.faspmanager2.faspmanager2> utilities = mockStatic(com.ibm.aspera.faspmanager2.faspmanager2.class)) {
            utilities.when(() -> com.ibm.aspera.faspmanager2.faspmanager2.modifyTransfer(anyString(), eq(4), eq(0))).thenReturn(true);
            boolean result = asperaFaspManagerWrapper.pause(xferId);
            assertEquals(true, result);
        }
    }

    @Test
    public void pause_validXferId_returnsFalse() {
        String xferId = "test-xfer-id";
        try (MockedStatic<com.ibm.aspera.faspmanager2.faspmanager2> utilities = mockStatic(com.ibm.aspera.faspmanager2.faspmanager2.class)) {
            utilities.when(() -> com.ibm.aspera.faspmanager2.faspmanager2.modifyTransfer(anyString(), eq(4), eq(0))).thenReturn(false);
            boolean result = asperaFaspManagerWrapper.pause(xferId);
            assertEquals(false, result);
        }
    }

    @Test
    public void testTransferManagerUtilsInstantiation() {
        AsperaFaspManagerWrapper instance = new AsperaFaspManagerWrapper();
        assertEquals(instance.getClass(), AsperaFaspManagerWrapper.class);
    }
}