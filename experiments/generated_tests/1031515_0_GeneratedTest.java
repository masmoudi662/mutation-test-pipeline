java
package eu.delving.core.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class LocalizedFieldNamesTest {

    @InjectMocks
    private LocalizedFieldNames localizedFieldNames;

    @Mock
    private MessageSource messageSource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("Localized Value");
    }

    @Test
    public void testCreateLookup() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        LocalizedFieldNames.Lookup lookup = localizedFieldNames.createLookup(keys);
        assertNotNull(lookup);
    }

    @Test
    public void testLookupGetValue() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        LocalizedFieldNames.Lookup lookup = localizedFieldNames.createLookup(keys);
        assertNotNull(lookup.getValue("key1"));
        assertEquals("Localized Value", lookup.getValue("key1"));
    }

    @Test
    public void testLookupGetValueNullKey() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        LocalizedFieldNames.Lookup lookup = localizedFieldNames.createLookup(keys);
        assertNull(lookup.getValue(null));
    }

    @Test
    public void testLookupGetValueUnknownKey() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        LocalizedFieldNames.Lookup lookup = localizedFieldNames.createLookup(keys);
        assertNull(lookup.getValue("unknown"));
    }

}