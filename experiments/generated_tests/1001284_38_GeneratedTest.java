java
package com.pholser.junit.quickcheck.internal;

import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GeometricDistributionTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSampleInvalidProbability() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        distribution.sample(1.1, random);
    }

    @Test
    public void testSampleProbabilityOne() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        int result = distribution.sample(1, random);
        assertEquals(0, result);
    }

    @Test
    public void testSampleProbabilityZero() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        when(random.nextDouble()).thenReturn(0.5);
        int result = distribution.sample(0.0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001, random);
        assertTrue(result > 0);
    }

    @Test
    public void testSampleTypicalCase() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        when(random.nextDouble()).thenReturn(0.5);
        int result = distribution.sample(0.5, random);
        assertTrue(result > 0);
    }

    @Test
    public void testSampleWithMockRandom() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        when(random.nextDouble()).thenReturn(0.25);
        int result = distribution.sample(0.75, random);
        assertTrue(result > 0);
    }

    @Test
    public void testSampleSmallProbability() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        when(random.nextDouble()).thenReturn(0.9);
        int result = distribution.sample(0.1, random);
        assertTrue(result > 0);
    }

    @Test
    public void testSampleLargeProbability() {
        GeometricDistribution distribution = new GeometricDistribution();
        SourceOfRandomness random = mock(SourceOfRandomness.class);
        when(random.nextDouble()).thenReturn(0.1);
        int result = distribution.sample(0.9, random);
        assertTrue(result > 0);
    }
}