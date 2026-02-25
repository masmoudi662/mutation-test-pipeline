java
package com.om.example.dvr.fixtures;

import com.om.example.dvr.domain.Program;
import com.om.example.util.DateUtil;
import org.junit.Test;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProgramGuideRowParserTest {

    @Test
    public void testParse() throws ParseException {
        ProgramGuideRowParser parser = new ProgramGuideRowParser();
        parser.parser = new ProgramParser();
        DateUtil dateUtil = new DateUtil();
        parser.parser.dateUtil = dateUtil;

        List<String> cells = Arrays.asList("2", "07:00 PM", "Program Name", "30");
        List<Program> programs = parser.parse(cells);

        assertNotNull(programs);
    }
}