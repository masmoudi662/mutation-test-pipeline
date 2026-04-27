java
package com.csci150.newsapp.entirenews.utils;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class UtilsTest {

    @Test
    public void print_debug() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", Log.DEBUG);
        Mockito.verifyStatic(Log.class);
        Log.d("EntireNews (test)", "message");
    }

    @Test
    public void print_error() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", Log.ERROR);
        Mockito.verifyStatic(Log.class);
        Log.e("EntireNews (test)", "message");
    }

    @Test
    public void print_info() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", Log.INFO);
        Mockito.verifyStatic(Log.class);
        Log.i("EntireNews (test)", "message");
    }

    @Test
    public void print_verbose() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", Log.VERBOSE);
        Mockito.verifyStatic(Log.class);
        Log.v("EntireNews (test)", "message");
    }

    @Test
    public void print_warn() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", Log.WARN);
        Mockito.verifyStatic(Log.class);
        Log.w("EntireNews (test)", "message");
    }

    @Test
    public void print_default() {
        PowerMockito.mockStatic(Log.class);
        Utils.print("test", "message", 10);
        Mockito.verifyStatic(Log.class);
        Log.d("EntireNews (test)", "message");
    }

}