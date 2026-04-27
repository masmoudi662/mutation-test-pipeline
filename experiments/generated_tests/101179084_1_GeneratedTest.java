java
package com.oussaki.rxfilesdownloader;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxDownloaderTest {

    @Mock
    private Context context;
    private RxDownloader rxDownloader;
    private List<String> files;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        files = new ArrayList<>();
        rxDownloader = new RxDownloader(context, files);
        rxDownloader.ORDER = DownloadStrategy.FLAG_SEQUENTIAL;
    }

    @Test
    public void asList_emptyList_returnsEmptyList() {
        TestObserver<List<FileContainer>> testObserver = rxDownloader.asList().test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(list -> list.isEmpty());
    }

    @Test
    public void asList_singleFile_returnsListWithOneFileContainer() {
        files.add("http://example.com/file1.txt");
        rxDownloader = new RxDownloader(context, files);
        rxDownloader.ORDER = DownloadStrategy.FLAG_SEQUENTIAL;

        TestObserver<List<FileContainer>> testObserver = rxDownloader.asList().test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(list -> list.size() == 1);
    }

    @Test
    public void asList_multipleFilesSequential_returnsListWithMultipleFileContainers() {
        files.add("http://example.com/file1.txt");
        files.add("http://example.com/file2.txt");
        rxDownloader = new RxDownloader(context, files);
        rxDownloader.ORDER = DownloadStrategy.FLAG_SEQUENTIAL;

        TestObserver<List<FileContainer>> testObserver = rxDownloader.asList().test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(list -> list.size() == 2);
    }
    @Test
    public void asList_multipleFilesParallel_returnsListWithMultipleFileContainers() {
        files.add("http://example.com/file1.txt");
        files.add("http://example.com/file2.txt");
        rxDownloader = new RxDownloader(context, files);
        rxDownloader.ORDER = DownloadStrategy.FLAG_PARALLEL;

        TestObserver<List<FileContainer>> testObserver = rxDownloader.asList().test();
        testObserver.awaitTerminalEvent();
        testObserver.assertNoErrors();
        testObserver.assertValue(list -> list.size() == 2);
    }
}