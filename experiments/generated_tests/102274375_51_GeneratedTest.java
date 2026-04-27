java
package com.readlearncode.constraints.notblank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("bean-validation")
class PersonTest {

    private Validator validator;

    private Person person;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        person = new Person();
    }


    @Test
    void whenCarsAreNotBlank_shouldPassValidation() {
        ArrayList<String> cars = new ArrayList<>();
        cars.add("BMW");
        cars.add("Mercedes");
        person.setCars(cars);
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(0, violations.size(), "No violations expected");
    }


    @Test
    void whenCarsAreBlank_shouldFailValidation() {
        ArrayList<String> cars = new ArrayList<>();
        cars.add(" ");
        cars.add("");
        person.setCars(cars);
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(2, violations.size(), "2 violations expected");
    }

    @Test
    void whenFirstNameIsNotBlank_shouldPassValidation() {
        person.setFirstName("John");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(0, violations.size(), "No violations expected");
    }

    @Test
    void whenFirstNameIsBlank_shouldFailValidation() {
        person.setFirstName(" ");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size(), "1 violation expected");
    }

    @Test
    void whenLastNameIsNotBlank_shouldPassValidation() {
        person.setLastName("Doe");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(0, violations.size(), "No violations expected");
    }

    @Test
    void whenLastNameIsBlank_shouldFailValidation() {
        person.setLastName(" ");
        Set<ConstraintViolation<Person>> violations = validator.validate(person);
        assertEquals(1, violations.size(), "1 violation expected");
    }
}