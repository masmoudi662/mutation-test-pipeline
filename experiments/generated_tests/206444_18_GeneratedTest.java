java
package org.apache.hive.hcatalog.templeton.tool;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class TempletonUtilsTest {

    @Test
    public void testIssetString() {
        assertTrue(TempletonUtils.isset("hello"));
        assertFalse(TempletonUtils.isset(""));
        assertFalse(TempletonUtils.isset(null));
    }

    @Test
    public void testIssetChar() {
        assertTrue(TempletonUtils.isset('a'));
        assertFalse(TempletonUtils.isset('\0'));
    }

    @Test
    public void testIssetArray() {
        String[] arr = {"a", "b"};
        assertTrue(TempletonUtils.isset(arr));
        String[] emptyArr = {};
        assertFalse(TempletonUtils.isset(emptyArr));
        assertFalse(TempletonUtils.isset((String[]) null));
    }

    @Test
    public void testIssetCollection() {
        Collection<String> col = new ArrayList<>();
        col.add("a");
        assertTrue(TempletonUtils.isset(col));
        Collection<String> emptyCol = new ArrayList<>();
        assertFalse(TempletonUtils.isset(emptyCol));
        assertFalse(TempletonUtils.isset((Collection<String>) null));
    }

    @Test
    public void testIssetMap() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "b");
        assertTrue(TempletonUtils.isset(map));
        Map<String, String> emptyMap = new HashMap<>();
        assertFalse(TempletonUtils.isset(emptyMap));
        assertFalse(TempletonUtils.isset((Map<String, String>) null));
    }

    @Test
    public void testExtractPercentCompleteJar() {
        String line = "14/11/05 18:39:39 INFO mapred.JobClient:  map 100% reduce 100%";
        String result = TempletonUtils.extractPercentComplete(line);
        assertEquals("map 100% reduce 100%", result);
    }

    @Test
    public void testExtractPercentCompletePig() {
        String line = "14/11/05 18:39:39 INFO mapred.JobClient:  90% complete";
        String result = TempletonUtils.extractPercentComplete(line);
        assertEquals("90% complete", result);
    }

    @Test
    public void testExtractPercentCompleteHive() {
        String line = "14/11/05 18:39:39 INFO mapred.JobClient:  map = 100%,  reduce = 100%";
        String result = TempletonUtils.extractPercentComplete(line);
        assertEquals("map 100% reduce 100%", result);
    }

    @Test
    public void testExtractChildJobIdJar() {
        String line = "14/11/05 18:39:39 INFO mapred.JobClient: Running job: job_201411051839_0001";
        String result = TempletonUtils.extractChildJobId(line);
        assertEquals("job_201411051839_0001", result);
    }
}