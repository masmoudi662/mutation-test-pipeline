java
package sushi.xml.importer;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sushi.event.SushiEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;

public class XMLParserTest {

    @Test
    public void testGenerateEventFromXMLValidFile() throws Exception {
        String filePath = "src/test/resources/valid_event.xml";
        SushiEvent event = XMLParser.generateEventFromXML(filePath);
        assertNotNull(event);
    }

    @Test(expected = XMLParsingException.class)
    public void testGenerateEventFromXMLInvalidFile() throws Exception {
        String filePath = "src/test/resources/invalid_event.xml";
        XMLParser.generateEventFromXML(filePath);
    }

    @Test
    public void testReadXMLDocumentValidFile() throws Exception {
        String filePath = "src/test/resources/valid_event.xml";
        Document document = XMLParser.readXMLDocument(filePath);
        assertNotNull(document);
    }

    @Test(expected = XMLParsingException.class)
    public void testReadXMLDocumentNonExistingFile() throws Exception {
        String filePath = "src/test/resources/non_existing_file.xml";
        XMLParser.readXMLDocument(filePath);
    }

    @Test
    public void testGenerateEvent() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("event");
        doc.appendChild(rootElement);

        Element timestampElement = doc.createElement("timestamp");
        timestampElement.setTextContent(String.valueOf(new Date().getTime()));
        rootElement.appendChild(timestampElement);

        Element entryElement = doc.createElement("entry");
        entryElement.setAttribute("key", "attribute1");
        entryElement.setTextContent("value1");
        rootElement.appendChild(entryElement);

        SushiEvent event = XMLParser.generateEvent(doc, null);
        assertNotNull(event);
    }

    @Test
    public void testEvaluateXPath() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);

        Element element = doc.createElement("element");
        element.setTextContent("testValue");
        rootElement.appendChild(element);

        String xPathExpression = "/root/element/text()";

        String result = XMLParser.evaluateXPath(doc, xPathExpression);
        assertEquals("testValue", result);
    }

    @Test(expected = XMLParsingException.class)
    public void testEvaluateXPathInvalidXPath() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);

        String xPathExpression = "/invalidXPath";

        XMLParser.evaluateXPath(doc, xPathExpression);
    }

    @Test
    public void testGenerateEventEmptyDocument() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        SushiEvent event = XMLParser.generateEvent(doc, null);
        assertNull(event);
    }

    @Test
    public void testGenerateEventWithoutTimestamp() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("event");
        doc.appendChild(rootElement);

        Element entryElement = doc.createElement("entry");
        entryElement.setAttribute("key", "attribute1");
        entryElement.setTextContent("value1");
        rootElement.appendChild(entryElement);

        SushiEvent event = XMLParser.generateEvent(doc, null);
        assertNotNull(event);
    }

    @Test
    public void testExtractAllEventEntries() throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();

        Element rootElement = doc.createElement("event");
        doc.appendChild(rootElement);

        Element entryElement1 = doc.createElement("entry");
        entryElement1.setAttribute("key", "attribute1");
        entryElement1.setTextContent("value1");
        rootElement.appendChild(entryElement1);

        Element entryElement2 = doc.createElement("entry");
        entryElement2.setAttribute("key", "attribute2");
        entryElement2.setTextContent("value2");
        rootElement.appendChild(entryElement2);

        Map<String, Serializable> eventEntries = XMLParser.extractAllEventEntries(doc);
        assertEquals(2, eventEntries.size());
        assertEquals("value1", eventEntries.get("attribute1"));
        assertEquals("value2", eventEntries.get("attribute2"));
    }

}