java
package com.adaptionsoft.games.trivia.runner;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ReplayingRandomTest {

    private ReplayingRandom replayingRandom;

    @Before
    public void setUp() {
        replayingRandom = new ReplayingRandom();
        replayingRandom.addPossibleNines(0);
        replayingRandom.addPossibleFives(0);
    }

    @Test
    public void should_return_0_when_nextInt_is_called_with_9() {
        assertThat(replayingRandom.nextInt(9), is(0));
    }

    @Test
    public void should_return_0_when_nextInt_is_called_with_5() {
        assertThat(replayingRandom.nextInt(5), is(0));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void should_throw_exception_when_nextInt_is_called_with_other_number() {
        replayingRandom.nextInt(10);
    }

    @Test
    public void should_return_next_value_when_addPossibleNines_has_multiple_values() {
        replayingRandom = new ReplayingRandom();
        replayingRandom.addPossibleNines(0);
        replayingRandom.addPossibleNines(1);
        assertThat(replayingRandom.nextInt(9), is(0));
        assertThat(replayingRandom.nextInt(9), is(1));
    }

    @Test
    public void should_return_next_value_when_addPossibleFives_has_multiple_values() {
        replayingRandom = new ReplayingRandom();
        replayingRandom.addPossibleFives(0);
        replayingRandom.addPossibleFives(1);
        assertThat(replayingRandom.nextInt(5), is(0));
        assertThat(replayingRandom.nextInt(5), is(1));
    }
}