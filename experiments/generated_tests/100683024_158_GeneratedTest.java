java
package org.apache.directory.api.ldap.model.url;

import org.apache.directory.api.ldap.model.name.Dn;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LdapUrlTest {

    @Test
    public void testToStringSimple() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom", url.toString());
    }

    @Test
    public void testToStringWithAttributes() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));
        url.setAttributes(Arrays.asList("cn", "sn"));

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom?cn,sn", url.toString());
    }

    @Test
    public void testToStringWithScope() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));
        url.setScope(SearchScope.ONELEVEL);

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom?one", url.toString());
    }

    @Test
    public void testToStringWithFilter() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));
        url.setScope(SearchScope.SUBTREE);
        url.setFilter("(objectClass=person)");

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom?sub?(objectClass=person)", url.toString());
    }

    @Test
    public void testToStringWithExtensions() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));

        Extension extension = new Extension("1.2.3.4.5", "value", true);
        url.setExtensions(Collections.singletonList(extension));

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom?????%211.2.3.4.5%3Dvalue", url.toString());
    }

    @Test
    public void testToStringWithAll() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("example.com");
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));
        url.setAttributes(Arrays.asList("cn", "sn"));
        url.setScope(SearchScope.SUBTREE);
        url.setFilter("(objectClass=person)");

        Extension extension = new Extension("1.2.3.4.5", "value", true);
        url.setExtensions(Collections.singletonList(extension));

        assertEquals("ldap://example.com:389/dc%3Dexample%2Cdc%3Dcom?cn,sn?sub?(objectClass=person)???%211.2.3.4.5%3Dvalue", url.toString());
    }

    @Test
    public void testToStringNoHost() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setDn(new Dn("dc=example,dc=com"));

        assertEquals("ldap:///dc%3Dexample%2Cdc%3Dcom", url.toString());
    }

    @Test
    public void testToStringNoDn() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");

        assertEquals("ldap:///", url.toString());
    }
    
    @Test
    public void testToStringIPV6() throws Exception {
        LdapUrl url = new LdapUrl();
        url.setScheme("ldap://");
        url.setHost("[2001:db8::7]");
        url.setHostType(HostTypeEnum.IPV6);
        url.setPort(389);
        url.setDn(new Dn("dc=example,dc=com"));
        assertEquals("ldap://[2001:db8::7]:389/dc%3Dexample%2Cdc%3Dcom", url.toString());
    }
}