java
package fr.cph.stock.entities;

import fr.cph.stock.enumtype.Currency;
import fr.cph.stock.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PortfolioTest {

    private Portfolio portfolio;

    @Mock
    private Equity equity1;
    @Mock
    private Equity equity2;
    @Mock
    private Company company1;
    @Mock
    private Company company2;

    @Before
    public void setUp() {
        portfolio = new Portfolio();
        portfolio.setEquities(new ArrayList<>());
    }

    @Test
    public void testGetChartSectorDataEmpty() {
        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetChartSectorDataWithSector() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn("Technology");
        when(equity1.getValue()).thenReturn(100.0);
        when(equity2.getCompany()).thenReturn(company2);
        when(equity2.getCurrentSector()).thenReturn("Finance");
        when(equity2.getValue()).thenReturn(200.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        equities.add(equity2);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(2, result.size());
        assertEquals(100.0, result.get("Technology"), 0.001);
        assertEquals(200.0, result.get("Finance"), 0.001);
    }

    @Test
    public void testGetChartSectorDataWithFund() {
        when(equity1.getCompany()).thenReturn(company1);
        when(company1.getFund()).thenReturn(true);
        when(equity1.getValue()).thenReturn(100.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(1, result.size());
        assertEquals(100.0, result.get("Fund"), 0.001);
    }

    @Test
    public void testGetChartSectorDataWithUnknownSector() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn(null);
        when(equity1.getValue()).thenReturn(100.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(1, result.size());
        assertEquals(100.0, result.get("Unknown"), 0.001);
    }

    @Test
    public void testGetChartSectorDataWithEmptySector() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn("");
        when(equity1.getValue()).thenReturn(100.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(1, result.size());
        assertEquals(100.0, result.get("Unknown"), 0.001);
    }

    @Test
    public void testGetChartSectorDataMultipleEquitiesSameSector() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn("Technology");
        when(equity1.getValue()).thenReturn(100.0);
        when(equity2.getCompany()).thenReturn(company2);
        when(equity2.getCurrentSector()).thenReturn("Technology");
        when(equity2.getValue()).thenReturn(200.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        equities.add(equity2);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();
        assertEquals(1, result.size());
        assertEquals(300.0, result.get("Technology"), 0.001);
    }

    @Test
    public void testGetChartSectorDataCached() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn("Technology");
        when(equity1.getValue()).thenReturn(100.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        portfolio.setEquities(equities);

        Map<String, Double> firstCall = portfolio.getChartSectorData();
        Map<String, Double> secondCall = portfolio.getChartSectorData();

        assertEquals(firstCall, secondCall);
    }

    @Test
    public void testGetChartSectorDataTreeMapOrder() {
        when(equity1.getCompany()).thenReturn(company1);
        when(equity1.getCurrentSector()).thenReturn("C");
        when(equity1.getValue()).thenReturn(100.0);

        when(equity2.getCompany()).thenReturn(company2);
        when(equity2.getCurrentSector()).thenReturn("A");
        when(equity2.getValue()).thenReturn(200.0);

        Equity equity3 = new Equity();
        Company company3 = new Company();
        when(equity3.getCompany()).thenReturn(company3);
        when(equity3.getCurrentSector()).thenReturn("B");
        when(equity3.getValue()).thenReturn(300.0);

        List<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        equities.add(equity2);
        equities.add(equity3);
        portfolio.setEquities(equities);

        Map<String, Double> result = portfolio.getChartSectorData();

        List<String> keys = new ArrayList<>(result.keySet());
        assertEquals("A", keys.get(0));
        assertEquals("B", keys.get(1));
        assertEquals("C", keys.get(2));
    }
}