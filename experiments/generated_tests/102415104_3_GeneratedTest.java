java
package br.com.liveo.mvp.screen.home;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.liveo.mvp.model.user.UserResponse;
import br.com.liveo.mvp.repositories.user.UserRepository;
import br.com.liveo.mvp.util.scheduler.BaseScheduler;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.*;

public class HomePresenterTest {

    @Mock
    private HomeView mView;

    @Mock
    private UserRepository mRepository;

    @Mock
    private BaseScheduler mScheduler;

    @InjectMocks
    private HomePresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mScheduler.io()).thenReturn(Schedulers.trampoline());
        when(mScheduler.ui()).thenReturn(Schedulers.trampoline());
    }

    @Test
    public void testFetchUsers_success() {
        UserResponse userResponse = new UserResponse();
        when(mView.getPage()).thenReturn(1);
        when(mRepository.fetchUsers(1)).thenReturn(Observable.just(userResponse));

        mPresenter.fetchUsers();

        verify(mView).onLoading(true);
        verify(mView).onLoading(false);
        verify(mView).onUserResponse(userResponse);
        verify(mView, never()).onError(any(Throwable.class));
    }

    @Test
    public void testFetchUsers_error() {
        Throwable error = new Throwable("Test error");
        when(mView.getPage()).thenReturn(1);
        when(mRepository.fetchUsers(1)).thenReturn(Observable.error(error));

        mPresenter.fetchUsers();

        verify(mView).onLoading(true);
        verify(mView).onLoading(false);
        verify(mView).onError(error);
        verify(mView, never()).onUserResponse(any(UserResponse.class));
    }

    @Test
    public void testFetchUsers_nullView() {
        mPresenter.detachView();
        mPresenter.fetchUsers();

        verify(mView, never()).onLoading(anyBoolean());
        verify(mRepository, never()).fetchUsers(anyInt());
    }
}