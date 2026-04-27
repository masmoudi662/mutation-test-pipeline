java
package io.vertx.workshop.portfolio.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.workshop.portfolio.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(VertxExtension.class)
class PortfolioServiceImplTest {

    private PortfolioServiceImpl portfolioService;
    private Portfolio portfolio;
    private Handler<AsyncResult<Portfolio>> resultHandler;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio();
        portfolioService = new PortfolioServiceImpl(null, null);
        portfolioService.portfolio = portfolio;
        resultHandler = Mockito.mock(Handler.class);
    }

    @Test
    void sell_invalidAmount() {
        JsonObject quote = new JsonObject().put("name", "FOO").put("bid", 10.0);
        portfolioService.sell(0, quote, resultHandler);
        ArgumentCaptor<AsyncResult<Portfolio>> captor = ArgumentCaptor.forClass(AsyncResult.class);
        verify(resultHandler).handle(captor.capture());
        assertTrue(captor.getValue().failed());
        assertEquals("Cannot sell FOO - the amount must be greater than 0", captor.getValue().cause().getMessage());
    }

    @Test
    void sell_notEnoughStocks() {
        JsonObject quote = new JsonObject().put("name", "FOO").put("bid", 10.0);
        portfolio.getShares().put("FOO", 10);
        portfolioService.sell(20, quote, resultHandler);
        ArgumentCaptor<AsyncResult<Portfolio>> captor = ArgumentCaptor.forClass(AsyncResult.class);
        verify(resultHandler).handle(captor.capture());
        assertTrue(captor.getValue().failed());
        assertEquals("Cannot sell 20 of FOO - not enough stocks in portfolio", captor.getValue().cause().getMessage());
    }

    @Test
    void sell_successful() {
        JsonObject quote = new JsonObject().put("name", "FOO").put("bid", 10.0);
        portfolio.getShares().put("FOO", 20);
        portfolio.setCash(100.0);
        portfolioService.sell(10, quote, resultHandler);
        ArgumentCaptor<AsyncResult<Portfolio>> captor = ArgumentCaptor.forClass(AsyncResult.class);
        verify(resultHandler).handle(captor.capture());
        assertTrue(captor.getValue().succeeded());
        assertEquals(10, portfolio.getShares().get("FOO"));
        assertEquals(200.0, portfolio.getCash());
        assertEquals(10, portfolio.getAmount("FOO"));
    }

    @Test
    void sell_successful_removeStock() {
        JsonObject quote = new JsonObject().put("name", "FOO").put("bid", 10.0);
        portfolio.getShares().put("FOO", 10);
        portfolio.setCash(100.0);
        portfolioService.sell(10, quote, resultHandler);
        ArgumentCaptor<AsyncResult<Portfolio>> captor = ArgumentCaptor.forClass(AsyncResult.class);
        verify(resultHandler).handle(captor.capture());
        assertTrue(captor.getValue().succeeded());
        assertNull(portfolio.getShares().get("FOO"));
        assertEquals(200.0, portfolio.getCash());
        assertEquals(0, portfolio.getAmount("FOO"));
    }

    @Test
    void buy() {
        // Buy tests are not required
    }
}