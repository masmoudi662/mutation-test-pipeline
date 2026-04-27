java
package android.arch.util.paging;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.arch.core.executor.ArchTaskExecutor;
import android.arch.core.executor.TaskExecutor;
import android.support.annotation.NonNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@RunWith(JUnit4.class)
public class PagedListTest {

    private TaskExecutor mOverrideTaskExecutor;

    @Before
    public void setUp() {
        mOverrideTaskExecutor = new TaskExecutor() {
            @Override
            public void executeOnDiskIO(@NonNull Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(@NonNull Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        };
        ArchTaskExecutor.getInstance().setDelegate(mOverrideTaskExecutor);
    }

    @After
    public void tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

    @Test
    public void testInitializeFrom() {
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(10).build();
        DataSource<Integer, String> dataSource = mock(DataSource.class);

        PagedList<String> oldPagedList = new PagedList<>(dataSource,
                mock(Executor.class), mock(Executor.class), null, config);

        oldPagedList.mAnchor = 5;
        oldPagedList.mInterestedKeyOffset = 0;
        oldPagedList.mItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            oldPagedList.mItems.add("Item " + i);
        }

        when(dataSource.getKey("Item 4")).thenReturn(4);
        when(dataSource.loadAfterInitial(4, 10)).thenReturn(List.of("New Item 1", "New Item 2"));

        PagedList<String> newPagedList = new PagedList<>(dataSource,
                mock(Executor.class), mock(Executor.class), null, config);

        boolean result = newPagedList.initializeFrom(oldPagedList);

        assertTrue(result);
        assertTrue(newPagedList.mInitialized);
        assertEquals(2, newPagedList.size());
        assertEquals("New Item 1", newPagedList.get(0));
        assertEquals("New Item 2", newPagedList.get(1));
    }

    @Test
    public void testInitializeFromNullInitialData() {
        PagedList.Config config = new PagedList.Config.Builder().setPageSize(10).build();
        DataSource<Integer, String> dataSource = mock(DataSource.class);

        PagedList<String> oldPagedList = new PagedList<>(dataSource,
                mock(Executor.class), mock(Executor.class), null, config);

        oldPagedList.mAnchor = 5;
        oldPagedList.mInterestedKeyOffset = 0;
        oldPagedList.mItems = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            oldPagedList.mItems.add("Item " + i);
        }

        when(dataSource.getKey("Item 4")).thenReturn(4);
        when(dataSource.loadAfterInitial(4, 10)).thenReturn(null);

        PagedList<String> newPagedList = new PagedList<>(dataSource,
                mock(Executor.class), mock(Executor.class), null, config);

        boolean result = newPagedList.initializeFrom(oldPagedList);

        assertFalse(result);
        assertFalse(newPagedList.mInitialized);
    }
}