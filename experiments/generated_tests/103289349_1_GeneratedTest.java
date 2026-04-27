java
package cz.jcode.auto.value.step.builder.example.primitive;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PrimitiveTest {

    @Test
    void lazyStep() {
        AutoValue_Primitive.PrimitiveIntLazyStep step = Primitive.lazyStep();
        assertNotNull(step);
    }

    @Test
    void testPrimitiveCreation() {
        Primitive primitive = Primitive.builder()
                .intValue(10)
                .longValue(100L)
                .floatValue(3.14f)
                .doubleValue(2.71)
                .booleanValue(true)
                .charValue('A')
                .shortValue((short) 5)
                .byteValue((byte) 1)
                .build();

        assertEquals(10, primitive.intValue());
        assertEquals(100L, primitive.longValue());
        assertEquals(3.14f, primitive.floatValue());
        assertEquals(2.71, primitive.doubleValue());
        assertEquals(true, primitive.booleanValue());
        assertEquals('A', primitive.charValue());
        assertEquals((short) 5, primitive.shortValue());
        assertEquals((byte) 1, primitive.byteValue());
    }

    @Test
    void testLazyStepBuild() {
        Primitive primitive = Primitive.lazyStep()
                .intValue(5)
                .longValue(50L)
                .floatValue(1.618f)
                .doubleValue(0.577)
                .booleanValue(false)
                .charValue('B')
                .shortValue((short) 2)
                .byteValue((byte) 0)
                .build();

        assertEquals(5, primitive.intValue());
        assertEquals(50L, primitive.longValue());
        assertEquals(1.618f, primitive.floatValue());
        assertEquals(0.577, primitive.doubleValue());
        assertEquals(false, primitive.booleanValue());
        assertEquals('B', primitive.charValue());
        assertEquals((short) 2, primitive.shortValue());
        assertEquals((byte) 0, primitive.byteValue());
    }
}