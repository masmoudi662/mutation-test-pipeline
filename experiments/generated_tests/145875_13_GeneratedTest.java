java
package com.dbdeploy.database;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class QueryStatementSplitterTest {

    @Test
    public void should_return_empty_list_if_no_statements() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter(";");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("");

        assertThat(statements, empty());
    }

    @Test
    public void should_split_on_semicolon() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter(";");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("select * from table1;\nselect * from table2;");

        assertThat(statements, contains("select * from table1", "select * from table2"));
    }

    @Test
    public void should_split_on_semicolon_with_windows_line_endings() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter(";");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("select * from table1;\r\nselect * from table2;");

        assertThat(statements, contains("select * from table1", "select * from table2"));
    }

    @Test
    public void should_split_on_go() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter("GO");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("select * from table1\nGO\nselect * from table2\nGO");

        assertThat(statements, contains("select * from table1", "select * from table2"));
    }

    @Test
    public void should_split_on_go_with_windows_line_endings() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter("GO");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("select * from table1\r\nGO\r\nselect * from table2\r\nGO");

        assertThat(statements, contains("select * from table1", "select * from table2"));
    }

    @Test
    public void should_split_on_slash() {
        QueryStatementSplitter splitter = new QueryStatementSplitter();
        splitter.setDelimiterType(DelimiterType.SUFFIX);
        splitter.setDelimiter("/");
        splitter.setLineEnding("\n");

        List<String> statements = splitter.split("select * from table1\n/\nselect * from table2\n/");

        assertThat(statements, contains("select * from table1", "select * from table2"));
    }
}