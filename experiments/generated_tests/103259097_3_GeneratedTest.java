java
package net.jspiner.zeplindiff.ui.login;

import net.jspiner.zeplindiff.api.Api;
import net.jspiner.zeplindiff.model.User;
import net.jspiner.zeplindiff.utils.KeyManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoginPresenterTest {

    @Mock
    private Contract.View view;
    @Mock
    private Api api;
    @Mock
    private Call<User> call;
    @Captor
    private ArgumentCaptor<Callback<User>> callbackArgumentCaptor;

    private LoginPresenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new LoginPresenter();
        presenter.attachView(view);
        presenter.api = api;

        when(api.login(anyString(), anyString())).thenReturn(call);
    }

    @Test
    public void onLoginButtonClicked_invalidInput_showToast() {
        presenter.onLoginButtonClicked("", "");

        verify(view).showInputInvaildToast();
        verify(api, never()).login(anyString(), anyString());
    }

    @Test
    public void onLoginButtonClicked_validInput_requestLogin() {
        String id = "testId";
        String password = "testPassword";

        presenter.onLoginButtonClicked(id, password);

        verify(view, never()).showInputInvaildToast();
        verify(api).login(id, password);
        verify(call).enqueue(callbackArgumentCaptor.capture());
    }

    @Test
    public void isUserInputVaild_emptyIdAndPassword_returnFalse() {
        boolean result = presenter.isUserInputVaild("", "");
        assert !result;
    }

    @Test
    public void isUserInputVaild_notEmptyIdAndPassword_returnTrue() {
        boolean result = presenter.isUserInputVaild("id", "password");
        assert result;
    }
}