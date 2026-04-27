java
package org.jeasy.random.randomizers.range;

import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LongRangeRandomizerTest {

    @Test
    void generateInRange() {
        Long min = 10L;
        Long max = 100L;
        LongRangeRandomizer longRangeRandomizer = new LongRangeRandomizer(min, max);
        Long randomNumber = longRangeRandomizer.getRandomValue();
        assertThat(randomNumber).isBetween(min, max);
    }

    @Test
    void generateEqualsMaxWhenMinEqualsMax() {
        Long min = 10L;
        Long max = 10L;
        LongRangeRandomizer longRangeRandomizer = new LongRangeRandomizer(min, max);
        Long randomNumber = longRangeRandomizer.getRandomValue();
        assertThat(randomNumber).isEqualTo(min);
    }

    @Test
    void generateInRangeWithNullMin() {
        Long min = null;
        Long max = 100L;
        LongRangeRandomizer longRangeRandomizer = new LongRangeRandomizer(min, max);
        Long randomNumber = longRangeRandomizer.getRandomValue();
        assertThat(randomNumber).isLessThanOrEqualTo(max);
    }

    @Test
    void generateInRangeWithNullMax() {
        Long min = 10L;
        Long max = null;
        LongRangeRandomizer longRangeRandomizer = new LongRangeRandomizer(min, max);
        Long randomNumber = longRangeRandomizer.getRandomValue();
        assertThat(randomNumber).isGreaterThanOrEqualTo(min);
    }

    @Test
    void generateInRangeWithNullMinAndMax() {
        Long min = null;
        Long max = null;
        LongRangeRandomizer longRangeRandomizer = new LongRangeRandomizer(min, max);
        Long randomNumber = longRangeRandomizer.getRandomValue();
        assertThat(randomNumber).isNotNull();
    }

    @Test
    void aNewLongRangeRandomizerIsNotNull() {
        LongRangeRandomizer longRangeRandomizer = LongRangeRandomizer.aNewLongRangeRandomizer(1L, 100L);
        assertThat(longRangeRandomizer).isNotNull();
    }

    @Test
    void issue423() {
        // Given
        final long min = 0;
        final long max = 1;
        final EasyRandomParameters parameters = new EasyRandomParameters().seed(123L);

        // When
        final LongRangeRandomizer randomizer = new LongRangeRandomizer(min, max, parameters.getRandom());

        // Then
        assertThat(randomizer.getRandomValue()).isEqualTo(0L);
        assertThat(randomizer.getRandomValue()).isEqualTo(0L);
        assertThat(randomizer.getRandomValue()).isEqualTo(1L);
        assertThat(randomizer.getRandomValue()).isEqualTo(1L);
        assertThat(randomizer.getRandomValue()).isEqualTo(0L);
    }
}