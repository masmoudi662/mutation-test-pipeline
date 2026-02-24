java
package org.apache.directory.server.xdbm;

import org.apache.directory.api.ldap.model.cursor.CursorException;
import org.apache.directory.api.ldap.model.cursor.InvalidCursorPositionException;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.api.partition.PartitionTxn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmptyIndexCursorTest
{
    private EmptyIndexCursor<String> cursor;

    @Mock
    private PartitionTxn partitionTxn;

    @BeforeEach
    public void setUp()
    {
        cursor = new EmptyIndexCursor<>( partitionTxn );
    }

    @Test
    public void testBefore() throws LdapException, CursorException
    {
        assertDoesNotThrow( () -> cursor.before( new IndexEntry<>( "key", "value" ) ) );
    }

    @Test
    public void testAfter() throws LdapException, CursorException
    {
        assertDoesNotThrow( () -> cursor.after( new IndexEntry<>( "key", "value" ) ) );
    }

    @Test
    public void testBeforeFirst()
    {
        assertDoesNotThrow( () -> cursor.beforeFirst() );
    }

    @Test
    public void testAfterLast()
    {
        assertDoesNotThrow( () -> cursor.afterLast() );
    }

    @Test
    public void testFirst() throws LdapException, CursorException
    {
        assertFalse( cursor.first() );
    }

    @Test
    public void testLast() throws LdapException, CursorException
    {
        assertFalse( cursor.last() );
    }

    @Test
    public void testPrevious() throws LdapException, CursorException
    {
        assertFalse( cursor.previous() );
    }

    @Test
    public void testNext() throws LdapException, CursorException
    {
        assertFalse( cursor.next() );
    }

    @Test
    public void testGet()
    {
        assertThrows( InvalidCursorPositionException.class, () -> cursor.get() );
    }

    @Test
    public void testClose() throws IOException
    {
        assertDoesNotThrow( () -> cursor.close() );
    }

    @Test
    public void testCloseWithCause() throws IOException
    {
        Exception cause = new Exception( "Test Cause" );
        assertDoesNotThrow( () -> cursor.close( cause ) );
    }

    @Test
    public void testAfterValue() throws Exception
    {
        assertDoesNotThrow(() -> cursor.afterValue("id", "indexValue"));
    }

    @Test
    public void testBeforeValue() throws Exception
    {
        assertDoesNotThrow(() -> cursor.beforeValue("id", "indexValue"));
    }
}