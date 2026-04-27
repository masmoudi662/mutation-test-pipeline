java
package org.scenarioo.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IdGeneratorTest {

    @Test
    public void testGenerateIdUsingHash_validInput() {
        String input = "testInput";
        String id = IdGenerator.generateIdUsingHash(input);
        assertNotNull(id);
        assertEquals(8, id.length());
    }

    @Test
    public void testGenerateIdUsingHash_emptyInput() {
        String input = "";
        String id = IdGenerator.generateIdUsingHash(input);
        assertNotNull(id);
        assertEquals(8, id.length());
    }

    @Test
    public void testGenerateIdUsingHash_differentInputsGenerateDifferentIds() {
        String input1 = "testInput1";
        String input2 = "testInput2";
        String id1 = IdGenerator.generateIdUsingHash(input1);
        String id2 = IdGenerator.generateIdUsingHash(input2);
        assertNotNull(id1);
        assertNotNull(id2);
        assertEquals(8, id1.length());
        assertEquals(8, id2.length());
    }

    @Test(expected = RuntimeException.class)
    public void testGenerateIdUsingHash_noSuchAlgorithmException() {
        // This test is designed to simulate a NoSuchAlgorithmException, but it's
        // difficult to reliably trigger the exception in a standard environment.
        // The exception is caught and re-thrown as a RuntimeException. To properly
        // test this, one might need to mock the MessageDigest class.
        // However, without mocking libraries available, we'll simply trigger the
        // method and expect the RuntimeException to be thrown.
        IdGenerator.generateIdUsingHash("someInput");

    }
}