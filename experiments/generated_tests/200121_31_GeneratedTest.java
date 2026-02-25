java
package org.constretto.internal.store.ldap;

import org.constretto.model.TaggedPropertySet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LdapConfigurationStoreTest {

    @InjectMocks
    private LdapConfigurationStore ldapConfigurationStore;

    @Mock
    private Attributes attributes;

    @Mock
    private Attribute attribute;

    @Mock
    private NamingEnumeration namingEnumeration;

    private Map<String, String> keyAttributesMap = new HashMap<>();

    @Before
    public void setUp() throws Exception {
        ldapConfigurationStore = new LdapConfigurationStore();
        ldapConfigurationStore.keyAttributesMap = new HashMap<>();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testParseConfiguration() throws Exception {
        ldapConfigurationStore.keyAttributesMap = new HashMap<>();
        ldapConfigurationStore.keyAttributesMap.put("key1", "attribute1");
        ldapConfigurationStore.keyAttributesMap.put("key2", "attribute2");
        Collection<TaggedPropertySet> result = ldapConfigurationStore.parseConfiguration();
        assertEquals(1, result.size());
    }

    @Test
    public void testParseConfigurationEmpty() throws Exception {
        ldapConfigurationStore.keyAttributesMap = new HashMap<>();
        Collection<TaggedPropertySet> result = ldapConfigurationStore.parseConfiguration();
        assertEquals(1, result.size());
    }

}