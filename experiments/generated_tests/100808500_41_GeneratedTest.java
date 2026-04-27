java
package org.oulipo.streams.opcodes;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class PutInvariantMediaOpTest {

    @Test
    public void testConstructor() throws IOException {
        PutInvariantMediaOp op = new PutInvariantMediaOp(10, 20, 30);
        assertEquals(10, op.ripIndex);
        assertEquals(20, op.to);
        assertEquals(30, op.length);
    }

    @Test
    public void testEquals() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(10, 20, 30);
        assertEquals(op1, op2);
    }

    @Test
    public void testNotEqualsRipIndex() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(11, 20, 30);
        assertNotEquals(op1, op2);
    }

    @Test
    public void testNotEqualsTo() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(10, 21, 30);
        assertNotEquals(op1, op2);
    }

    @Test
    public void testNotEqualsLength() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(10, 20, 31);
        assertNotEquals(op1, op2);
    }

     @Test
    public void testHashCode() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(10, 20, 30);
        assertEquals(op1.hashCode(), op2.hashCode());
    }

    @Test
    public void testHashCodeDifferentRipIndex() throws IOException {
        PutInvariantMediaOp op1 = new PutInvariantMediaOp(10, 20, 30);
        PutInvariantMediaOp op2 = new PutInvariantMediaOp(11, 20, 30);
        assertNotEquals(op1.hashCode(), op2.hashCode());
    }
}