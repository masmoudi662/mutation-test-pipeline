java
package pl.zankowski.iextrading4j.hist.api.field;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IEXPriceTest {

    @Test
    void testGetNumber() {
        IEXPrice iexPrice = new IEXPrice(12345L);
        assertThat(iexPrice.getNumber()).isEqualTo(12345L);
    }

    @Test
    void testConstructor() {
        IEXPrice iexPrice = new IEXPrice(54321L);
        assertThat(iexPrice.getNumber()).isEqualTo(54321L);
    }

    @Test
    void testCompareToEqual() {
        IEXPrice price1 = new IEXPrice(100L);
        IEXPrice price2 = new IEXPrice(100L);
        assertThat(price1.compareTo(price2)).isEqualTo(0);
    }

    @Test
    void testCompareToGreater() {
        IEXPrice price1 = new IEXPrice(200L);
        IEXPrice price2 = new IEXPrice(100L);
        assertThat(price1.compareTo(price2)).isGreaterThan(0);
    }

    @Test
    void testCompareToLess() {
        IEXPrice price1 = new IEXPrice(50L);
        IEXPrice price2 = new IEXPrice(100L);
        assertThat(price1.compareTo(price2)).isLessThan(0);
    }

    @Test
    void testToString() {
        IEXPrice price = new IEXPrice(98765L);
        assertThat(price.toString()).isEqualTo("IEXPrice{number=98765}");
    }

    @Test
    void testEqualsAndHashCode() {
        IEXPrice price1 = new IEXPrice(123L);
        IEXPrice price2 = new IEXPrice(123L);
        IEXPrice price3 = new IEXPrice(456L);

        assertThat(price1).isEqualTo(price2);
        assertThat(price1).isNotEqualTo(price3);
        assertThat(price1.hashCode()).isEqualTo(price2.hashCode());
        assertThat(price1.hashCode()).isNotEqualTo(price3.hashCode());
    }
}