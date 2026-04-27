java
package com.muzima.view.progressdialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.muzima.R;
import com.muzima.utils.MuzimaPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MuzimaProgressDialogTest {

    private MuzimaProgressDialog muzimaProgressDialog;

    @Mock
    private ProgressDialog progressDialog;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        muzimaProgressDialog = new MuzimaProgressDialog();
        muzimaProgressDialog.dialog = progressDialog;
    }

    @Test
    public void dismiss_dialogIsShowing_shouldDismissDialog() {
        when(progressDialog.isShowing()).thenReturn(true);
        doNothing().when(progressDialog).dismiss();
        muzimaProgressDialog.dismiss();
        verify(progressDialog).dismiss();
    }

    @Test
    public void dismiss_dialogIsNotShowing_shouldNotDismissDialog() {
        when(progressDialog.isShowing()).thenReturn(false);
        muzimaProgressDialog.dismiss();
        verify(progressDialog, never()).dismiss();
    }
}