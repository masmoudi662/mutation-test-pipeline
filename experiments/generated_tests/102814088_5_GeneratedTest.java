java
package vn.tiki.collectionview;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class CollectionViewPresenterTest {

    private CollectionViewPresenter presenter;

    @Mock
    private CollectionView collectionView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new CollectionViewPresenter();
    }

    @Test
    public void attach_shouldSetCollectionViewAndCallOnLoad() {
        presenter.attach(collectionView);
    }
}