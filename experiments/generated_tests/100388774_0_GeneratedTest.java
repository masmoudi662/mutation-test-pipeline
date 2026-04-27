java
package uk.nhs.careconnect.ri.database.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseHumanNameTest {

    private BaseHumanName humanName;

    @BeforeEach
    void setUp() {
        humanName = new BaseHumanName() {
            @Override
            public Long getId() {
                return null;
            }
        };
    }

    @Test
    void getDisplayName_allFieldsPresent() {
        humanName.setPrefix("Mr.");
        humanName.setGivenName("John");
        humanName.setFamilyName("Doe");
        humanName.setSuffix("Jr.");
        assertEquals("Mr. John Doe Jr.", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_noPrefix() {
        humanName.setGivenName("John");
        humanName.setFamilyName("Doe");
        humanName.setSuffix("Jr.");
        assertEquals("John Doe Jr.", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_noSuffix() {
        humanName.setPrefix("Mr.");
        humanName.setGivenName("John");
        humanName.setFamilyName("Doe");
        assertEquals("Mr. John Doe", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_onlyGivenName() {
        humanName.setGivenName("John");
        assertEquals("John", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_onlyFamilyName() {
        humanName.setFamilyName("Doe");
        assertEquals("Doe", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_emptyFields() {
        assertEquals("", humanName.getDisplayName());
    }

    @Test
    void getDisplayName_nullFields() {
        humanName.setPrefix(null);
        humanName.setGivenName(null);
        humanName.setFamilyName(null);
        humanName.setSuffix(null);
        assertEquals("", humanName.getDisplayName());
    }
}