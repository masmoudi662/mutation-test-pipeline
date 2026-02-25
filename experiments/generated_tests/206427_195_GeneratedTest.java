java
package org.apache.tika.parser.microsoft.chm;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ChmItspHeaderTest {

    private ChmItspHeader chmItspHeader;

    @Before
    public void setUp() throws Exception {
        chmItspHeader = new ChmItspHeader();
        chmItspHeader.index_root = 123;
    }

    @Test
    public void testGetIndex_root() {
        assertEquals(123, chmItspHeader.getIndex_root());
    }
}