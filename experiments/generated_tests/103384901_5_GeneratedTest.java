package lt.tokenmill.crawling.es;

import org.junit.Test;
import static org.junit.Assert.*;

public class ElasticConnectionTest {

    @Test
    public void testBuilder() {
        ElasticConnection.Builder builder = ElasticConnection.builder();
        assertNotNull(builder);
    }
}