java
package com.techyourchance.threadposters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.techyourchance.threadposter.UiThreadPoster;
import com.techyourchance.threadposter.BackgroundThreadPoster;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FetchDataUseCaseTest {

    private FetchDataUseCase SUT;
    private BackgroundThreadPoster mBackgroundThreadPosterMock;

    @Before
    public void setup() {
        mBackgroundThreadPosterMock = mock(BackgroundThreadPoster.class);
        SUT = new FetchDataUseCase(mBackgroundThreadPosterMock, mock(UiThreadPoster.class));
    }

    @Test
    public void fetchData_postsToBackgroundThread() {
        SUT.fetchData();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        verify(mBackgroundThreadPosterMock).post(runnableCaptor.capture());

        // Execute the runnable to simulate the background thread execution
        runnableCaptor.getValue().run();
    }
}