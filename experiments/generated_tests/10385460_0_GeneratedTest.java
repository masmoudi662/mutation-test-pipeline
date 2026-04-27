java
package net.sourceforge.jabm.spring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.FactoryBean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PRNGSeedFactoryBeanTest {

    private PRNGSeedFactoryBean prngSeedFactoryBean;

    @Before
    public void setUp() {
        prngSeedFactoryBean = new PRNGSeedFactoryBean();
        prngSeedFactoryBean.metaPrng = new Random();
    }

    @Test
    public void testGetObject() throws Exception {
        Integer seed = prngSeedFactoryBean.getObject();
        assertNotNull(seed);
    }

    @Test
    public void testGetObjectType() {
        assertEquals(Integer.class, prngSeedFactoryBean.getObjectType());
    }

    @Test
    public void testIsSingleton() {
        assertTrue(prngSeedFactoryBean.isSingleton());
    }

    @Test(expected = RuntimeException.class)
    public void testGetObjectThrowsRuntimeException() throws Exception {
        PRNGSeedFactoryBean factoryBean = new PRNGSeedFactoryBean();
        factoryBean.metaPrng = new Random();

        PRNGSeedFactoryBean spyFactoryBean = spy(factoryBean);
        doThrow(new UnknownHostException()).when(spyFactoryBean).getLocalHost();

        try {
            spyFactoryBean.getObject();
        } catch (RuntimeException e) {
            assertEquals(UnknownHostException.class, e.getCause().getClass());
            throw e;
        }
    }

    @Test
    public void testGetLocalHost() throws Exception {
        InetAddress address = prngSeedFactoryBean.getLocalHost();
        assertNotNull(address);
    }
}