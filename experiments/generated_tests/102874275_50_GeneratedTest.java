java
package org.eclipse.rdf4j.query.algebra.evaluation.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

public class ValueComparatorTest {

	private static final ValueFactory vf = SimpleValueFactory.getInstance();
	private final ValueComparator comparator = new ValueComparator();

	@Test
	public void testNullEquals() {
		assertEquals(0, comparator.compare(null, null));
	}

	@Test
	public void testNullComparison() {
		Value v = vf.createLiteral("test");
		assertTrue(comparator.compare(null, v) < 0);
		assertTrue(comparator.compare(v, null) > 0);
	}

	@Test
	public void testBNodeComparison() {
		BNode b1 = vf.createBNode("b1");
		BNode b2 = vf.createBNode("b2");
		assertTrue(comparator.compare(b1, b2) != 0);
	}

	@Test
	public void testBNodeIRIComparison() {
		BNode b1 = vf.createBNode("b1");
		IRI iri = vf.createIRI("http://example.org/iri");
		assertTrue(comparator.compare(b1, iri) < 0);
		assertTrue(comparator.compare(iri, b1) > 0);
	}

	@Test
	public void testIRIComparison() {
		IRI iri1 = vf.createIRI("http://example.org/iri1");
		IRI iri2 = vf.createIRI("http://example.org/iri2");
		assertTrue(comparator.compare(iri1, iri2) != 0);
	}

	@Test
	public void testIRILiteralComparison() {
		IRI iri = vf.createIRI("http://example.org/iri");
		Literal lit = vf.createLiteral("test");
		assertTrue(comparator.compare(iri, lit) < 0);
		assertTrue(comparator.compare(lit, iri) > 0);
	}

	@Test
	public void testLiteralComparison() {
		Literal lit1 = vf.createLiteral("test1");
		Literal lit2 = vf.createLiteral("test2");
		assertTrue(comparator.compare(lit1, lit2) != 0);
	}

	@Test
	public void testLiteralTypedComparison() {
		Literal lit1 = vf.createLiteral("1", vf.createIRI("http://www.w3.org/2001/XMLSchema#integer"));
		Literal lit2 = vf.createLiteral("2", vf.createIRI("http://www.w3.org/2001/XMLSchema#integer"));
		assertTrue(comparator.compare(lit1, lit2) < 0);
	}

	@Test
	public void testLiteralUntypedTypedComparison() {
		Literal lit1 = vf.createLiteral("1");
		Literal lit2 = vf.createLiteral("2", vf.createIRI("http://www.w3.org/2001/XMLSchema#integer"));
		assertTrue(comparator.compare(lit1, lit2) < 0);
		assertTrue(comparator.compare(lit2, lit1) > 0);
	}

	@Test
	public void testLiteralLanguageComparison() {
		Literal lit1 = vf.createLiteral("test1", "en");
		Literal lit2 = vf.createLiteral("test2", "en");
		assertTrue(comparator.compare(lit1, lit2) != 0);
	}
}