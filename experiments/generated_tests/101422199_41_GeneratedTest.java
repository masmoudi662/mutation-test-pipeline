java
package org.jfleet.inspection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.jfleet.EntityFieldType;
import org.jfleet.EntityFieldType.FieldTypeEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldTypeInspectorTest {

    private final FieldTypeInspector inspector = new FieldTypeInspector();

    @Test
    void getFieldType_Long() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(Long.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.LONG, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_long() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(long.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.LONG, fieldType.get().getFieldType());
        assertTrue(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_String() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(String.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.STRING, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_Timestamp() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(Timestamp.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.TIMESTAMP, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_Time() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(Time.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.TIME, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_BigDecimal() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(BigDecimal.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.BIGDECIMAL, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_LocalDate() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(LocalDate.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.LOCALDATE, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_LocalDateTime() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(LocalDateTime.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.LOCALDATETIME, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }

    @Test
    void getFieldType_Unknown() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(Object.class);
        assertFalse(fieldType.isPresent());
    }

    @Test
    void getFieldType_BigInteger() {
        Optional<EntityFieldType> fieldType = inspector.getFieldType(BigInteger.class);
        assertTrue(fieldType.isPresent());
        assertEquals(FieldTypeEnum.BIGINTEGER, fieldType.get().getFieldType());
        assertFalse(fieldType.get().isPrimitive());
    }
}