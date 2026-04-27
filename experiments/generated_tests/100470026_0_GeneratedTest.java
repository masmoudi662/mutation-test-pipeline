java
package javaeetutorial.standalone.ejb;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StandaloneBeanTest {

    private static EJBContainer ejbContainer;
    private static Context context;

    @BeforeAll
    public static void setUp() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(EJBContainer.MODULES, new File("target/classes"));
        ejbContainer = EJBContainer.createEJBContainer(properties);
        context = ejbContainer.getContext();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (ejbContainer != null) {
            ejbContainer.close();
        }
    }

    @Test
    public void testReturnMessage() throws Exception {
        StandaloneBean bean = (StandaloneBean) context.lookup("java:global/classes/StandaloneBean");
        assertNotNull(bean);
        assertEquals("Hello, world!", bean.returnMessage());
    }

    @Test
    public void testLookup() throws Exception {
        StandaloneBean bean = (StandaloneBean) context.lookup("java:global/classes/StandaloneBean");
        assertNotNull(bean);
    }

    @Test
    public void testAnotherMessage() throws Exception {
        StandaloneBean bean = (StandaloneBean) context.lookup("java:global/classes/StandaloneBean");
        assertNotNull(bean);
        bean.setMessage("Another message");
        assertEquals("Another message", bean.returnMessage());
    }

    @Test
    public void testSetAndGetMessage() throws Exception {
        StandaloneBean bean = (StandaloneBean) context.lookup("java:global/classes/StandaloneBean");
        assertNotNull(bean);
        bean.setMessage("Test Message");
        assertEquals("Test Message", bean.returnMessage());
    }
}