java
package org.apache.directory.studio.openldap.config.acl.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpenLdapAclParserTest
{
    private OpenLdapAclParser parser;

    @BeforeEach
    public void setUp()
    {
        parser = new OpenLdapAclParser();
    }

    @Test
    public void testParseValidAcl() throws ParseException
    {
        String acl = "to * by * manage";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }

    @Test
    public void testParseInvalidAcl()
    {
        String acl = "invalid acl";
        assertThrows( ParseException.class, () -> parser.parse( acl ) );
    }

    @Test
    public void testParseEmptyAcl() throws ParseException
    {
        String acl = "";
        AclItem aclItem = parser.parse( acl );
        // Assuming an empty ACL is still a valid AclItem object
        assertNotNull( aclItem );
    }

    @Test
    public void testParseAclWithDn() throws ParseException
    {
        String acl = "to dn.exact=\"dc=example,dc=com\" by * manage";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }

    @Test
    public void testParseAclWithAttributes() throws ParseException
    {
        String acl = "to attrs=userPassword by * manage";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }

    @Test
    public void testParseAclWithFilter() throws ParseException
    {
        String acl = "to filter=\"(objectClass=*)\" by * manage";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }

    @Test
    public void testParseAclWithWhoDn() throws ParseException
    {
        String acl = "to * by dn.exact=\"cn=admin,dc=example,dc=com\" manage";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }

    @Test
    public void testParseAclWithAccessLevel() throws ParseException
    {
        String acl = "to * by * self auth";
        AclItem aclItem = parser.parse( acl );
        assertNotNull( aclItem );
    }
}