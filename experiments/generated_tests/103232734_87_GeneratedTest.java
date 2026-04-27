java
package pl.mk5.gdx.fireapp.android.auth;

import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.promises.FuturePromise;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthPromiseConsumerTest {

    private AuthPromiseConsumer<Object> authPromiseConsumer;

    @Mock
    private Task<Object> task;
    @Mock
    private FuturePromise<GdxFirebaseUser> promise;
    @Mock
    private GdxFIRAuth gdxFIRAuth;
    @Mock
    private GdxFirebaseUser gdxFirebaseUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        authPromiseConsumer = new AuthPromiseConsumer<Object>();
        authPromiseConsumer.task = task;
    }

    @Test
    public void accept_successfulTask_completesPromise() {
        when(task.isSuccessful()).thenReturn(true);
        GdxFIRAuth.instance = gdxFIRAuth;
        when(gdxFIRAuth.getCurrentUser()).thenReturn(gdxFirebaseUser);

        authPromiseConsumer.accept(promise);

        ArgumentCaptor<com.google.android.gms.tasks.OnCompleteListener> captor = ArgumentCaptor.forClass(com.google.android.gms.tasks.OnCompleteListener.class);
        verify(task).addOnCompleteListener(captor.capture());
        captor.getValue().onComplete(task);

        verify(promise).doComplete(gdxFirebaseUser);
        GdxFIRAuth.instance = null;
    }

    @Test
    public void accept_failedTask_failsPromise() {
        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(new Exception("Test Exception"));

        authPromiseConsumer.accept(promise);

        ArgumentCaptor<com.google.android.gms.tasks.OnCompleteListener> captor = ArgumentCaptor.forClass(com.google.android.gms.tasks.OnCompleteListener.class);
        verify(task).addOnCompleteListener(captor.capture());
        captor.getValue().onComplete(task);

        verify(promise).doFail("Test Exception", new Exception("Test Exception"));
    }

    @Test
    public void accept_failedTask_failsPromise_nullException() {
        when(task.isSuccessful()).thenReturn(false);
        when(task.getException()).thenReturn(null);

        authPromiseConsumer.accept(promise);

        ArgumentCaptor<com.google.android.gms.tasks.OnCompleteListener> captor = ArgumentCaptor.forClass(com.google.android.gms.tasks.OnCompleteListener.class);
        verify(task).addOnCompleteListener(captor.capture());
        captor.getValue().onComplete(task);

        verify(promise).doFail("Authorization fail", null);
    }

    @Test
    public void accept_nullTask_doesNothing() {
        AuthPromiseConsumer<Object> authPromiseConsumer = new AuthPromiseConsumer<>();
        authPromiseConsumer.accept(promise);
    }
}