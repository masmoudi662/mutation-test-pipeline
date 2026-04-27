java
package com.jzy.game.engine.math;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathUtilTest {

    @Test
    public void testIsZero_positive() {
        assertTrue(MathUtil.isZero(0.000001f));
    }

    @Test
    public void testIsZero_negative() {
        assertTrue(MathUtil.isZero(-0.000001f));
    }

    @Test
    public void testIsZero_zero() {
        assertTrue(MathUtil.isZero(0.0f));
    }

    @Test
    public void testIsZero_positive_not_zero() {
        assertFalse(MathUtil.isZero(0.1f));
    }

    @Test
    public void testIsZero_negative_not_zero() {
        assertFalse(MathUtil.isZero(-0.1f));
    }

    @Test
    public void testIsZero_rounding_error() {
        assertTrue(MathUtil.isZero(MathUtil.FLOAT_ROUNDING_ERROR));
    }

    @Test
     public void testIsZero_just_above_rounding_error() {
        assertFalse(MathUtil.isZero(MathUtil.FLOAT_ROUNDING_ERROR * 2));
    }

    @Test
     public void testIsZero_just_below_rounding_error() {
        assertTrue(MathUtil.isZero(MathUtil.FLOAT_ROUNDING_ERROR / 2));
    }
}