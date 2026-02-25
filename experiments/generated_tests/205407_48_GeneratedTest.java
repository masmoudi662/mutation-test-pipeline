java
package org.apache.directory.studio.openldap.config.acl.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.text.ParseException;

public class OpenLdapAclParserTest {

    @Test
    public void testParseSimpleAcl() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to * by * read";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseAclWithAttributes() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to attrs=userPassword by self write";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseAclWithFilter() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to filter=(objectClass=person) by users read";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseAclWithMultipleAccessItems() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to * by self write by anonymous auth";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseExceptionOnInvalidAcl() {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String invalidAclString = "access to * by";
        assertThrows(ParseException.class, () -> parser.parse(invalidAclString));
    }

    @Test
    public void testParseExceptionOnEmptyAcl() {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String emptyAclString = "";
        assertThrows(ParseException.class, () -> parser.parse(emptyAclString));
    }

    @Test
    public void testParseAclWithControl() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to * control by * none";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseAclWithDN() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to * by dn=\"cn=admin,dc=example,dc=com\" write";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }

    @Test
    public void testParseAclWithDNRegex() throws ParseException {
        OpenLdapAclParser parser = new OpenLdapAclParser();
        String aclString = "access to * by dn.regex=\"cn=.*,dc=example,dc=com\" write";
        AclItem aclItem = parser.parse(aclString);
        assertNotNull(aclItem);
    }
}