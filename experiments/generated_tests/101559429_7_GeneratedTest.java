java
package com.mohamadamin.rxactivityresults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import android.content.Intent;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ActivityResultTest {

    @Test
    public void testToString() {
        ActivityResult activityResult = new ActivityResult(1, new Intent());
        String expected = "ActivityResult { ResultCode = 1, Data = Intent {  } }";
        assertEquals(expected, activityResult.toString());
    }

    @Test
    public void testToStringWithNullIntent() {
        ActivityResult activityResult = new ActivityResult(1, null);
        String expected = "ActivityResult { ResultCode = 1, Data = null }";
        assertEquals(expected, activityResult.toString());
    }

    @Test
    public void testToStringWithResultCodeZero() {
        ActivityResult activityResult = new ActivityResult(0, new Intent());
        String expected = "ActivityResult { ResultCode = 0, Data = Intent {  } }";
        assertEquals(expected, activityResult.toString());
    }

    @Test
    public void testToStringWithNegativeResultCode() {
        ActivityResult activityResult = new ActivityResult(-1, new Intent());
        String expected = "ActivityResult { ResultCode = -1, Data = Intent {  } }";
        assertEquals(expected, activityResult.toString());
    }
}