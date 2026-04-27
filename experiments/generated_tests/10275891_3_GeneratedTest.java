java
package org.eluder.coverage.sample;

import org.junit.Test;

public class PartialCoverageTest {

    @Test
    public void testPartialTrue() {
        new PartialCoverage().partial(true);
    }

    @Test
    public void testPartialFalse() {
        new PartialCoverage().partial(false);
    }
}