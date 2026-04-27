java
package eu.planets_project.tools;

import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;

public class ToolSpecTest {

    @Test
    public void testFromInputStream() throws FileNotFoundException, JAXBException {
        String xmlContent = "<toolSpec><name>TestTool</name></toolSpec>";
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        ToolSpec toolSpec = ToolSpec.fromInputStream(inputStream);
        assertNotNull(toolSpec);
    }

    @Test(expected = JAXBException.class)
    public void testFromInputStreamInvalidXML() throws FileNotFoundException, JAXBException {
        String xmlContent = "<toolSpec><name>TestTool</name";
        InputStream inputStream = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8));
        ToolSpec.fromInputStream(inputStream);
    }

    @Test(expected = NullPointerException.class)
    public void testFromInputStreamNull() throws FileNotFoundException, JAXBException {
        ToolSpec.fromInputStream(null);
    }
}