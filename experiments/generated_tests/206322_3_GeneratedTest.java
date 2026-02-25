java
package org.apache.maven.plugins.ejb;

import org.junit.Test;

import static org.junit.Assert.*;

public class EjbHelperTest
{

    @Test
    public void testIsClassifierValid_null()
    {
        assertFalse( EjbHelper.isClassifierValid( null ) );
    }

    @Test
    public void testIsClassifierValid_empty()
    {
        assertFalse( EjbHelper.isClassifierValid( "" ) );
    }

    @Test
    public void testIsClassifierValid_blank()
    {
        assertFalse( EjbHelper.isClassifierValid( "   " ) );
    }

    @Test
    public void testIsClassifierValid_valid()
    {
        assertTrue( EjbHelper.isClassifierValid( "test" ) );
    }

    @Test
    public void testIsClassifierValid_validWithNumber()
    {
        assertTrue( EjbHelper.isClassifierValid( "test1" ) );
    }

    @Test
    public void testIsClassifierValid_validWithDash()
    {
        assertTrue( EjbHelper.isClassifierValid( "test-1" ) );
    }

    @Test
    public void testIsClassifierValid_invalidStartsWithNumber()
    {
        assertFalse( EjbHelper.isClassifierValid( "1test" ) );
    }

    @Test
    public void testIsClassifierValid_invalidSpecialCharacters()
    {
        assertFalse( EjbHelper.isClassifierValid( "test_" ) );
        assertFalse( EjbHelper.isClassifierValid( "test." ) );
    }

    @Test
    public void testIsClassifierValid_justADash()
    {
        assertFalse( EjbHelper.isClassifierValid( "-" ) );
    }

    @Test
    public void testIsClassifierValid_dashAtStart()
    {
        assertFalse( EjbHelper.isClassifierValid( "-test" ) );
    }
}