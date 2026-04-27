java
package pl.zankowski.iextrading4j.book.api;

import org.junit.jupiter.api.Test;
import pl.zankowski.iextrading4j.hist.api.field.IEXPrice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PriceLevelTest {

    @Test
    void testEquals() {
        PriceLevel priceLevel1 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel2 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel3 = new PriceLevel("MSFT", 1678886400000L, IEXPrice.of(150.00), 100L);

        assertEquals(priceLevel1, priceLevel2);
        assertNotEquals(priceLevel1, priceLevel3);
    }

    @Test
    void testHashCode() {
        PriceLevel priceLevel1 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel2 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel3 = new PriceLevel("MSFT", 1678886400000L, IEXPrice.of(150.00), 100L);

        assertEquals(priceLevel1.hashCode(), priceLevel2.hashCode());
        assertNotEquals(priceLevel1.hashCode(), priceLevel3.hashCode());
    }

    @Test
    void testToString() {
        PriceLevel priceLevel = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        String expectedString = "PriceLevel{symbol='AAPL', timestamp=1678886400000, price=150.00, size=100}";
        assertEquals(expectedString, priceLevel.toString());
    }

    @Test
    void testGetters() {
        PriceLevel priceLevel = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);

        assertEquals("AAPL", priceLevel.getSymbol());
        assertEquals(1678886400000L, priceLevel.getTimestamp());
        assertEquals(IEXPrice.of(150.00), priceLevel.getPrice());
        assertEquals(100L, priceLevel.getSize());
    }

    @Test
    void testEqualsWithNull() {
        PriceLevel priceLevel = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        assertNotEquals(priceLevel, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        PriceLevel priceLevel = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        assertNotEquals(priceLevel, new Object());
    }

    @Test
    void testEqualsDifferentSymbol() {
        PriceLevel priceLevel1 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel2 = new PriceLevel("MSFT", 1678886400000L, IEXPrice.of(150.00), 100L);
        assertNotEquals(priceLevel1, priceLevel2);
    }

    @Test
    void testEqualsDifferentTimestamp() {
        PriceLevel priceLevel1 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel2 = new PriceLevel("AAPL", 1678886500000L, IEXPrice.of(150.00), 100L);
        assertNotEquals(priceLevel1, priceLevel2);
    }

    @Test
    void testEqualsDifferentPrice() {
        PriceLevel priceLevel1 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(150.00), 100L);
        PriceLevel priceLevel2 = new PriceLevel("AAPL", 1678886400000L, IEXPrice.of(160.00), 100L);
        assertNotEquals(priceLevel1, priceLevel2);
    }
}