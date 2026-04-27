java
package de.learnlib.algorithm.dhc.mealy;

import de.learnlib.AccessSequenceTransformer;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.datastructure.observationtable.ObservationTable;
import de.learnlib.datastructure.observationtable.Row;
import de.learnlib.oracle.EquivalenceOracle;
import net.automatalib.automaton.transducer.CompactMealy;
import net.automatalib.word.Word;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class MealyDHCTest {

    private MealyDHC<String, String> mealyDHC;
    private MembershipOracle<String, Word<String>> membershipOracle;
    private CompactMealy<String, String> hypothesis;

    @BeforeMethod
    public void setUp() {
        membershipOracle = Mockito.mock(MembershipOracle.class);
        mealyDHC = new MealyDHC<>(membershipOracle);
        mealyDHC.startLearning();
    }

    @Test
    public void testGetHypothesisModel() {
        CompactMealy<String, String> hypothesis = mealyDHC.getHypothesisModel();
        Assert.assertNotNull(hypothesis);
    }
}