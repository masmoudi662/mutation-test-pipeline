java
package com.jokers.common.date;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void isDate_validDateWithTime() {
        assertTrue(DateUtil.isDate("2024-01-01 12:00:00"));
    }

    @Test
    void isDate_invalidDateWithTime() {
        assertFalse(DateUtil.isDate("2024-01-32 12:00:00"));
    }

    @Test
    void isDate_validDateWithoutTime() {
        assertTrue(DateUtil.isDate("2024-01-01", DateUtil.PATTERN_NO_TIME));
    }

    @Test
    void isDate_invalidDateWithoutTime() {
        assertFalse(DateUtil.isDate("2024-01-32", DateUtil.PATTERN_NO_TIME));
    }

    @Test
    void isDate_emptyString() {
        assertFalse(DateUtil.isDate(""));
    }

    @Test
    void isDate_nullString() {
        assertFalse(DateUtil.isDate(null));
    }

    @Test
    void isDate_invalidFormat() {
        assertFalse(DateUtil.isDate("01-01-2024", DateUtil.PATTERN_HAVE_TIME));
    }

    @Test
    void isDate_validDateWithMillis() {
        assertTrue(DateUtil.isDate("2024-01-01 12:00:00.000", DateUtil.PATTERN_HAVE_MILLIS));
    }

    @Test
    void isDate_invalidDateWithMillis() {
        assertFalse(DateUtil.isDate("2024-01-32 12:00:00.000", DateUtil.PATTERN_HAVE_MILLIS));
    }

    @Test
    void isDate_validDateDifferentPattern() {
        assertTrue(DateUtil.isDate("2024/01/01", "yyyy/MM/dd"));
    }
}