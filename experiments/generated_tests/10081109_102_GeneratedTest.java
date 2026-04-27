java
package com.rackspacecloud.blueflood.service;

import com.rackspacecloud.blueflood.rollup.Granularity;
import com.rackspacecloud.blueflood.rollup.SlotKey;
import com.rackspacecloud.blueflood.types.Locator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;

public class LocatorFetchRunnableTest {

    private LocatorFetchRunnable locatorFetchRunnable;
    private RollupExecutionContext executionContext;
    private SlotKey parentSlotKey;

    @Before
    public void setup() {
        executionContext = Mockito.mock(RollupExecutionContext.class);
        parentSlotKey = Mockito.mock(SlotKey.class);

        locatorFetchRunnable = new LocatorFetchRunnable() {
            @Override
            protected Set<Locator> getLocators(RollupExecutionContext executionContext) {
                return Collections.singleton(new Locator("ac1", "m1"));
            }

            @Override
            protected Set<Locator> getDelayedLocators(RollupExecutionContext executionContext, SlotKey slotKey) {
                return Collections.singleton(new Locator("ac2", "m2"));
            }

            @Override
            public Granularity getGranularity() {
                return Granularity.MIN_5;
            }

            @Override
            public SlotKey getSlotKey() {
                return parentSlotKey;
            }
        };
    }

    @Test
    public void testGetLocators_noDelayedMetrics() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = false;
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, false, Granularity.MIN_20, Granularity.MIN_20);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac1", "m1")));
    }

    @Test
    public void testGetLocators_isReroll_coarserRerollGranularity() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = true;
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, true, Granularity.MIN_15, Granularity.MIN_20);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac1", "m1")));
    }

    @Test
    public void testGetLocators_isReroll_coarserStorageGranularity() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = true;
        when(parentSlotKey.getChildrenKeys(Granularity.MIN_20)).thenReturn(Collections.singletonList(Mockito.mock(SlotKey.class)));
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, true, Granularity.MIN_5, Granularity.MIN_20);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac2", "m2")));
    }

    @Test
    public void testGetLocators_isReroll_sameStorageGranularity() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = true;
        when(parentSlotKey.extrapolate(Granularity.MIN_5)).thenReturn(Mockito.mock(SlotKey.class));
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, true, Granularity.MIN_5, Granularity.MIN_5);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac2", "m2")));
    }

    @Test
    public void testGetLocators_isNotReroll() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = true;
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, false, Granularity.MIN_5, Granularity.MIN_5);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac1", "m1")));
    }

    @Test
    public void testGetLocators_delayedMetricsDisabled() {
        LocatorFetchRunnable.RECORD_DELAYED_METRICS = false;
        Set<Locator> locators = locatorFetchRunnable.getLocators(executionContext, true, Granularity.MIN_5, Granularity.MIN_5);
        Assert.assertEquals(1, locators.size());
        Assert.assertTrue(locators.contains(new Locator("ac1", "m1")));
    }
}