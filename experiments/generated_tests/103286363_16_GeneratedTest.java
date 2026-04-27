java
package com.dlsc.formsfx.model.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringLengthValidatorTest {

    @Test
    void testUpTo() {
        StringLengthValidator validator = StringLengthValidator.upTo(5, "Error");
        assertNotNull(validator);
    }

    @Test
    void testExactLength() {
        StringLengthValidator validator = StringLengthValidator.exactly(5, "Error");
        assertNotNull(validator);
    }

    @Test
    void testBetween() {
        StringLengthValidator validator = StringLengthValidator.between(2, 5, "Error");
        assertNotNull(validator);
    }

    @Test
    void testAtLeast() {
        StringLengthValidator validator = StringLengthValidator.atLeast(3, "Error");
        assertNotNull(validator);
    }

    @Test
    void testValidateUpTo() {
        StringLengthValidator validator = StringLengthValidator.upTo(5, "Error");
        assertTrue(validator.validate("test").isValid());
        assertFalse(validator.validate("testing").isValid());
        assertEquals("Error", validator.validate("testing").getMessage());
    }

    @Test
    void testValidateAtLeast() {
        StringLengthValidator validator = StringLengthValidator.atLeast(3, "Error");
        assertTrue(validator.validate("test").isValid());
        assertFalse(validator.validate("te").isValid());
        assertEquals("Error", validator.validate("te").getMessage());
    }

    @Test
    void testValidateBetween() {
        StringLengthValidator validator = StringLengthValidator.between(2, 5, "Error");
        assertTrue(validator.validate("test").isValid());
        assertFalse(validator.validate("t").isValid());
        assertFalse(validator.validate("testing").isValid());
        assertEquals("Error", validator.validate("testing").getMessage());
    }

    @Test
    void testValidateExactLength() {
        StringLengthValidator validator = StringLengthValidator.exactly(4, "Error");
        assertTrue(validator.validate("test").isValid());
        assertFalse(validator.validate("te").isValid());
        assertFalse(validator.validate("testing").isValid());
        assertEquals("Error", validator.validate("testing").getMessage());
    }

    @Test
    void testValidateNull() {
        StringLengthValidator validator = StringLengthValidator.exactly(4, "Error");
        assertTrue(validator.validate(null).isValid());
    }

    @Test
    void testErrorMessage() {
        StringLengthValidator validator = StringLengthValidator.upTo(5, "Custom Error Message");
        assertFalse(validator.validate("toolong").isValid());
        assertEquals("Custom Error Message", validator.validate("toolong").getMessage());
    }
}