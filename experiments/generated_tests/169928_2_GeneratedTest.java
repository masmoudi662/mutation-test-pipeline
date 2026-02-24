java
package com.om.example.dvr.fixtures;

import com.om.example.dvr.domain.Program;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProgramGuideRowParserTest {

    private ProgramGuideRowParser parser;

    @Before
    public void setUp() throws ParseException {
        parser = new ProgramGuideRowParser("2024-01-01", "08:00");
    }

    @Test
    public void testParseSingleProgram() {
        List<String> cells = new ArrayList<>();
        cells.add("4");
        cells.add("Program A (08:00-09:00)");

        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
        assertEquals(1, programs.size());
        assertEquals("Program A", programs.get(0).getTitle());
    }

    @Test
    public void testParseMultiplePrograms() {
        List<String> cells = new ArrayList<>();
        cells.add("7");
        cells.add("Program B (08:00-08:30)");
        cells.add("Program C (08:30-09:00)");

        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
        assertEquals(2, programs.size());
        assertEquals("Program B", programs.get(0).getTitle());
        assertEquals("Program C", programs.get(1).getTitle());
    }

    @Test
    public void testParseWithEmptyPrograms() {
        List<String> cells = new ArrayList<>();
        cells.add("9");

        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
        assertEquals(0, programs.size());
    }

    @Test
    public void testParseWithWhitespace() {
        List<String> cells = new ArrayList<>();
        cells.add("11");
        cells.add(" Program D (08:00-09:00) ");

        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
        assertEquals(1, programs.size());
        assertEquals("Program D", programs.get(0).getTitle());
    }

    @Test
    public void testParseChannelNumber() {
        List<String> cells = new ArrayList<>();
        cells.add("13");
        cells.add("Program E (08:00-09:00)");

        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
        assertEquals(1, programs.size());
        assertEquals(13, programs.get(0).getChannel());
    }
}