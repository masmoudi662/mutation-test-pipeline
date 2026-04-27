java
package com.acme.counter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CounterViewModelTest {

    private CounterViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new CounterViewModel();
    }

    @Test
    public void increment() {
        int initialCount = viewModel.getCount();
        viewModel.increment();
        assertEquals(initialCount + 1, viewModel.getCount());
    }

    @Test
    public void decrement() {
        int initialCount = viewModel.getCount();
        viewModel.decrement();
        assertEquals(initialCount - 1, viewModel.getCount());
    }

    @Test
    public void setCount() {
        viewModel.setCount(10);
        assertEquals(10, viewModel.getCount());
    }

    @Test
    public void getCount() {
        viewModel.setCount(5);
        assertEquals(5, viewModel.getCount());
    }

    @Test
    public void incrementMultipleTimes() {
        viewModel.increment();
        viewModel.increment();
        viewModel.increment();
        assertEquals(3, viewModel.getCount());
    }

    @Test
    public void decrementMultipleTimes() {
        viewModel.decrement();
        viewModel.decrement();
        assertEquals(-2, viewModel.getCount());
    }

    @Test
    public void incrementAndDecrement() {
        viewModel.increment();
        viewModel.decrement();
        assertEquals(0, viewModel.getCount());
    }

    @Test
    public void setCountAndIncrement() {
        viewModel.setCount(5);
        viewModel.increment();
        assertEquals(6, viewModel.getCount());
    }

    @Test
    public void setCountAndDecrement() {
        viewModel.setCount(5);
        viewModel.decrement();
        assertEquals(4, viewModel.getCount());
    }
}