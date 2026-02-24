java
package org.constretto.internal.store.ldap;

import org.constretto.model.TaggedPropertySet;
import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LdapConfigurationStoreTest {

    @Test
    public void testParseConfiguration_emptyStore() {
        LdapConfigurationStore store = new LdapConfigurationStore();
        Collection<TaggedPropertySet> result = store.parseConfiguration();
        assertEquals(1, result.size());
        TaggedPropertySet propertySet = result.iterator().next();
        assertEquals(Collections.emptyMap(), propertySet.getProperties());
    }

    @Test
    public void testParseConfiguration_singleEntry() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("attribute1", "value1");
        LdapConfigurationStore store = new LdapConfigurationStore(new LdapConfigurationStore(), "key1", attributes);
        Collection<TaggedPropertySet> result = store.parseConfiguration();
        assertEquals(1, result.size());
        TaggedPropertySet propertySet = result.iterator().next();
        assertEquals(1, propertySet.getProperties().size());
        assertEquals("value1", propertySet.getProperties().get("key1.attribute1"));
    }

    @Test
    public void testParseConfiguration_multipleEntries() throws Exception {
        Attributes attributes1 = new BasicAttributes();
        attributes1.put("attribute1", "value1");
        Attributes attributes2 = new BasicAttributes();
        attributes2.put("attribute2", "value2");

        LdapConfigurationStore store1 = new LdapConfigurationStore(new LdapConfigurationStore(), "key1", attributes1);
        LdapConfigurationStore store2 = new LdapConfigurationStore(store1, "key2", attributes2);

        Collection<TaggedPropertySet> result = store2.parseConfiguration();
        assertEquals(1, result.size());
        TaggedPropertySet propertySet = result.iterator().next();
        assertEquals(2, propertySet.getProperties().size());
        assertEquals("value1", propertySet.getProperties().get("key1.attribute1"));
        assertEquals("value2", propertySet.getProperties().get("key2.attribute2"));
    }

    @Test
    public void testParseConfiguration_withTags() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("attribute1", "value1");
        LdapConfigurationStore store = new LdapConfigurationStore(new LdapConfigurationStore(), "key1", attributes, "tag1", "tag2");
        Collection<TaggedPropertySet> result = store.parseConfiguration();
        assertEquals(2, result.size());
        List<TaggedPropertySet> list = new ArrayList<>(result);

        assertEquals(1, list.get(0).getProperties().size());
        assertEquals("value1", list.get(0).getProperties().get("key1.attribute1"));
        assertEquals("tag1", list.get(0).getTag());

        assertEquals(1, list.get(1).getProperties().size());
        assertEquals("value1", list.get(1).getProperties().get("key1.attribute1"));
        assertEquals("tag2", list.get(1).getTag());
    }

    @Test
    public void testParseConfiguration_multipleAttributeValues() throws Exception {
        Attributes attributes = Mockito.mock(Attributes.class);
        Attribute attribute = Mockito.mock(Attribute.class);
        NamingEnumeration<Attribute> enumeration = Mockito.mock(NamingEnumeration.class);

        when(attributes.getAll()).thenReturn(enumeration);
        when(enumeration.hasMore()).thenReturn(true, false);
        when(enumeration.next()).thenReturn(attribute);
        when(attribute.getID()).thenReturn("attribute1");
        when(attribute.size()).thenReturn(2);
        when(attribute.get(0)).thenReturn("value1");
        when(attribute.get(1)).thenReturn("value2");


        LdapConfigurationStore store = new LdapConfigurationStore(new LdapConfigurationStore(), "key1", attributes);
        Collection<TaggedPropertySet> result = store.parseConfiguration();
        assertEquals(1, result.size());
        TaggedPropertySet propertySet = result.iterator().next();
        assertEquals(1, propertySet.getProperties().size());
        assertEquals("[\"value1\",\"value2\"]", propertySet.getProperties().get("key1.attribute1"));
    }

    @Test
    public void testParseConfiguration_passwordAttributeIgnored() throws Exception {
        Attributes attributes = new BasicAttributes();
        attributes.put("password", "secret");
        LdapConfigurationStore store = new LdapConfigurationStore(new LdapConfigurationStore(), "key1", attributes);
        Collection<TaggedPropertySet> result = store.parseConfiguration();
        assertEquals(1, result.size());
        TaggedPropertySet propertySet = result.iterator().next();
        assertEquals(0, propertySet.getProperties().size());
    }
}