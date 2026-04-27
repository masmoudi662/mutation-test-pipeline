java
package org.nzbhydra.indexers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nzbhydra.config.SearchSource;
import org.nzbhydra.config.searching.SearchType;
import org.nzbhydra.searching.SearchResult;
import org.nzbhydra.searching.db.SearchResultEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BinsearchTest {

    @Test
    void completeIndexerSearchResult_withNavigationTable_hasMore() {
        Binsearch binsearch = new Binsearch();
        String response = "<html><body><table class=\"xMenuT\"><tr><td></td></tr><tr><td class=\"tdPageLinks\"><a href=\"#\">&lt;</a> <a href=\"#\">1</a> <a href=\"#\">2</a> <a href=\"#\">></a></td></tr></table></body></html>";
        IndexerSearchResult indexerSearchResult = new IndexerSearchResult();
        indexerSearchResult.setSearchResultItems(Collections.emptyList());
        SearchRequest searchRequest = new SearchRequest(SearchType.SEARCH, "test", 0, 50, SearchSource.INTERNAL);

        binsearch.completeIndexerSearchResult(response, indexerSearchResult, null, searchRequest, 0, 50);

        assertTrue(indexerSearchResult.isHasMoreResults());
        assertEquals(100, indexerSearchResult.getTotalResults());
        assertFalse(indexerSearchResult.isTotalResultsKnown());
        assertEquals(50, indexerSearchResult.getLimit());
        assertEquals(0, indexerSearchResult.getOffset());
    }

    @Test
    void completeIndexerSearchResult_withNavigationTable_noMore() {
        Binsearch binsearch = new Binsearch();
        String response = "<html><body><table class=\"xMenuT\"><tr><td></td></tr><tr><td class=\"tdPageLinks\"><a href=\"#\">&lt;</a> <a href=\"#\">1</a> <a href=\"#\">2</a></td></tr></table></body></html>";
        IndexerSearchResult indexerSearchResult = new IndexerSearchResult();
        indexerSearchResult.setSearchResultItems(Collections.emptyList());
        SearchRequest searchRequest = new SearchRequest(SearchType.SEARCH, "test", 0, 50, SearchSource.INTERNAL);

        binsearch.completeIndexerSearchResult(response, indexerSearchResult, null, searchRequest, 0, 50);

        assertFalse(indexerSearchResult.isHasMoreResults());
        assertEquals(0, indexerSearchResult.getTotalResults());
        assertTrue(indexerSearchResult.isTotalResultsKnown());
        assertEquals(50, indexerSearchResult.getLimit());
        assertEquals(0, indexerSearchResult.getOffset());
    }

    @Test
    void completeIndexerSearchResult_withoutNavigationTable() {
        Binsearch binsearch = new Binsearch();
        String response = "<html><body><h1>No results found</h1></body></html>";
        IndexerSearchResult indexerSearchResult = new IndexerSearchResult();
        SearchRequest searchRequest = new SearchRequest(SearchType.SEARCH, "test", 0, 50, SearchSource.INTERNAL);

        binsearch.completeIndexerSearchResult(response, indexerSearchResult, null, searchRequest, 0, 50);

        assertFalse(indexerSearchResult.isHasMoreResults());
        assertEquals(0, indexerSearchResult.getTotalResults());
        assertTrue(indexerSearchResult.isTotalResultsKnown());
    }

    @Test
    void completeIndexerSearchResult_withResults() {
        Binsearch binsearch = new Binsearch();
        String response = "<html><body><table class=\"xMenuT\"><tr><td></td></tr><tr><td class=\"tdPageLinks\"><a href=\"#\">&lt;</a> <a href=\"#\">1</a> <a href=\"#\">2</a></td></tr></table></body></html>";
        IndexerSearchResult indexerSearchResult = new IndexerSearchResult();
        indexerSearchResult.setSearchResultItems(List.of(Mockito.mock(SearchResultEntity.class), Mockito.mock(SearchResultEntity.class)));
        SearchRequest searchRequest = new SearchRequest(SearchType.SEARCH, "test", 0, 50, SearchSource.INTERNAL);

        binsearch.completeIndexerSearchResult(response, indexerSearchResult, null, searchRequest, 0, 50);

        assertFalse(indexerSearchResult.isHasMoreResults());
        assertEquals(2, indexerSearchResult.getTotalResults());
        assertTrue(indexerSearchResult.isTotalResultsKnown());
    }

    @Test
    void completeIndexerSearchResult_withResultsAndHasMore() {
        Binsearch binsearch = new Binsearch();
        String response = "<html><body><table class=\"xMenuT\"><tr><td></td></tr><tr><td class=\"tdPageLinks\"><a href=\"#\">&lt;</a> <a href=\"#\">1</a> <a href=\"#\">2</a> <a href=\"#\">></a></td></tr></table></body></html>";
        IndexerSearchResult indexerSearchResult = new IndexerSearchResult();
        indexerSearchResult.setSearchResultItems(List.of(Mockito.mock(SearchResultEntity.class), Mockito.mock(SearchResultEntity.class)));
        SearchRequest searchRequest = new SearchRequest(SearchType.SEARCH, "test", 0, 50, SearchSource.INTERNAL);

        binsearch.completeIndexerSearchResult(response, indexerSearchResult, null, searchRequest, 0, 50);

        assertTrue(indexerSearchResult.isHasMoreResults());
        assertEquals(100, indexerSearchResult.getTotalResults());
        assertFalse(indexerSearchResult.isTotalResultsKnown());
    }
}