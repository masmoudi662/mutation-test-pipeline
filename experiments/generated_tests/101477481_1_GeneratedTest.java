java
package com.weblyzard.sparql.tsv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.apache.jena.graph.Node;
import org.junit.Test;

public class TsvParserTest {

    @Test
    public void testParseNode_validIRI() {
        Optional<Node> node = TsvParser.parseNode("<http://example.com/resource>");
        assertTrue(node.isPresent());
        assertEquals("http://example.com/resource", node.get().getURI());
    }

    @Test
    public void testParseNode_validLiteral() {
        Optional<Node> node = TsvParser.parseNode("\"test literal\"");
        assertTrue(node.isPresent());
        assertEquals("test literal", node.get().getLiteralValue());
    }

    @Test
    public void testParseNode_invalidIRI() {
        Optional<Node> node = TsvParser.parseNode("<invalid iri>");
        assertTrue(node.isEmpty());
    }

    @Test
    public void testParseNode_emptyString() {
         Optional<Node> node = TsvParser.parseNode("");
         assertTrue(node.isPresent());
         assertTrue(node.get().isBlank());
    }

    @Test
    public void testParseNode_nullString() {
        Optional<Node> node = TsvParser.parseNode(null);
        assertTrue(node.isPresent());
        assertTrue(node.get().isBlank());
    }
    
    @Test
    public void testParseNode_prefixedName() {
    	Optional<Node> node = TsvParser.parseNode("ex:resource");
    	assertTrue(node.isPresent());
    }

    @Test
    public void testParseNode_plainLiteral() {
        Optional<Node> node = TsvParser.parseNode("plain literal");
        assertTrue(node.isPresent());
        assertEquals("plain literal", node.get().getLiteralValue());
    }
}