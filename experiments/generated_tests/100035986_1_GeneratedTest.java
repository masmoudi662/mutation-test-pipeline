java
package de.tudarmstadt.ukp.argumentation.semeval2018;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScorerTest
{
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testReadLabelsFromFileValid() throws IOException
    {
        File tempFile = tempFolder.newFile("test.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("id1 0");
            pw.println("id2 1");
            pw.println("id3 0");
        }

        Map<String, Integer> labels = Scorer.readLabelsFromFile(tempFile);
        assertEquals(3, labels.size());
        assertEquals(Integer.valueOf(0), labels.get("id1"));
        assertEquals(Integer.valueOf(1), labels.get("id2"));
        assertEquals(Integer.valueOf(0), labels.get("id3"));
    }

    @Test
    public void testReadLabelsFromFileEmptyFile() throws IOException
    {
        File tempFile = tempFolder.newFile("empty.txt");
        assertTrue(tempFile.createNewFile());

        Map<String, Integer> labels = Scorer.readLabelsFromFile(tempFile);
        assertTrue(labels.isEmpty());
    }

    @Test
    public void testReadLabelsFromFileWithCommentsAndEmptyLines() throws IOException
    {
        File tempFile = tempFolder.newFile("test_comments.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("# This is a comment");
            pw.println("");
            pw.println("id1 0");
            pw.println("  id2 1  ");
            pw.println("id3 0 # another comment");
        }

        Map<String, Integer> labels = Scorer.readLabelsFromFile(tempFile);
        assertEquals(3, labels.size());
        assertEquals(Integer.valueOf(0), labels.get("id1"));
        assertEquals(Integer.valueOf(1), labels.get("id2"));
        assertEquals(Integer.valueOf(0), labels.get("id3"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadLabelsFromFileInvalidValue() throws IOException
    {
        File tempFile = tempFolder.newFile("test_invalid.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("id1 2");
        }
        Scorer.readLabelsFromFile(tempFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadLabelsFromFileInvalidFormat() throws IOException
    {
        File tempFile = tempFolder.newFile("test_invalid_format.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("id1");
        }
        Scorer.readLabelsFromFile(tempFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadLabelsFromFileInvalidNumberFormat() throws IOException
    {
        File tempFile = tempFolder.newFile("test_invalid_number.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("id1 abc");
        }
        Scorer.readLabelsFromFile(tempFile);
    }

    @Test
    public void testReadLabelsFromFileSorted() throws IOException
    {
        File tempFile = tempFolder.newFile("test_sorted.txt");
        try (PrintWriter pw = new PrintWriter(tempFile)) {
            pw.println("id3 0");
            pw.println("id1 1");
            pw.println("id2 0");
        }

        Map<String, Integer> labels = Scorer.readLabelsFromFile(tempFile);
        Object[] keys = labels.keySet().toArray();
        assertEquals("id1", keys[0]);
        assertEquals("id2", keys[1]);
        assertEquals("id3", keys[2]);
    }
}