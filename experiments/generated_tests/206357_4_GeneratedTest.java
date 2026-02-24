java
package org.apache.pig.piggybank.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.junit.Test;

public class TestStitch {

    @Test
    public void testExecNullInput() throws IOException {
        Stitch stitch = new Stitch();
        assertNull(stitch.exec(null));
    }

    @Test
    public void testExecEmptyInput() throws IOException {
        Stitch stitch = new Stitch();
        Tuple input = TupleFactory.getInstance().newTuple();
        assertNull(stitch.exec(input));
    }

    @Test
    public void testExecSingleBag() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag = BagFactory.getInstance().newDefaultBag();
        bag.add(TupleFactory.getInstance().newTuple(1));
        bag.add(TupleFactory.getInstance().newTuple(2));
        Tuple input = TupleFactory.getInstance().newTuple(1);
        input.set(0, bag);
        DataBag result = stitch.exec(input);
        assertEquals(bag.size(), result.size());
    }

    @Test
    public void testExecTwoBags() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag1 = BagFactory.getInstance().newDefaultBag();
        bag1.add(TupleFactory.getInstance().newTuple(1));
        bag1.add(TupleFactory.getInstance().newTuple(2));
        DataBag bag2 = BagFactory.getInstance().newDefaultBag();
        bag2.add(TupleFactory.getInstance().newTuple("a"));
        bag2.add(TupleFactory.getInstance().newTuple("b"));
        Tuple input = TupleFactory.getInstance().newTuple(2);
        input.set(0, bag1);
        input.set(1, bag2);
        DataBag result = stitch.exec(input);
        assertEquals(bag1.size(), result.size());
    }

    @Test
    public void testExecThreeBags() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag1 = BagFactory.getInstance().newDefaultBag();
        bag1.add(TupleFactory.getInstance().newTuple(1));
        bag1.add(TupleFactory.getInstance().newTuple(2));
        DataBag bag2 = BagFactory.getInstance().newDefaultBag();
        bag2.add(TupleFactory.getInstance().newTuple("a"));
        bag2.add(TupleFactory.getInstance().newTuple("b"));
        DataBag bag3 = BagFactory.getInstance().newDefaultBag();
        bag3.add(TupleFactory.getInstance().newTuple(1.0));
        bag3.add(TupleFactory.getInstance().newTuple(2.0));
        Tuple input = TupleFactory.getInstance().newTuple(3);
        input.set(0, bag1);
        input.set(1, bag2);
        input.set(2, bag3);
        DataBag result = stitch.exec(input);
        assertEquals(bag1.size(), result.size());
    }

    @Test
    public void testExecDifferentBagSizes() throws IOException {
        Stitch stitch = new Stitch();
        DataBag bag1 = BagFactory.getInstance().newDefaultBag();
        bag1.add(TupleFactory.getInstance().newTuple(1));
        bag1.add(TupleFactory.getInstance().newTuple(2));
        DataBag bag2 = BagFactory.getInstance().newDefaultBag();
        bag2.add(TupleFactory.getInstance().newTuple("a"));
        Tuple input = TupleFactory.getInstance().newTuple(2);
        input.set(0, bag1);
        input.set(1, bag2);
        DataBag result = stitch.exec(input);
        assertEquals(bag1.size(), result.size());
    }
}