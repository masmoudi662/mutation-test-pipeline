java
package org.apache.pig.piggybank.evaluation;

import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StitchTest {

    @Test
    public void testExecWithNullInput() throws IOException {
        Stitch stitch = new Stitch();
        assertNull(stitch.exec(null));
    }

    @Test
    public void testExecWithEmptyInput() throws IOException {
        Stitch stitch = new Stitch();
        Tuple input = TupleFactory.getInstance().newTuple();
        assertNull(stitch.exec(input));
    }

    @Test
    public void testExecWithSingleBag() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag = BagFactory.getInstance().newDefaultBag();
        bag.add(TupleFactory.getInstance().newTuple(List.of(1, "a")));
        Tuple input = TupleFactory.getInstance().newTuple(List.of(bag));
        DataBag result = stitch.exec(input);
        assertEquals(1, result.size());
    }

    @Test
    public void testExecWithMultipleBags() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag1 = BagFactory.getInstance().newDefaultBag();
        bag1.add(TupleFactory.getInstance().newTuple(List.of(1)));
        DataBag bag2 = BagFactory.getInstance().newDefaultBag();
        bag2.add(TupleFactory.getInstance().newTuple(List.of("a")));

        Tuple input = TupleFactory.getInstance().newTuple(List.of(bag1, bag2));
        DataBag result = stitch.exec(input);
        assertEquals(1, result.size());
        Tuple expectedTuple = TupleFactory.getInstance().newTuple(List.of(1, "a"));
        assertEquals(expectedTuple, result.iterator().next());
    }

    @Test
    public void testExecWithMultipleBagsMultipleTuples() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag1 = BagFactory.getInstance().newDefaultBag();
        bag1.add(TupleFactory.getInstance().newTuple(List.of(1)));
        bag1.add(TupleFactory.getInstance().newTuple(List.of(2)));
        DataBag bag2 = BagFactory.getInstance().newDefaultBag();
        bag2.add(TupleFactory.getInstance().newTuple(List.of("a")));
        bag2.add(TupleFactory.getInstance().newTuple(List.of("b")));

        Tuple input = TupleFactory.getInstance().newTuple(List.of(bag1, bag2));
        DataBag result = stitch.exec(input);
        assertEquals(2, result.size());

        List<Tuple> expectedTuples = new ArrayList<>();
        expectedTuples.add(TupleFactory.getInstance().newTuple(List.of(1, "a")));
        expectedTuples.add(TupleFactory.getInstance().newTuple(List.of(2, "b")));

        List<Tuple> actualTuples = new ArrayList<>();
        for(Tuple t : result) {
            actualTuples.add(t);
        }

        assertEquals(expectedTuples, actualTuples);
    }

    @Test(expected = org.apache.pig.backend.executionengine.ExecException.class)
    public void testExecWithInvalidInputType() throws IOException {
        Stitch stitch = new Stitch();
        Tuple input = TupleFactory.getInstance().newTuple(List.of("not a bag"));
        stitch.exec(input);
    }
}