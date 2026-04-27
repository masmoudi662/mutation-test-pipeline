java
package org.jscience.physics.unit;

import org.jscience.physics.dimension.PhysicsDimension;
import org.jscience.physics.unit.converters.AddConverter;
import org.jscience.physics.unit.converters.MultiplyConverter;
import org.junit.Test;
import static org.junit.Assert.*;

public class PhysicsUnitTest {

    @Test
    public void testGetSymbol() {
        PhysicsUnitImpl unit = new PhysicsUnitImpl();
        assertNull(unit.getSymbol());
    }

    private static class PhysicsUnitImpl extends PhysicsUnit<PhysicsUnitImpl> {

        @Override
        public PhysicsDimension getDimension() {
            return null;
        }

        @Override
        public PhysicsConverter toSI() {
            return null;
        }

        @Override
        public String getSymbol() {
            return null;
        }

        @Override
        public PhysicsUnitImpl to(PhysicsUnit other) {
            return this;
        }

        @Override
        protected PhysicsUnitImpl create(PhysicsDimension dimension) {
            return this;
        }

        @Override
        protected PhysicsUnitImpl create(MultiplyConverter converterToSI) {
            return this;
        }

        @Override
        protected PhysicsUnitImpl create(AddConverter converterToSI) {
            return this;
        }

        @Override
        protected PhysicsUnitImpl create(MultiplyConverter converterToSI, PhysicsDimension dimension) {
            return this;
        }
    }
}